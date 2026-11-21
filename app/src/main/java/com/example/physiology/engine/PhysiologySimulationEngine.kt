package com.example.physiology.engine

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale

/**
 * Reusable, deterministic PhysiologySimulationEngine implementing full lifecycle control:
 * initialize(), start(), pause(), resume(), reset(), setSpeed(), setParameter(),
 * loadScenario(), step(), getState(), dispose().
 *
 * Separates high-frequency frame stepping (step()) from immutable UI snapshots cleanly.
 */
class PhysiologySimulationEngine {

    private val clock = SimulationClock()

    private val _state = MutableStateFlow(SimulationState())
    val state: StateFlow<SimulationState> = _state.asStateFlow()

    fun initialize(scenarioId: String = "cardio_normal_cycle", autoStart: Boolean = true) {
        clock.reset()
        clock.setSpeed(1.0f)
        loadScenario(scenarioId)
        if (autoStart) {
            start()
        } else {
            pause()
        }
    }

    fun start() {
        if (_state.value.isDisposed) return
        clock.start()
        _state.update { it.copy(isRunning = true) }
    }

    fun pause() {
        clock.pause()
        _state.update { it.copy(isRunning = false) }
    }

    fun resume() {
        if (_state.value.isDisposed) return
        clock.resume()
        _state.update { it.copy(isRunning = true) }
    }

    fun togglePlayPause() {
        if (_state.value.isRunning) pause() else resume()
    }

    fun reset() {
        val currentScenario = SimulationModelRegistry.findScenario(_state.value.activeScenarioId)
        val model = SimulationModelRegistry.getModel(currentScenario.systemId)
        val validatedParams = SimulationValidationService.validateParameterMap(
            model.parameterDefs,
            currentScenario.defaultParameters
        )
        clock.reset()
        val initialPhase = model.phases.first().id
        _state.update {
            it.copy(
                simulationTimeSec = 0f,
                elapsedRealTimeSec = 0f,
                cycleProgress = 0f,
                currentPhaseId = initialPhase,
                parameterValues = validatedParams,
                derivedOutputs = computeVerifiedTimingOutputs(currentScenario.systemId, validatedParams)
            )
        }
    }

    fun setSpeed(speed: Float) {
        clock.setSpeed(speed)
        _state.update { it.copy(playbackSpeed = clock.playbackSpeed) }
    }

    fun setParameter(parameterId: String, requestedValue: Float) {
        val current = _state.value
        val model = SimulationModelRegistry.getModel(current.activeSystem)
        val def = model.parameterDefs.firstOrNull { it.id == parameterId } ?: return
        val safeVal = SimulationValidationService.validateParameter(def, requestedValue)
        val updatedParams = current.parameterValues.toMutableMap().apply {
            this[parameterId] = safeVal
        }
        _state.update {
            it.copy(
                parameterValues = updatedParams,
                derivedOutputs = computeVerifiedTimingOutputs(current.activeSystem, updatedParams)
            )
        }
    }

    fun resetParametersToScenarioDefaults() {
        val scenario = SimulationModelRegistry.findScenario(_state.value.activeScenarioId)
        val model = SimulationModelRegistry.getModel(scenario.systemId)
        val defaults = SimulationValidationService.validateParameterMap(
            model.parameterDefs,
            scenario.defaultParameters
        )
        _state.update {
            it.copy(
                parameterValues = defaults,
                derivedOutputs = computeVerifiedTimingOutputs(scenario.systemId, defaults)
            )
        }
    }

    fun selectSystem(systemId: PhysiologySystemId, preserveStructureId: String? = null) {
        val defaultScenario = SimulationModelRegistry.getScenariosForSystem(systemId).first()
        loadScenario(defaultScenario.scenarioId, preserveStructureId)
    }

    fun loadScenario(scenarioId: String, preserveStructureId: String? = null) {
        val scenario = SimulationModelRegistry.findScenario(scenarioId)
        val model = SimulationModelRegistry.getModel(scenario.systemId)
        val validatedParams = SimulationValidationService.validateParameterMap(
            model.parameterDefs,
            scenario.defaultParameters
        )
        clock.reset()

        val circulationFilter = when (scenario.viewModeTag) {
            "pulmonary" -> CirculationFilterMode.PULMONARY_ONLY
            "systemic" -> CirculationFilterMode.SYSTEMIC_ONLY
            else -> CirculationFilterMode.BOTH
        }
        val showConduction = scenario.viewModeTag == "conduction"
        val zoomSynapse = scenario.viewModeTag == "synapse"

        _state.update { prev ->
            prev.copy(
                activeSystem = scenario.systemId,
                activeScenarioId = scenario.scenarioId,
                simulationTimeSec = 0f,
                elapsedRealTimeSec = 0f,
                cycleProgress = 0f,
                currentPhaseId = model.phases.first().id,
                parameterValues = validatedParams,
                derivedOutputs = computeVerifiedTimingOutputs(scenario.systemId, validatedParams),
                selectedOrganPartId = preserveStructureId ?: scenario.systemId.defaultAnatomyStructureId,
                circulationFilter = circulationFilter,
                showConductionOverlay = showConduction,
                zoomSynapse = zoomSynapse,
                isDisposed = false
            )
        }
    }

    fun selectOrganPart(partId: String?) {
        _state.update { it.copy(selectedOrganPartId = partId) }
    }

    fun setCirculationFilter(filter: CirculationFilterMode) {
        _state.update { it.copy(circulationFilter = filter) }
    }

    fun toggleConductionOverlay() {
        _state.update { it.copy(showConductionOverlay = !it.showConductionOverlay) }
    }

    fun toggleLabels() {
        _state.update { it.copy(showLabels = !it.showLabels) }
    }

    fun toggleZoomSynapse() {
        _state.update { it.copy(zoomSynapse = !it.zoomSynapse) }
    }

    fun setQualityMode(mode: RenderQualityMode) {
        _state.update { it.copy(qualityMode = mode) }
    }

    fun setReducedMotion(enabled: Boolean) {
        _state.update { it.copy(reducedMotion = enabled) }
    }

    /**
     * Deterministic simulation step invoked by the rendering ticker or automated tests.
     */
    fun step(rawDeltaSeconds: Float): SimulationState {
        val current = _state.value
        if (current.isDisposed || !current.isRunning) return current

        val effectiveDt = if (current.reducedMotion) rawDeltaSeconds * 0.35f else rawDeltaSeconds
        val appliedDelta = clock.tick(effectiveDt)
        if (appliedDelta <= 0f) return current

        val model = SimulationModelRegistry.getModel(current.activeSystem)
        val periodSec = resolveCurrentCyclePeriodSeconds(current.activeSystem, current.parameterValues)
        val nextProgress = (current.cycleProgress + appliedDelta / periodSec) % 1.0f
        val nextPhase = SimulationValidationService.resolvePhaseForProgress(model.phases, nextProgress)

        val updated = current.copy(
            simulationTimeSec = clock.simulationTimeSec,
            elapsedRealTimeSec = clock.elapsedRealTimeSec,
            cycleProgress = nextProgress,
            currentPhaseId = nextPhase.id
        )
        _state.value = updated
        return updated
    }

    fun seekCycleProgress(normalizedProgress: Float) {
        val clamped = normalizedProgress.coerceIn(0f, 0.999f)
        val model = SimulationModelRegistry.getModel(_state.value.activeSystem)
        val phase = SimulationValidationService.resolvePhaseForProgress(model.phases, clamped)
        _state.update {
            it.copy(cycleProgress = clamped, currentPhaseId = phase.id)
        }
    }

    fun getState(): SimulationState = _state.value

    fun dispose() {
        clock.pause()
        _state.update { it.copy(isRunning = false, isDisposed = true) }
    }

    private fun resolveCurrentCyclePeriodSeconds(
        systemId: PhysiologySystemId,
        params: Map<String, Float>
    ): Float = when (systemId) {
        PhysiologySystemId.CARDIOVASCULAR ->
            SimulationValidationService.computeCyclePeriodSeconds(params["heart_rate_bpm"] ?: 75f, 0.8f)
        PhysiologySystemId.RESPIRATORY ->
            SimulationValidationService.computeCyclePeriodSeconds(params["breathing_rate_bpm"] ?: 14f, 4.28f)
        PhysiologySystemId.NEURAL ->
            SimulationValidationService.computeCyclePeriodSeconds(params["neural_cycle_rate"] ?: 15f, 4.0f)
        PhysiologySystemId.RENAL ->
            SimulationValidationService.computeCyclePeriodSeconds(params["nephron_flow_tempo"] ?: 12f, 5.0f)
        PhysiologySystemId.DIGESTIVE ->
            SimulationValidationService.computeCyclePeriodSeconds(params["gi_journey_tempo"] ?: 10f, 6.0f)
    }

    private fun computeVerifiedTimingOutputs(
        systemId: PhysiologySystemId,
        params: Map<String, Float>
    ): Map<String, String> {
        val period = resolveCurrentCyclePeriodSeconds(systemId, params)
        val formattedPeriod = String.format(Locale.US, "%.2f s", period)
        return when (systemId) {
            PhysiologySystemId.CARDIOVASCULAR -> mapOf(
                "cycle_period" to formattedPeriod,
                "systole_window" to String.format(Locale.US, "%.2f s (illustrative)", period * 0.36f),
                "diastole_window" to String.format(Locale.US, "%.2f s (illustrative)", period * 0.64f)
            )
            PhysiologySystemId.RESPIRATORY -> mapOf(
                "breath_period" to formattedPeriod,
                "inspiration_window" to String.format(Locale.US, "%.2f s (illustrative)", period * 0.42f)
            )
            else -> mapOf("sequence_period" to formattedPeriod)
        }
    }
}

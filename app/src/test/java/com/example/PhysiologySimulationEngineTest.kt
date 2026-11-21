package com.example

import com.example.physiology.engine.CirculationFilterMode
import com.example.physiology.engine.PhysiologySimulationEngine
import com.example.physiology.engine.PhysiologySystemId
import com.example.physiology.engine.RenderQualityMode
import com.example.physiology.engine.ScientificValidityLevel
import com.example.physiology.engine.SimulationClock
import com.example.physiology.engine.SimulationModelRegistry
import com.example.physiology.engine.SimulationValidationService
import com.example.physiology.engine.ValveStatus
import com.example.physiology.learning.PhysiologyLearningSeed
import com.example.physiology.learning.QuizQuestionFormat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PhysiologySimulationEngineTest {

    @Test
    fun simulationClock_deterministicProgression_clamping_pause_resume_reset() {
        val clock = SimulationClock()
        clock.start()

        // Normal deterministic step
        val applied = clock.tick(0.02f)
        assertEquals(0.02f, applied, 0.0001f)
        assertEquals(0.02f, clock.simulationTimeSec, 0.0001f)

        // Large tab/background resume delta must clamp to MAX_SAFE_DELTA_SECONDS (0.05s)
        val clamped = clock.tick(4.5f)
        assertEquals(SimulationClock.MAX_SAFE_DELTA_SECONDS, clamped, 0.0001f)

        // Pause freezes simulation time completely
        clock.pause()
        val pausedDelta = clock.tick(0.03f)
        assertEquals(0f, pausedDelta, 0.0001f)
        val timeAtPause = clock.simulationTimeSec

        // Resume restores progression with playback speed multiplier
        clock.setSpeed(1.5f)
        clock.resume()
        val fastApplied = clock.tick(0.02f)
        assertEquals(0.03f, fastApplied, 0.0001f)
        assertEquals(timeAtPause + 0.03f, clock.simulationTimeSec, 0.0001f)

        // Reset restores clock to zero
        clock.reset()
        assertEquals(0f, clock.simulationTimeSec, 0.0001f)
        assertEquals(0f, clock.elapsedRealTimeSec, 0.0001f)
    }

    @Test
    fun validationService_clampsParameters_rejectsNaN_andComputesExactCyclePeriods() {
        val cardioModel = SimulationModelRegistry.getModel(PhysiologySystemId.CARDIOVASCULAR)
        val hrDef = cardioModel.parameterDefs.first { it.id == "heart_rate_bpm" }

        assertEquals(hrDef.minValue, SimulationValidationService.validateParameter(hrDef, -50f), 0.001f)
        assertEquals(hrDef.maxValue, SimulationValidationService.validateParameter(hrDef, 999f), 0.001f)
        assertEquals(hrDef.defaultValue, SimulationValidationService.validateParameter(hrDef, Float.NaN), 0.001f)
        assertEquals(hrDef.defaultValue, SimulationValidationService.validateParameter(hrDef, Float.POSITIVE_INFINITY), 0.001f)

        // Exact mathematical cycle periods (60 / BPM)
        assertEquals(1.00f, SimulationValidationService.computeCyclePeriodSeconds(60f, 0.8f), 0.0001f)
        assertEquals(0.80f, SimulationValidationService.computeCyclePeriodSeconds(75f, 0.8f), 0.0001f)
        assertEquals(0.60f, SimulationValidationService.computeCyclePeriodSeconds(100f, 0.8f), 0.0001f)
    }

    @Test
    fun cardiacValveStates_transitionAccuratelyAcrossAllFiveCyclePhases() {
        // Phase 1 (0.00..0.12): Atrial Systole -> AV open, Semilunar closed
        val atrialSystole = SimulationValidationService.evaluateCardiacValves(0.06f)
        assertEquals(ValveStatus.OPEN, atrialSystole.tricuspid)
        assertEquals(ValveStatus.OPEN, atrialSystole.mitral)
        assertEquals(ValveStatus.CLOSED, atrialSystole.pulmonary)
        assertEquals(ValveStatus.CLOSED, atrialSystole.aortic)

        // Phase 2 (0.12..0.20): Isovolumetric Contraction -> ALL 4 valves closed
        val isoContraction = SimulationValidationService.evaluateCardiacValves(0.16f)
        assertEquals(ValveStatus.CLOSED, isoContraction.tricuspid)
        assertEquals(ValveStatus.CLOSED, isoContraction.mitral)
        assertEquals(ValveStatus.CLOSED, isoContraction.pulmonary)
        assertEquals(ValveStatus.CLOSED, isoContraction.aortic)

        // Phase 3 (0.20..0.48): Ventricular Ejection -> AV closed, Semilunar open
        val ejection = SimulationValidationService.evaluateCardiacValves(0.34f)
        assertEquals(ValveStatus.CLOSED, ejection.tricuspid)
        assertEquals(ValveStatus.CLOSED, ejection.mitral)
        assertEquals(ValveStatus.OPEN, ejection.pulmonary)
        assertEquals(ValveStatus.OPEN, ejection.aortic)

        // Phase 4 (0.48..0.58): Isovolumetric Relaxation -> ALL 4 valves closed
        val isoRelax = SimulationValidationService.evaluateCardiacValves(0.53f)
        assertEquals(ValveStatus.CLOSED, isoRelax.tricuspid)
        assertEquals(ValveStatus.CLOSED, isoRelax.mitral)
        assertEquals(ValveStatus.CLOSED, isoRelax.pulmonary)
        assertEquals(ValveStatus.CLOSED, isoRelax.aortic)

        // Phase 5 (0.58..1.00): Passive Ventricular Filling -> AV open, Semilunar closed
        val filling = SimulationValidationService.evaluateCardiacValves(0.78f)
        assertEquals(ValveStatus.OPEN, filling.tricuspid)
        assertEquals(ValveStatus.OPEN, filling.mitral)
        assertEquals(ValveStatus.CLOSED, filling.pulmonary)
        assertEquals(ValveStatus.CLOSED, filling.aortic)
    }

    @Test
    fun simulationEngine_lifecycle_speedVsBpmSeparation_andDisposeSafety() {
        val engine = PhysiologySimulationEngine()
        engine.initialize(scenarioId = "cardio_normal_cycle", autoStart = true)

        assertTrue(engine.getState().isRunning)
        assertEquals(PhysiologySystemId.CARDIOVASCULAR, engine.getState().activeSystem)
        assertEquals(75f, engine.getState().parameterValues["heart_rate_bpm"] ?: 0f, 0.001f)
        assertEquals(1.0f, engine.getState().playbackSpeed, 0.001f)

        // Changing physiological BPM does NOT alter playback speed
        engine.setParameter("heart_rate_bpm", 100f)
        assertEquals(100f, engine.getState().parameterValues["heart_rate_bpm"] ?: 0f, 0.001f)
        assertEquals(1.0f, engine.getState().playbackSpeed, 0.001f)

        // Changing playback speed does NOT alter physiological BPM
        engine.setSpeed(0.5f)
        assertEquals(0.5f, engine.getState().playbackSpeed, 0.001f)
        assertEquals(100f, engine.getState().parameterValues["heart_rate_bpm"] ?: 0f, 0.001f)

        // Stepping advances cycle progress deterministically
        val beforeProgress = engine.getState().cycleProgress
        engine.step(0.05f)
        assertTrue(engine.getState().cycleProgress > beforeProgress)

        // Reset restores scenario defaults and time=0
        engine.reset()
        assertEquals(0f, engine.getState().simulationTimeSec, 0.0001f)
        assertEquals(0f, engine.getState().cycleProgress, 0.0001f)
        assertEquals(75f, engine.getState().parameterValues["heart_rate_bpm"] ?: 0f, 0.001f)

        // Dispose halts execution and prevents subsequent stepping
        engine.dispose()
        assertTrue(engine.getState().isDisposed)
        assertFalse(engine.getState().isRunning)
        val frozenTime = engine.getState().simulationTimeSec
        engine.step(0.05f)
        assertEquals(frozenTime, engine.getState().simulationTimeSec, 0.0001f)
    }

    @Test
    fun registry_allFiveSystems_15Scenarios_validityLevels_andLearningSeedIntegrity() {
        val engine = PhysiologySimulationEngine()
        engine.initialize(autoStart = true)

        // Verify all 5 organ systems have models, explanations, bilingual text, references, and honest validity levels
        assertEquals(5, PhysiologySystemId.entries.size)
        PhysiologySystemId.entries.forEach { systemId ->
            val model = SimulationModelRegistry.getModel(systemId)
            val explanation = SimulationModelRegistry.getExplanation(systemId)
            val scenarios = SimulationModelRegistry.getScenariosForSystem(systemId)

            assertTrue("System $systemId must have scenarios", scenarios.size >= 2)
            assertTrue("System $systemId must have phases", model.phases.size >= 3)
            assertTrue("System $systemId must have reference sources", explanation.sources.isNotEmpty())
            assertTrue("Bilingual EN explanation required", explanation.scientificDefinition.en.isNotBlank())
            assertTrue("Bilingual AR explanation required", explanation.scientificDefinition.ar.isNotBlank())
            assertTrue("Bilingual sequence of events required", explanation.sequenceOfEvents.isNotEmpty())
            // Scientific integrity: none of the procedural/educational models falsely claim Level A clinical validation
            assertNotEquals(ScientificValidityLevel.LEVEL_A_SCIENTIFICALLY_VALIDATED, model.validityLevel)
        }

        // Verify all 15 scenarios load cleanly and update overlays/modes
        assertEquals(15, SimulationModelRegistry.scenarios.size)
        SimulationModelRegistry.scenarios.forEach { scenario ->
            engine.loadScenario(scenario.scenarioId)
            val s = engine.getState()
            assertEquals(scenario.scenarioId, s.activeScenarioId)
            assertEquals(scenario.systemId, s.activeSystem)
        }

        // Verify specific interactive mode toggles
        engine.loadScenario("cardio_pulmonary_circuit")
        assertEquals(CirculationFilterMode.PULMONARY_ONLY, engine.getState().circulationFilter)
        engine.loadScenario("cardio_faster_cycle")
        assertTrue(engine.getState().showConductionOverlay)
        engine.loadScenario("neural_synaptic_transmission")
        assertTrue(engine.getState().zoomSynapse)

        // Quality & accessibility toggles
        engine.setQualityMode(RenderQualityMode.LOW)
        assertEquals(RenderQualityMode.LOW, engine.getState().qualityMode)
        engine.setReducedMotion(true)
        assertTrue(engine.getState().reducedMotion)

        // Guided lessons & 5 quiz formats verification
        val cardiacLesson = PhysiologyLearningSeed.guidedLessons.first { it.lessonId == "lesson_cardiac_cycle" }
        assertEquals(7, cardiacLesson.steps.size)

        val representedFormats = PhysiologyLearningSeed.quizQuestions.map { it.format }.toSet()
        QuizQuestionFormat.entries.forEach { format ->
            assertTrue("Quiz must include format $format", format in representedFormats)
        }
    }
}

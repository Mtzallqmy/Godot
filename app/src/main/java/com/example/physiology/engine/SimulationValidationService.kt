package com.example.physiology.engine

/**
 * Validates physiological model parameters, enforces documented safe domains,
 * and computes strictly verified timing relationships (no fabricated hemodynamic predictions).
 */
object SimulationValidationService {

    fun validateParameter(def: SimulationParameterDef, requestedValue: Float): Float {
        if (requestedValue.isNaN() || requestedValue.isInfinite()) {
            return def.defaultValue
        }
        return requestedValue.coerceIn(def.minValue, def.maxValue)
    }

    fun validateParameterMap(
        defs: List<SimulationParameterDef>,
        requested: Map<String, Float>
    ): Map<String, Float> {
        val result = mutableMapOf<String, Float>()
        for (def in defs) {
            val raw = requested[def.id] ?: def.defaultValue
            result[def.id] = validateParameter(def, raw)
        }
        return result
    }

    /**
     * Exact mathematical reciprocal relationship:
     * Cycle Duration (seconds) = 60 / Rate (cycles per minute).
     * Strictly validated arithmetic; makes no unsupported claims about stroke volume or BP.
     */
    fun computeCyclePeriodSeconds(ratePerMinute: Float, fallbackSeconds: Float): Float {
        if (ratePerMinute <= 0f || ratePerMinute.isNaN() || ratePerMinute.isInfinite()) {
            return fallbackSeconds
        }
        return 60f / ratePerMinute
    }

    /**
     * Maps normalized cardiac cycle progress (0f..1f) to documented mechanical valve states.
     * Phase timing:
     * 0.00..0.12 Atrial Systole              -> AV Open, Semilunar Closed
     * 0.12..0.20 Isovolumetric Contraction   -> All 4 Valves Closed
     * 0.20..0.48 Ventricular Ejection        -> AV Closed, Semilunar Open
     * 0.48..0.58 Isovolumetric Relaxation    -> All 4 Valves Closed
     * 0.58..1.00 Ventricular Filling         -> AV Open, Semilunar Closed
     */
    fun evaluateCardiacValves(cycleProgress: Float): CardiacValveStates {
        val p = ((cycleProgress % 1f) + 1f) % 1f
        return when {
            p < 0.12f -> CardiacValveStates(
                tricuspid = ValveStatus.OPEN,
                mitral = ValveStatus.OPEN,
                pulmonary = ValveStatus.CLOSED,
                aortic = ValveStatus.CLOSED
            )
            p < 0.20f -> CardiacValveStates(
                tricuspid = ValveStatus.CLOSED,
                mitral = ValveStatus.CLOSED,
                pulmonary = ValveStatus.CLOSED,
                aortic = ValveStatus.CLOSED
            )
            p < 0.48f -> CardiacValveStates(
                tricuspid = ValveStatus.CLOSED,
                mitral = ValveStatus.CLOSED,
                pulmonary = ValveStatus.OPEN,
                aortic = ValveStatus.OPEN
            )
            p < 0.58f -> CardiacValveStates(
                tricuspid = ValveStatus.CLOSED,
                mitral = ValveStatus.CLOSED,
                pulmonary = ValveStatus.CLOSED,
                aortic = ValveStatus.CLOSED
            )
            else -> CardiacValveStates(
                tricuspid = ValveStatus.OPEN,
                mitral = ValveStatus.OPEN,
                pulmonary = ValveStatus.CLOSED,
                aortic = ValveStatus.CLOSED
            )
        }
    }

    fun resolvePhaseForProgress(phases: List<SimulationPhaseDef>, progress: Float): SimulationPhaseDef {
        val p = ((progress % 1f) + 1f) % 1f
        return phases.firstOrNull { p >= it.startNormalized && p < it.endNormalized }
            ?: phases.last()
    }
}

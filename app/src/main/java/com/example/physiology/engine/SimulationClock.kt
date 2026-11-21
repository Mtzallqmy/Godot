package com.example.physiology.engine

/**
 * Deterministic simulation clock decoupled from UI frame lifecycles.
 * Clamps frame deltas (MAX_DELTA_SECONDS) so app backgrounding or tab suspension
 * never produces unstable numerical jumps.
 */
class SimulationClock(
    private val maxDeltaSeconds: Float = MAX_SAFE_DELTA_SECONDS
) {
    var simulationTimeSec: Float = 0f
        private set

    var elapsedRealTimeSec: Float = 0f
        private set

    var playbackSpeed: Float = 1.0f
        private set

    var isRunning: Boolean = false
        private set

    fun start() {
        isRunning = true
    }

    fun pause() {
        isRunning = false
    }

    fun resume() {
        isRunning = true
    }

    fun reset() {
        simulationTimeSec = 0f
        elapsedRealTimeSec = 0f
    }

    fun setSpeed(speed: Float) {
        if (speed.isNaN() || speed.isInfinite()) return
        playbackSpeed = speed.coerceIn(MIN_SPEED, MAX_SPEED)
    }

    /**
     * Advances simulation time deterministically.
     * Returns the scaled simulation delta actually applied (0f when paused).
     */
    fun tick(rawDeltaSeconds: Float): Float {
        if (!isRunning || rawDeltaSeconds <= 0f || rawDeltaSeconds.isNaN() || rawDeltaSeconds.isInfinite()) {
            return 0f
        }
        val clampedDelta = rawDeltaSeconds.coerceAtMost(maxDeltaSeconds)
        elapsedRealTimeSec += clampedDelta
        val scaledDelta = clampedDelta * playbackSpeed
        simulationTimeSec += scaledDelta
        return scaledDelta
    }

    companion object {
        const val MAX_SAFE_DELTA_SECONDS = 0.05f // 50ms ceiling prevents tab-resume jumps
        const val MIN_SPEED = 0.25f
        const val MAX_SPEED = 2.0f
    }
}

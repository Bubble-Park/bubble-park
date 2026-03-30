package fr.iutlens.mmi.demo.game

import kotlin.time.TimeSource

class Chrono(private val maxMs: Long) {
    private val start = TimeSource.Monotonic.markNow()
    private var pausedMs = 0L
    private var pauseStart: Long? = null

    val remainingMs: Long
        get() {
            val totalPaused = pausedMs + (pauseStart?.let {
                (TimeSource.Monotonic.markNow() - start).inWholeMilliseconds - it
            } ?: 0L)
            return (maxMs - (TimeSource.Monotonic.markNow() - start).inWholeMilliseconds + totalPaused)
                .coerceAtLeast(0L)
        }

    val value: Float get() = remainingMs / 1000f

    fun isFinished(): Boolean = remainingMs <= 0L

    fun pause() {
        if (pauseStart == null)
            pauseStart = (TimeSource.Monotonic.markNow() - start).inWholeMilliseconds
    }

    fun resume() {
        pauseStart?.let {
            pausedMs += (TimeSource.Monotonic.markNow() - start).inWholeMilliseconds - it
            pauseStart = null
        }
    }
}

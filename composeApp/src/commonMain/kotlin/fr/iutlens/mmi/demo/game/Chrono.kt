package fr.iutlens.mmi.demo.game

import kotlin.time.TimeSource

class Chrono(private val maxMs: Long) {
    private val start = TimeSource.Monotonic.markNow()

    val remainingMs: Long
        get() = (maxMs - (TimeSource.Monotonic.markNow() - start).inWholeMilliseconds)
                    .coerceAtLeast(0L)

    val value: Float get() = remainingMs / 1000f

    fun isFinished(): Boolean = remainingMs <= 0L
}

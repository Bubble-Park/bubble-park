package fr.iutlens.mmi.demo.game

import kotlin.time.TimeSource

class Chrono(private val maxMs: Long = 30_000L) {
    private val start = TimeSource.Monotonic.markNow()
    private var bonusMs = 0L

    val remainingMs: Long
        get() = (maxMs - (TimeSource.Monotonic.markNow() - start).inWholeMilliseconds + bonusMs)
                    .coerceAtLeast(0L)

    val value: Float get() = remainingMs / 1000f

    fun addTime(seconds: Float) {
        bonusMs += (seconds * 1000).toLong()
    }

    fun isFinished(): Boolean = remainingMs <= 0L
}

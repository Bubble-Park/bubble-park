package fr.iutlens.mmi.demo.game

class Chrono(private val maxMs: Long = 30_000L) {

    private var valueMs: Long = maxMs

    val value: Float get() = valueMs / 1000f

    fun update(deltaMs: Long = 20L) {
        valueMs = (valueMs - deltaMs).coerceAtLeast(0L)
    }

    fun addTime(seconds: Float) {
        valueMs = (valueMs + (seconds * 1000).toLong()).coerceAtMost(maxMs)
    }

    fun isFinished(): Boolean = valueMs <= 0L
}

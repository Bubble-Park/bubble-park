package fr.iutlens.mmi.demo.game

object SlowEffect {
    const val DURATION = 250 // 5s à 50fps (20ms/frame)
    var timer: Int = 0
    val isActive get() = timer > 0
    val speedMultiplier get() = if (isActive) 0f else 1.0f
    fun activate() { timer = DURATION }
    fun reset() { timer = 0 }
}

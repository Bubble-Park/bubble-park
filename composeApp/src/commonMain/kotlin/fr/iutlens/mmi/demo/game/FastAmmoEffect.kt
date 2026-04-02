package fr.iutlens.mmi.demo.game

object FastAmmoEffect {
    const val DURATION = 300 // 6s à 50fps (20ms/frame)
    var timer: Int = 0
    val isActive get() = timer > 0
    fun activate() { timer = DURATION }
    fun reset() { timer = 0 }
}

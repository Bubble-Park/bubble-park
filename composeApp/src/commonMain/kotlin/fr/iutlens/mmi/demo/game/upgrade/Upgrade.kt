package fr.iutlens.mmi.demo.game.upgrade

import fr.iutlens.mmi.demo.components.Player

abstract class Upgrade {
    abstract val id: String
    abstract val name: String
    abstract val description: String
    abstract val maxCount: Int

    var acquiredCount: Int = 0
    val isMaxed: Boolean get() = acquiredCount >= maxCount

    abstract fun apply(player: Player)

    fun reset() {
        acquiredCount = 0
    }
}

package fr.iutlens.mmi.demo.game.upgrade

import fr.iutlens.mmi.demo.components.Player
import org.jetbrains.compose.resources.DrawableResource

abstract class Upgrade {
    abstract val id: String
    abstract val name: String
    abstract val description: String
    abstract val maxCount: Int
    open val imageRes: DrawableResource? = null

    var acquiredCount: Int = 0
    val isMaxed: Boolean get() = acquiredCount >= maxCount

    open fun isAvailable(catalogue: List<Upgrade>): Boolean = true

    abstract fun apply(player: Player)

    /**
     * Réapplique les effets de stat persistants sur un nouveau player (sans effets secondaires comme heal).
     * Appelé après chaque changement de niveau pour restaurer les upgrades acquises.
     */
    open fun restoreStats(player: Player) {}

    fun reset() {
        acquiredCount = 0
    }
}

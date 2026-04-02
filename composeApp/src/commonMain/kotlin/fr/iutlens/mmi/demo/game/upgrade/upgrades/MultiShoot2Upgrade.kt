package fr.iutlens.mmi.demo.game.upgrade.upgrades

import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.components.Player
import fr.iutlens.mmi.demo.components.ShootMode
import fr.iutlens.mmi.demo.game.upgrade.Upgrade
import fr.iutlens.mmi.demo.upgrade_mutlishoot_2
import org.jetbrains.compose.resources.DrawableResource

class MultiShoot2Upgrade : Upgrade() {
    override val id = "multishoot_both"
    override val name = "Multi-Tir+"
    override val description = "Tire aussi dans les\ndirections perpendiculaires"
    override val maxCount = 1
    override val imageRes: DrawableResource = Res.drawable.upgrade_mutlishoot_2

    override fun isAvailable(catalogue: List<Upgrade>): Boolean =
        catalogue.filterIsInstance<MultiShoot1Upgrade>().firstOrNull()?.acquiredCount?.let { it >= 1 } ?: false

    override fun apply(player: Player) {
        player.shootMode = ShootMode.BOTH
    }

    override fun restoreStats(player: Player) {
        if (acquiredCount >= 1) player.shootMode = ShootMode.BOTH
    }
}

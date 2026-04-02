package fr.iutlens.mmi.demo.game.upgrade.upgrades

import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.components.Player
import fr.iutlens.mmi.demo.components.ShootMode
import fr.iutlens.mmi.demo.game.upgrade.Upgrade
import fr.iutlens.mmi.demo.upgrade_multishoot_1
import org.jetbrains.compose.resources.DrawableResource

class MultiShoot1Upgrade : Upgrade() {
    override val id = "multishoot_horizontal"
    override val name = "Multi-Tir"
    override val description = "Tire dans la direction\nopposée en même temps"
    override val maxCount = 1
    override val imageRes: DrawableResource = Res.drawable.upgrade_multishoot_1

    override fun apply(player: Player) {
        player.shootMode = ShootMode.HORIZONTAL
    }

    override fun restoreStats(player: Player) {
        if (acquiredCount >= 1) player.shootMode = ShootMode.HORIZONTAL
    }
}

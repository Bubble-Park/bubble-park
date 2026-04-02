package fr.iutlens.mmi.demo.game.upgrade.upgrades

import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.components.Player
import fr.iutlens.mmi.demo.game.upgrade.Upgrade
import fr.iutlens.mmi.demo.upgrade_cadence
import org.jetbrains.compose.resources.DrawableResource

class FireRateUpgrade : Upgrade() {
    override val id = "fire_rate"
    override val name = "Cadence"
    override val description = "Délai entre les tirs\nréduit de 20%"
    override val maxCount = 3
    override val imageRes: DrawableResource = Res.drawable.upgrade_cadence

    override fun apply(player: Player) {
        player.baseShootDelayMs = (player.baseShootDelayMs * 0.8).toLong()
    }

    override fun restoreStats(player: Player) {
        repeat(acquiredCount) {
            player.baseShootDelayMs = (player.baseShootDelayMs * 0.8).toLong()
        }
    }
}

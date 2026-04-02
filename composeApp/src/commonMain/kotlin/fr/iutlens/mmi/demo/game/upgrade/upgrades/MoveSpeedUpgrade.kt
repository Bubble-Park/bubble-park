package fr.iutlens.mmi.demo.game.upgrade.upgrades

import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.components.Player
import fr.iutlens.mmi.demo.game.upgrade.Upgrade
import fr.iutlens.mmi.demo.upgrade_faster
import org.jetbrains.compose.resources.DrawableResource

class MoveSpeedUpgrade : Upgrade() {
    override val id = "move_speed"
    override val name = "Vitesse"
    override val description = "Vitesse de déplacement\naugmentée de 20%"
    override val maxCount = 3
    override val imageRes: DrawableResource = Res.drawable.upgrade_faster

    override fun apply(player: Player) {
        player.moveSpeedMultiplier *= 1.2f
    }

    override fun restoreStats(player: Player) {
        repeat(acquiredCount) {
            player.moveSpeedMultiplier *= 1.2f
        }
    }
}

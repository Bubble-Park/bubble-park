package fr.iutlens.mmi.demo.game.upgrade.upgrades

import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.components.Player
import fr.iutlens.mmi.demo.game.upgrade.Upgrade
import fr.iutlens.mmi.demo.upgrade_invincible
import org.jetbrains.compose.resources.DrawableResource

class InvincibilityUpgrade : Upgrade() {
    override val id = "invincibility"
    override val name = "Invincible"
    override val description = "Durée d'invincibilité\naprès un hit x1.5"
    override val maxCount = 2
    override val imageRes: DrawableResource = Res.drawable.upgrade_invincible

    override fun apply(player: Player) {
        player.invincibilityMultiplier *= 1.5f
    }

    override fun restoreStats(player: Player) {
        repeat(acquiredCount) {
            player.invincibilityMultiplier *= 1.5f
        }
    }
}

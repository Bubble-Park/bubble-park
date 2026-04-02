package fr.iutlens.mmi.demo.game.upgrade.upgrades

import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.components.Player
import fr.iutlens.mmi.demo.game.upgrade.Upgrade
import fr.iutlens.mmi.demo.upgrade_life
import org.jetbrains.compose.resources.DrawableResource

class ExtraLifeUpgrade : Upgrade() {
    override val id = "extra_life"
    override val name = "Vie Supp."
    override val description = "+1 vie maximum\n(remplit le nouveau slot)"
    override val maxCount = 3
    override val imageRes: DrawableResource = Res.drawable.upgrade_life

    override fun apply(player: Player) {
        player.maxLife++
        player.heal()
    }
}

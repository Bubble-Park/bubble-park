package fr.iutlens.mmi.demo.game.upgrade.upgrades

import fr.iutlens.mmi.demo.components.Player
import fr.iutlens.mmi.demo.game.upgrade.Upgrade

class ExtraLifeUpgrade : Upgrade() {
    override val id = "extra_life"
    override val name = "Vie Supp."
    override val description = "+1 vie maximum\n(remplit le nouveau slot)"
    override val maxCount = 3

    override fun apply(player: Player) {
        player.maxLife++
        player.heal()
    }
}

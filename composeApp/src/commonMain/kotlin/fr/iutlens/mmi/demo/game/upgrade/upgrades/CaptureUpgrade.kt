package fr.iutlens.mmi.demo.game.upgrade.upgrades

import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.components.Player
import fr.iutlens.mmi.demo.game.upgrade.Upgrade
import fr.iutlens.mmi.demo.upgrade_capture
import org.jetbrains.compose.resources.DrawableResource

class CaptureUpgrade : Upgrade() {
    override val id = "capture"
    override val name = "Capture Tout"
    override val description = "Capture un dino\nen plus par bulle"
    override val maxCount = 3
    override val imageRes: DrawableResource = Res.drawable.upgrade_capture

    override fun apply(player: Player) {
        player.bulletMaxCaptures++
    }

    override fun restoreStats(player: Player) {
        player.bulletMaxCaptures += acquiredCount
    }
}

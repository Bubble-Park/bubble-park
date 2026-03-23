package fr.iutlens.mmi.demo.components.bonus

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.player_heart
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.game.sprite.squareWaveRotation

class LifeBonus(x: Float, y: Float) : BasicSprite(Res.drawable.player_heart, x, y) {

    var collected = false
    private var phase = 0f

    override fun paint(drawScope: DrawScope, elapsed: Long) {
        val w2 = spriteSheet.spriteWidth / 2
        val h2 = spriteSheet.spriteHeight / 2
        val rotation = squareWaveRotation(phase, 8f)
        drawScope.withTransform({
            translate(x, y)
            rotate(rotation, pivot = Offset.Zero)
        }) {
            spriteSheet.paint(this, 0, -w2, -h2)
        }
    }

    override fun update() {
        y += 2f
        phase += 0.08f
    }
}

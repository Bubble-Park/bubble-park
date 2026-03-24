package fr.iutlens.mmi.demo.components.bonus

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.slow_bonus
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import kotlin.math.sin

class SlowBonus(x: Float, y: Float) : BasicSprite(Res.drawable.slow_bonus, x, y) {

    var collected = false
    private var phase = 0f
    private val spawnX = x

    override fun paint(drawScope: DrawScope, elapsed: Long) {
        val w2 = spriteSheet.spriteWidth / 2
        val h2 = spriteSheet.spriteHeight / 2
        val rotation = if (sin(phase) > 0f) 8f else -8f
        drawScope.withTransform({
            translate(x, y)
            rotate(rotation, pivot = Offset.Zero)
            scale(0.25f, 0.25f)
        }) {
            spriteSheet.paint(this, 0, -w2, -h2)
        }
    }

    override fun update() {
        y += 0.8f
        phase += 0.15f
    }
}

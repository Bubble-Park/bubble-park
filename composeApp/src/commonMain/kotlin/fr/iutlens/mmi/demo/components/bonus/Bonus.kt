package fr.iutlens.mmi.demo.components.bonus

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import org.jetbrains.compose.resources.DrawableResource
import kotlin.math.sin

abstract class Bonus(res: DrawableResource, x: Float, y: Float, private val scale: Float = 0.5f) :
    BasicSprite(res, x, y) {

    var collected = false
    protected var phase = 0f

    abstract fun onCollect()

    open fun rotation(): Float = if (sin(phase) > 0f) 8f else -8f

    override fun paint(drawScope: DrawScope, elapsed: Long) {
        val w2 = spriteSheet.spriteWidth / 2
        val h2 = spriteSheet.spriteHeight / 2
        drawScope.withTransform({
            translate(x, y)
            scale(scale, scale, pivot = Offset.Zero)
            rotate(rotation(), pivot = Offset.Zero)
        }) {
            spriteSheet.paint(this, 0, -w2, -h2)
        }
    }

    override fun update() {
        y += 0.8f
        phase += 0.15f
    }
}

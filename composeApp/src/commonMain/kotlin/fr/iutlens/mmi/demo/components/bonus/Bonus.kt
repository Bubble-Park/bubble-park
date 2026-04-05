package fr.iutlens.mmi.demo.components.bonus

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import org.jetbrains.compose.resources.DrawableResource
import kotlin.math.sin

abstract class Bonus(res: DrawableResource, x: Float, y: Float, scale: Float = 0.5f) :
    BasicSprite(res, x, y) {

    private val baseScale = scale
    var collected = false
    var isCollecting = false
    private var collectTimer = 0
    private var currentScale = scale
    protected var phase = 0f

    abstract fun onCollect()

    fun startCollect() {
        isCollecting = true
        onCollect()
    }

    override val boundingBox get(): Rect {
        val w2 = spriteSheet.spriteWidth * baseScale / 2
        val h2 = spriteSheet.spriteHeight * baseScale / 2
        return Rect(x - w2, y - h2, x + w2, y + h2)
    }

    open fun rotation(): Float = if (sin(phase) > 0f) 8f else -8f

    override fun paint(drawScope: DrawScope, elapsed: Long) {
        val w2 = spriteSheet.spriteWidth / 2
        val h2 = spriteSheet.spriteHeight / 2
        drawScope.withTransform({
            translate(x, y)
            scale(currentScale, currentScale, pivot = Offset.Zero)
            rotate(rotation(), pivot = Offset.Zero)
        }) {
            spriteSheet.paint(this, 0, -w2, -h2)
        }
    }

    override fun update() {
        if (isCollecting) {
            collectTimer++
            val t = (collectTimer / 12f).coerceIn(0f, 1f)
            currentScale = baseScale * (1f + 0.6f * t)
            if (collectTimer >= 12) collected = true
            return
        }
        y += 3f
        phase += 0.15f
    }
}

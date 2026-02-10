package fr.iutlens.mmi.demo.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import fr.iutlens.mmi.demo.game.sprite.Sprite
import kotlin.math.cos
import kotlin.math.sin

class Bullet(
    var x: Float,
    var y: Float,
    angle: Double,
    val speed: Float = 90f
) : Sprite {

    var vx = (speed * cos(angle)).toFloat()
    var vy = (speed * sin(angle)).toFloat()
    val radius = 70f

    override val boundingBox: Rect
        get() = Rect(x - radius, y - radius, x + radius, y + radius)

    override fun paint(drawScope: DrawScope, elapsed: Long) {
        drawScope.drawCircle(
            color = Color.Yellow,
            radius = radius,
            center = Offset(x, y)
        )
    }

    override fun update() {
        x += vx
        y += vy
        
        // Friction
        vx *= 0.96f
        vy *= 0.99f
    }
    
    val isStopped: Boolean
        get() = (vx * vx + vy * vy) < 0.1f // Seuil bas pour arrêt
}

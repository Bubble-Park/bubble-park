package fr.iutlens.mmi.demo.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import fr.iutlens.mmi.demo.game.sprite.Sprite
import fr.iutlens.mmi.demo.game.sprite.TiledArea
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

class Bullet(
    var x: Float,
    var y: Float,
    angle: Double,
    val mapArea: TiledArea,
    val speed: Float = 85f,
    val collides: Boolean = false
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

    private fun isWall(x: Float, y: Float): Boolean {
        with(mapArea) {
            val i = floor(x / w).toInt()
            val j = floor(y / h).toInt()
            
            val code = tileMap.get(i, j) ?: 0
            
            return code != 0
        }
    }

    override fun update() {
        if (!collides) {
            x += vx
            y += vy
        } else {
            val nextX = x + vx
            
            // Rebond X
            val testX = nextX + (if (vx > 0) radius else -radius)
            if (isWall(testX, y)) {
                vx = -vx
            } else {
                x = nextX
            }

            val nextY = y + vy
            // Rebond Y
            val testY = nextY + (if (vy > 0) radius else -radius)
            if (isWall(x, testY)) {
                vy = -vy
            } else {
                y = nextY
            }
        }
        
        // Décélération
        vx *= 0.96f
        vy *= 0.99f
    }
    
    val isStopped: Boolean
        get() = (vx * vx + vy * vy) < 5f
}

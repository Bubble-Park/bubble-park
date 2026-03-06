package fr.iutlens.mmi.demo.game.sprite

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import fr.iutlens.mmi.demo.utils.DistanceMap
import fr.iutlens.mmi.demo.utils.MoveAction
import org.jetbrains.compose.resources.DrawableResource
import kotlin.math.floor

enum class EnemyBehavior {
    ATTACK,
    FLEE
}

class EnemySprite(
    res: DrawableResource,
    x: Float,
    y: Float,
    mapArea: TiledArea,
    val distanceMap: DistanceMap,
    val behavior: EnemyBehavior,
    var speed: Float = if (behavior == EnemyBehavior.FLEE) 32f else 22f
) : PhysicsSprite(res, x, y, mapArea, gravity = 8f, jumpForce = -110f) {

    var stunTimer = 0
    var jumpCooldown = 0
    val radius = 60f
    var isDead = false

    override val boundingBox: Rect
        get() = Rect(x - radius, y - radius, x + radius, y + radius)

    override fun paint(drawScope: DrawScope, elapsed: Long) {
        val color = if (behavior == EnemyBehavior.ATTACK) Color.Red else Color.Blue
        drawScope.drawCircle(
            color = color,
            radius = radius,
            center = androidx.compose.ui.geometry.Offset(x, y)
        )
    }

    override fun update() {
        if (isDead) return

        if (stunTimer > 0) {
            stunTimer--
            applyPhysics()
            return
        }

        if (jumpCooldown > 0) jumpCooldown--

        val i = floor(x / mapArea.w).toInt()
        val j = floor((y + radius - 1f) / mapArea.h).toInt()

        val move = when (behavior) {
            EnemyBehavior.ATTACK -> distanceMap.nextWithAction(i to j)
            EnemyBehavior.FLEE -> distanceMap.nextFleeWithAction(i to j)
        }

        val dirX = move?.dirX ?: 0f

        if (move != null && isOnGround && jumpCooldown <= 0) {
            when (move.action) {
                MoveAction.JUMP -> {
                    jump()
                    jumpCooldown = if (behavior == EnemyBehavior.FLEE) 30 else 50
                }
                MoveAction.FALL, MoveAction.WALK -> { /* gravité ou marche normale */ }
            }
        }

        moveX(dirX * speed, speed)
        applyPhysics()
        super.update()
    }
}

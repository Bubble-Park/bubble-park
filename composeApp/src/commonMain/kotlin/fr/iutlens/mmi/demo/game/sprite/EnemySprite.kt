package fr.iutlens.mmi.demo.game.sprite

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import fr.iutlens.mmi.demo.utils.DistanceMap
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
    var speed: Float = 22f
) : PhysicsSprite(res, x, y, mapArea, gravity = 8f, jumpForce = -110f) {

    var stunTimer = 0
    var jumpCooldown = 0
    val radius = 60f 
    var isDead = false

    override val boundingBox: Rect
        get() = Rect(x - radius, y - radius, x + radius, y + radius)

    override fun paint(drawScope: DrawScope, elapsed: Long) {
        val enemyColor = if (behavior == EnemyBehavior.ATTACK) Color.Red else Color.Blue
        
        drawScope.drawCircle(
            color = enemyColor,
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

        if (jumpCooldown > 0) {
            jumpCooldown--
        }

        val i = floor(x / mapArea.w).toInt()
        val j = floor((y + radius - 1f) / mapArea.h).toInt()

        val targetTile = if (behavior == EnemyBehavior.ATTACK) {
            distanceMap.next(i to j)
        } else {
            distanceMap.nextFlee(i to j)
        }

        val playerX = distanceMap.target.boundingBox.center.x

        var dirX = 0f
        if (targetTile != null) {
            if (targetTile.first < i) dirX = -1f
            else if (targetTile.first > i) dirX = 1f
            else if (targetTile.second > j) {
                val targetX = targetTile.first * mapArea.w + mapArea.w / 2f
                dirX = if (x < targetX) 1f else if (x > targetX) -1f else 0f
            }
        } else {
            if (behavior == EnemyBehavior.ATTACK) {
                dirX = if (x < playerX) 1f else if (x > playerX) -1f else 0f
            } else {
                dirX = if (x < playerX) -1f else if (x > playerX) 1f else 0f
            }
        }

        if (isOnGround && jumpCooldown <= 0) {
            var shouldJump = false

            if (targetTile != null && targetTile.second < j) {
                shouldJump = true
            }

            if (!shouldJump && dirX != 0f) {
                val nextX = x + dirX * mapArea.w * 0.6f
                if (isWall(nextX, y, checkPlatform = false)) {
                    val wallI = floor(nextX / mapArea.w).toInt()
                    val aboveCode = mapArea.tileMap.get(wallI, j - 1) ?: 0
                    if (aboveCode == 0) shouldJump = true
                }
            }

            if (shouldJump) {
                jump()
                jumpCooldown = 50
            }
        }

        val actualSpeed = dirX * speed
        moveX(actualSpeed, speed)
        applyPhysics()
        
        super.update()
    }
}

package fr.iutlens.mmi.demo.game.sprite

import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import org.jetbrains.compose.resources.DrawableResource

open class PhysicsSprite(
    res: DrawableResource,
    x: Float,
    y: Float,
    val mapArea: TiledArea,
    var gravity: Float = 1.5f,
    var jumpForce: Float = -25f
) : BasicSprite(res, x, y) {

    var vy = 0f
    var isOnGround = false

    protected fun isWall(x: Float, y: Float): Boolean {
        with(mapArea) {
            val i = floor(x / w).toInt()
            val j = floor(y / h).toInt()
            if (i !in 0..<tileMap.geometry.sizeX) return true
            if (j < 0) return false
            if (j >= tileMap.geometry.sizeY) return true
            return tileMap.get(i, j) != 0
        }
    }

    fun applyPhysics() {
        val h2 = spriteSheet.spriteHeight / 2f
        val w2 = spriteSheet.spriteWidth / 2f

        vy += gravity
        val nextY = y + vy

        if (vy > 0) { // Chute
            if (!isWall(x - w2/4, nextY + h2) && !isWall(x + w2/4, nextY + h2)) {
                y = nextY
                if (vy > gravity * 2) isOnGround = false
            } else { // Atterrissage
                isOnGround = true
                val tileJ = floor((nextY + h2) / mapArea.h).toInt()
                y = tileJ * mapArea.h - h2 - 0.1f
                vy = 0f
            }
        } else if (vy < 0) { // Montée
            if (!isWall(x, nextY - h2)) {
                y = nextY
            } else {
                vy = 0f
            }
        }
    }

    fun moveX(speed: Float) {
        val w2 = spriteSheet.spriteWidth / 2f
        // Limitation arbitraire de vitesse max (peut être surchargée ou paramétrée si besoin)
        val maxSpeed = 45f 
        val clampedSpeed = max(min(speed, maxSpeed), -maxSpeed)
        
        val nextX = x + clampedSpeed

        if (!isWall(nextX + if (clampedSpeed > 0) w2/2 else -w2/2, y)) {
            x = nextX
        }
    }

    fun jump() {
        if (isOnGround) {
            vy = jumpForce
            isOnGround = false
            y -= 2f
        }
    }
}

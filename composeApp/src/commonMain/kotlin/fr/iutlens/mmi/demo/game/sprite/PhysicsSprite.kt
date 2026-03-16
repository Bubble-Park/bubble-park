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

    var vy = 0f // vélocité Y
    var isOnGround = false // en contact avec le sol ?


    /**
     * Vérifie si il y a une mur à droite ou à gauche de la position (x, y)
     * @param x position sur l'axe X
     * @param y position sur l'axe Y
     * @return true si il y a une mur
     */
    protected fun isWall(x: Float, y: Float, checkPlatform: Boolean = true): Boolean {
        with(mapArea) {
            val i = floor(x / w).toInt()
            val j = floor(y / h).toInt()
            if (i !in 0..<tileMap.geometry.sizeX) return true
            if (j < 0) return false
            if (j >= tileMap.geometry.sizeY) return true
            
            val code = tileMap.get(i, j) ?: 0
            
            return when (code) {
                0 -> false
                1,2,3,4,5,6,7 -> checkPlatform
                8,9,10,11,12 -> false
                else -> true
            }
        }
    }

    /**
     * Applique la physique au sprite
     */
    fun applyPhysics() {
        val h2 = spriteSheet.spriteHeight / 2f

        vy += gravity
        val nextY = y + vy

        if (vy > 0) {
            if (!isWall(x, nextY + h2, checkPlatform = true)) {
                y = nextY
                isOnGround = false
            } else {
                isOnGround = true
                val tileJ = floor((nextY + h2) / mapArea.h).toInt()
                y = tileJ * mapArea.h - h2 - 0.1f
                vy = 0f
            }
        } else if (vy < 0) { // Montée
            if (!isWall(x, nextY - h2, checkPlatform = false)) {
                y = nextY
            } else {
                vy = 0f
            }
        }
    }

    /**
     * Déplace le sprite sur l'axe X
     * @param speed vitesse de déplacement
     * @param maxSpeed vitesse maximale
     */
    fun moveX(speed: Float, maxSpeed: Float) {
        val w2 = spriteSheet.spriteWidth / 2f
        val clampedSpeed = max(min(speed, maxSpeed), -maxSpeed)
        
        val nextX = x + clampedSpeed

        if (!isWall(nextX + if (clampedSpeed > 0) w2/2 else -w2/2, y, checkPlatform = false)) {
            x = nextX
        }
    }

    /**
     * Saut
     */
    fun jump() {
        if (isOnGround) {
            vy = jumpForce
            isOnGround = false
            y -= 2f
        }
    }
}

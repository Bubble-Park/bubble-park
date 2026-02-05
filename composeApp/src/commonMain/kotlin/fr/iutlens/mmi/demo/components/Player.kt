package fr.iutlens.mmi.demo.components

import fr.iutlens.mmi.demo.JoystickPosition
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.game.sprite.TiledArea
import org.jetbrains.compose.resources.DrawableResource
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class Player(
    res: DrawableResource,
    x: Float,
    y: Float,
    val mapArea: TiledArea,
    val joystickProvider: () -> JoystickPosition?
) : BasicSprite(res, x, y) {

    private var frameCounter = 0
    private var vy = 0f
    private val gravity = 1.5f
    private val jumpForce = -25f
    private var isOnGround = false
    private var jumpReleased = true

    private fun isWall(x: Float, y: Float): Boolean {
        with(mapArea) {
            val i = floor(x / w).toInt()
            val j = floor(y / h).toInt()
            if (i !in 0..<tileMap.geometry.sizeX) return true
            if (j < 0) return false
            if (j >= tileMap.geometry.sizeY) return true
            return tileMap.get(i, j) != 0
        }
    }

    override fun update() {
        super.update()
        val position = joystickProvider() ?: return
        val h2 = spriteSheet.spriteHeight / 2f
        val w2 = spriteSheet.spriteWidth / 2f

        vy += gravity
        val nextY = y + vy

        if (vy > 0) {
            if (!isWall(x - w2/4, nextY + h2) && !isWall(x + w2/4, nextY + h2)) {
                y = nextY
                if (vy > gravity * 2) isOnGround = false
            } else {
                isOnGround = true
                val tileJ = floor((nextY + h2) / mapArea.h).toInt()
                y = tileJ * mapArea.h - h2 - 0.1f
                vy = 0f
            }
        } else if (vy < 0) {
            if (!isWall(x, nextY - h2)) {
                y = nextY
            } else {
                vy = 0f
            }
        }

        if (position.y >= -0.2f) jumpReleased = true
        
        if (isOnGround && position.y < -0.6f && jumpReleased) {
            vy = jumpForce
            isOnGround = false
            jumpReleased = false
            y -= 2f
        }

        val speed = position.x * mapArea.w / 4
        val maxSpeed = 45f
        val nextX = x + max(min(speed, maxSpeed), -maxSpeed)

        if (!isWall(nextX + if (speed > 0) w2/2 else -w2/2, y)) {
            x = nextX
        }

        if (!isOnGround) {
            ndx = if (vy < 0) 21 else 23
        } else if (!position.isCentered && speed != 0f) {
            frameCounter++
            val animFrame = (frameCounter / 4) % 3
            if (speed > 0) {
                ndx = if (speed > maxSpeed/3) 10 + animFrame else 0 + animFrame
            } else {
                ndx = if (speed < -(maxSpeed/3)) 13 + animFrame else 3 + animFrame
            }
        } else {
            ndx = if (ndx in 13..15 || ndx == 3 || speed < 0) 13 else 10
        }
    }
}

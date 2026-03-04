package fr.iutlens.mmi.demo.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import fr.iutlens.mmi.demo.game.sprite.TiledArea
import org.jetbrains.compose.resources.DrawableResource
import kotlin.math.floor
import kotlin.random.Random

class Ino(
    res: DrawableResource,
    x: Float, y: Float,
    mapArea: TiledArea
) : Dino(res, x, y, mapArea) {

    enum class State { IDLE, MOVING }

    private var state = State.IDLE
    private var idleTimer = Random.nextInt(10, 100)
    private var dirX = 0f
    private var moveTarget = 0
    private var moveDone = 0f
    val speed = 20f

    override fun update() {
        if (isDead) return

        if (stunTimer > 0) {
            stunTimer--
            applyPhysics()
            return
        }

        if (jumpCooldown > 0) jumpCooldown--

        when (state) {
            State.IDLE -> {
                dirX = 0f
                idleTimer--
                if (idleTimer <= 0) startMoving()
            }
            State.MOVING -> {
                val i = floor(x / mapArea.w).toInt()
                val j = floor((y + radius - 1f) / mapArea.h).toInt()

                if (isBlockedInDir(dirX, i)) {
                    switchToIdle()
                } else {
                    moveDone += speed
                    if (moveDone >= moveTarget) {
                        switchToIdle()
                    } else if (isOnGround && jumpCooldown <= 0 && hasPlatformAbove(i, j) && Random.nextFloat() < 0.02f) {
                        jump()
                        jumpCooldown = 50
                    }
                }
            }
        }

        moveX(dirX * speed, speed)
        applyPhysics()
    }

    private fun switchToIdle() {
        state = State.IDLE
        dirX = 0f
        idleTimer = Random.nextInt(25, 101)
    }

    private fun startMoving() {
        val i = floor(x / mapArea.w).toInt()
        val leftBlocked = isBlockedInDir(-1f, i)
        val rightBlocked = isBlockedInDir(1f, i)

        dirX = when {
            leftBlocked && rightBlocked -> { switchToIdle(); return }
            leftBlocked -> 1f
            rightBlocked -> -1f
            else -> if (Random.nextBoolean()) 1f else -1f
        }

        moveTarget = Random.nextInt(2, 10) * mapArea.w
        moveDone = 0f
        state = State.MOVING
    }

    private fun hasPlatformAbove(i: Int, j: Int): Boolean {
        for (dy in 1..5) {
            val code = mapArea.tileMap.get(i, j - dy) ?: 0
            val above = mapArea.tileMap.get(i, j - dy - 1) ?: 0
            if (code in 1..3 && above == 0) return true
        }
        return false
    }

    private fun isBlockedInDir(dir: Float, i: Int): Boolean {
        val mapSizeX = mapArea.tileMap.geometry.sizeX
        if (dir < 0f && i <= 0) return true
        if (dir > 0f && i >= mapSizeX - 1) return true
        if (!isOnGround) return false
        val nextX = x + dir * mapArea.w * 0.6f
        return isWall(nextX, y, checkPlatform = false)
    }

    override fun paint(drawScope: DrawScope, elapsed: Long) {
        drawScope.drawCircle(
            color = Color.Green,
            radius = radius,
            center = Offset(x, y)
        )
    }
}

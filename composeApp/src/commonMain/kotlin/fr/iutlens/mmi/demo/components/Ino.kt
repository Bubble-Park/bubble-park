package fr.iutlens.mmi.demo.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import fr.iutlens.mmi.demo.game.sprite.TiledArea
import fr.iutlens.mmi.demo.utils.PlatformGraph
import org.jetbrains.compose.resources.DrawableResource
import kotlin.math.floor
import kotlin.random.Random

class Ino(
    res: DrawableResource,
    x: Float, y: Float,
    mapArea: TiledArea,
    graph: PlatformGraph
) : WalkingDino(res, x, y, mapArea, graph) {

    enum class State { IDLE, MOVING }

    private var state = State.IDLE

    override fun reset(x: Float, y: Float) {
        super.reset(x, y)
        state = State.IDLE
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

        when (state) {
            State.IDLE -> {
                dirX = 0f
                if (--idleTimer <= 0) {
                    startMoving()
                    if (currentPath != null) {
                        state = State.MOVING
                    } else {
                        idleTimer = Random.nextInt(25, 101)
                    }
                }
            }
            State.MOVING -> {
                val done = followPath(i, j)
                if (done) switchToIdle()
            }
        }

        moveX(dirX * speed, speed)
        applyPhysics()
    }

    private fun switchToIdle() {
        state = State.IDLE
        dirX = 0f
        currentPath = null
        idleTimer = Random.nextInt(25, 101)
    }

    override fun paint(drawScope: DrawScope, elapsed: Long) {
        drawScope.drawCircle(
            color = Color.Green,
            radius = radius,
            center = Offset(x, y)
        )
    }
}

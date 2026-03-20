package fr.iutlens.mmi.demo.components

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.IntSize
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.game.sprite.TiledArea
import org.jetbrains.compose.resources.DrawableResource
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

class Bullet(
    x: Float,
    y: Float,
    angle: Double,
    val mapArea: TiledArea,
    val speed: Float = 45f,
    val collides: Boolean = false,
    res: DrawableResource,
) : BasicSprite(res, x, y, ndx = 8) {

    var vx = (speed * cos(angle)).toFloat()
    var vy = (speed * sin(angle)).toFloat()
    val radius = 22f
    private val displaySize = IntSize(130, 130)

    private companion object {
        const val ENTRANCE_FIRST = 8
        const val ENTRANCE_COUNT = 4
        const val IDLE_FIRST = 0
        const val IDLE_COUNT = 3
        const val POP_FIRST = 4
        const val POP_COUNT = 4
        const val FRAME_DELAY = 3
        const val MIN_SPEED_SQUARED = 5f
    }

    private var frame = 0
    private var frameTimer = 0

    private enum class State { ENTRANCE, NORMAL, STOPPED }
    private var state = State.ENTRANCE

    private var popFrame = 0
    private var isPlayingPopAnimation = false
    private var hasPlayedPop = false

    init {
        ndx = ENTRANCE_FIRST
        frameTimer = FRAME_DELAY + 3
    }

    override val boundingBox: Rect
        get() = Rect(x - radius, y - radius, x + radius, y + radius)

    override fun paint(drawScope: DrawScope, elapsed: Long) {
        drawScope.withTransform({ translate(x, y) }) {
            spriteSheet.paint(this, ndx, -displaySize.width / 2, -displaySize.height / 2, displaySize)
        }
    }

    private fun isWall(x: Float, y: Float): Boolean {
        with(mapArea) {
            val i = floor(x / w).toInt()
            val j = floor(y / h).toInt()
            val code = tileMap.get(i, j) ?: 0
            return code in 1..7
        }
    }

    override fun update() {
        frameTimer++

        if (isPlayingPopAnimation) {
            if (frameTimer > FRAME_DELAY) {
                frameTimer = 0
                ndx = POP_FIRST + popFrame
                popFrame++

                if (popFrame >= POP_COUNT) {
                    isPlayingPopAnimation = false
                    state = State.STOPPED
                    ndx = POP_FIRST + POP_COUNT - 1
                }
            }
            return
        }

        if (frameTimer > FRAME_DELAY) {
            frameTimer = 0
            when (state) {
                State.ENTRANCE -> {
                    ndx = ENTRANCE_FIRST + frame
                    frame++
                    if (frame >= ENTRANCE_COUNT) {
                        state = State.NORMAL
                        frame = 0
                        ndx = IDLE_FIRST
                    }
                }
                State.NORMAL -> {
                    ndx = IDLE_FIRST + (frame % IDLE_COUNT)
                    frame++
                }
                State.STOPPED -> {
                }
            }
        }

        if (state != State.STOPPED) {
            if (!collides) {
                x += vx
                y += vy
            } else {
                val nextX = x + vx
                val testX = nextX + if (vx > 0) radius else -radius
                if (isWall(testX, y)) vx = -vx else x = nextX

                val nextY = y + vy
                val testY = nextY + if (vy > 0) radius else -radius
                if (isWall(x, testY)) vy = -vy else y = nextY
            }

            vx *= 0.96f
            vy *= 0.985f

            if (state == State.NORMAL && !hasPlayedPop && (vx * vx + vy * vy) < MIN_SPEED_SQUARED) {
                explode()
            }
        }
    }

    fun explode() {
        if (!isPlayingPopAnimation && !isStopped) {
            isPlayingPopAnimation = true
            hasPlayedPop = true
            popFrame = 0
            frameTimer = 0
            ndx = POP_FIRST
            vx = 0f
            vy = 0f
        }
    }

    val isStopped: Boolean
        get() = state == State.STOPPED
}

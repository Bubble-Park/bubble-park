package fr.iutlens.mmi.demo.components

import androidx.compose.ui.geometry.Rect
import fr.iutlens.mmi.demo.game.sprite.PhysicsSprite
import fr.iutlens.mmi.demo.game.sprite.TiledArea
import fr.iutlens.mmi.demo.utils.MoveAction
import fr.iutlens.mmi.demo.utils.PathPlan
import fr.iutlens.mmi.demo.utils.PlatformGraph
import org.jetbrains.compose.resources.DrawableResource
import kotlin.math.floor
import kotlin.random.Random

open class WalkingDino(
    res: DrawableResource,
    x: Float, y: Float,
    mapArea: TiledArea,
    val graph: PlatformGraph,
    gravity: Float = 4.5f,
    jumpForce: Float = -54f
) : PhysicsSprite(res, x, y, mapArea, gravity, jumpForce) {

    open val scoreValue: Int = 0

    var stunTimer = 0
    var jumpCooldown = 0
    val radius = 40f
    var isDead = false

    override val halfHeight get() = radius
    override val halfWidth get() = radius

    override val boundingBox: Rect
        get() = Rect(x - radius, y - radius, x + radius, y + radius)

    protected var idleTimer: Int = Random.nextInt(10, 100)
    protected var dirX: Float = 0f
    protected var currentPath: PathPlan? = null
    protected var savedPathDirX: Float = 0f
    protected var lastDirX: Float = 0f
    protected var stepTimeout: Int = 0

    companion object {
        const val STEP_TIMEOUT = 300
    }

    override fun reset(x: Float, y: Float) {
        super.reset(x, y)
        isDead = false
        stunTimer = 0
        jumpCooldown = 0
        idleTimer = Random.nextInt(10, 100)
        dirX = 0f
        currentPath = null
        savedPathDirX = 0f
        lastDirX = 0f
        stepTimeout = 0
    }

    protected fun startMoving() {
        val i = floor(x / mapArea.w).toInt()
        val j = floor((y + radius - 1f) / mapArea.h).toInt()
        val steps = graph.findPath(i to j, graph.randomStandable() ?: return)
        if (steps.isEmpty()) return
        currentPath = PathPlan(steps)
        stepTimeout = 0
    }

    protected fun followPath(i: Int, j: Int): Boolean {
        val path = currentPath ?: return true
        val step = path.current ?: return true

        val reachedStep = when (step.action) {
            MoveAction.WALK -> if (graph.isStandable(step.tile.first, step.tile.second)) {
                i == step.tile.first && j == step.tile.second
            } else {
                i == step.tile.first
            }
            MoveAction.FALL -> j >= step.tile.second
            MoveAction.JUMP -> j <= step.tile.second
        }

        when {
            reachedStep -> { path.advance(); stepTimeout = 0 }
            ++stepTimeout > STEP_TIMEOUT -> { currentPath = null; stepTimeout = 0; return true }
        }

        val activeStep = path.current
        if (activeStep != null && activeStep.action == MoveAction.WALK
            && graph.isStandable(activeStep.tile.first, activeStep.tile.second)) {
            val jumpAhead = path.peekNextJump()
            if (jumpAhead != null) {
                val (jumpIdx, jumpStep) = jumpAhead
                if (graph.getAction(i to j, jumpStep.tile) == MoveAction.JUMP) {
                    path.skipTo(jumpIdx)
                }
            }
        }

        val effectiveStep = path.current
        if (effectiveStep != null) {
            if (effectiveStep.dirX != 0f) {
                lastDirX = effectiveStep.dirX
                savedPathDirX = effectiveStep.dirX
            }
            dirX = if (effectiveStep.dirX != 0f) effectiveStep.dirX else lastDirX
            if (effectiveStep.action == MoveAction.JUMP && jumpCooldown <= 0 && j > 0) {
                jump()
                jumpCooldown = 50
            }
        } else {
            dirX = lastDirX
        }

        return path.isDone
    }
}

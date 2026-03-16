package fr.iutlens.mmi.demo.components

import fr.iutlens.mmi.demo.game.sprite.TiledArea
import fr.iutlens.mmi.demo.utils.MoveAction
import fr.iutlens.mmi.demo.utils.PathPlan
import fr.iutlens.mmi.demo.utils.PlatformGraph
import org.jetbrains.compose.resources.DrawableResource
import kotlin.math.floor
import kotlin.random.Random

abstract class WalkingDino(
    res: DrawableResource,
    x: Float, y: Float,
    mapArea: TiledArea,
    val graph: PlatformGraph,
    gravity: Float = 4.5f,
    jumpForce: Float = -60f
) : Dino(res, x, y, mapArea, gravity, jumpForce) {

    protected var idleTimer: Int = Random.nextInt(10, 100)
    protected var dirX: Float = 0f
    protected var currentPath: PathPlan? = null
    protected var fleeingDirX: Float = 0f
    protected var stepTimeout: Int = 0
    protected val speed: Float = 11f

    companion object {
        const val STEP_TIMEOUT = 300
    }

    override fun reset(x: Float, y: Float) {
        super.reset(x, y)
        dirX = 0f
        currentPath = null
        stepTimeout = 0
        idleTimer = Random.nextInt(10, 100)
        fleeingDirX = 0f
    }

    protected fun hasPlatformAbove(i: Int, j: Int): Boolean {
        for (dy in 1..5) {
            val code = mapArea.tileMap.get(i, j - dy) ?: 0
            val above = mapArea.tileMap.get(i, j - dy - 1) ?: 0
            if (code in 1..7 && above !in 1..7) return true
        }
        return false
    }

    protected fun isBlockedInDir(dir: Float, i: Int): Boolean {
        val mapSizeX = mapArea.tileMap.geometry.sizeX
        if (dir < 0f && i <= 0) return true
        if (dir > 0f && i >= mapSizeX - 1) return true
        if (!isOnGround) return false
        val nextX = x + dir * mapArea.w * 0.6f
        return isWall(nextX, y, checkPlatform = false)
    }

    protected open fun startMoving() {
        val i = floor(x / mapArea.w).toInt()
        val j = floor((y + radius - 1f) / mapArea.h).toInt()
        val from = i to j
        val to = graph.randomStandable() ?: return
        val steps = graph.findPath(from, to)
        if (steps.isEmpty()) return
        currentPath = PathPlan(steps)
        stepTimeout = 0
    }

    /**
     * Suit le PathPlan courant pas à pas. Met à jour dirX et déclenche les sauts.
     * Retourne true si le chemin est terminé ou en timeout.
     */
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

        // Anticipation du saut : si le step courant est WALK standable et qu'un JUMP
        // est prévu plus loin, sauter dès qu'on est en position.
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
            if (effectiveStep.dirX != 0f) fleeingDirX = effectiveStep.dirX
            dirX = if (effectiveStep.dirX != 0f) effectiveStep.dirX else fleeingDirX
            if (effectiveStep.action == MoveAction.JUMP && jumpCooldown <= 0 && j > 0) {
                jump()
                jumpCooldown = 50
            }
        } else {
            dirX = fleeingDirX
        }

        return path.isDone
    }
}

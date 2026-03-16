package fr.iutlens.mmi.demo.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import fr.iutlens.mmi.demo.game.sprite.TiledArea
import fr.iutlens.mmi.demo.utils.DistanceMap
import fr.iutlens.mmi.demo.utils.MoveAction
import fr.iutlens.mmi.demo.utils.PathPlan
import fr.iutlens.mmi.demo.utils.PlatformGraph
import org.jetbrains.compose.resources.DrawableResource
import kotlin.math.floor

class Attack(
    res: DrawableResource,
    x: Float, y: Float,
    mapArea: TiledArea,
    val distanceMap: DistanceMap,
    val graph: PlatformGraph
) : Dino(res, x, y, mapArea) {

    companion object {
        const val PATH_REFRESH_INTERVAL = 25
        const val STEP_TIMEOUT = 300
    }

    val speed = 15f

    override val boundingBox: Rect
        get() = Rect(x - mapArea.w, y - mapArea.h, x + mapArea.w, y + mapArea.h)

    private var currentPath: PathPlan? = null
    private var pathRefreshTimer: Int = 0
    private var stepTimeout: Int = 0
    private var dirX: Float = 0f
    private var lastDirX: Float = 0f

    override fun reset(x: Float, y: Float) {
        super.reset(x, y)
        currentPath = null
        pathRefreshTimer = 0
        stepTimeout = 0
        dirX = 0f
        lastDirX = 0f
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

        if (--pathRefreshTimer <= 0) {
            recomputeAttackPath(i, j)
            pathRefreshTimer = PATH_REFRESH_INTERVAL
        }

        if (!isOnGround) {
            dirX = lastDirX
        } else {
            val path = currentPath
            if (path == null || path.isDone) {
                recomputeAttackPath(i, j)
            } else {
                val step = path.current!!
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
                    reachedStep -> {
                        path.advance()
                        stepTimeout = 0
                    }
                    ++stepTimeout > STEP_TIMEOUT -> {
                        currentPath = null
                        stepTimeout = 0
                    }
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
                    if (effectiveStep.dirX != 0f) lastDirX = effectiveStep.dirX
                    dirX = if (effectiveStep.dirX != 0f) effectiveStep.dirX else lastDirX
                    if (effectiveStep.action == MoveAction.JUMP && jumpCooldown <= 0 && j > 0) {
                        jump()
                        jumpCooldown = 50
                    }
                } else {
                    dirX = lastDirX
                }
            }
        }

        moveX(dirX * speed, speed)
        applyPhysics()
    }

    private fun recomputeAttackPath(i: Int, j: Int) {
        val playerTile = distanceMap.targetTile
        val steps = graph.findPath(i to j, playerTile)
        currentPath = if (steps.isNotEmpty()) PathPlan(steps) else null
        stepTimeout = 0
    }

    override fun paint(drawScope: DrawScope, elapsed: Long) {
        drawScope.drawCircle(
            color = Color.Red,
            radius = mapArea.w.toFloat(),
            center = Offset(x, y)
        )
    }
}

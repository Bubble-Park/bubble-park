package fr.iutlens.mmi.demo.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import fr.iutlens.mmi.demo.game.sprite.TiledArea
import fr.iutlens.mmi.demo.utils.DistanceMap
import fr.iutlens.mmi.demo.utils.MoveAction
import fr.iutlens.mmi.demo.utils.PathPlan
import fr.iutlens.mmi.demo.utils.PlatformGraph
import org.jetbrains.compose.resources.DrawableResource
import kotlin.math.floor
import kotlin.random.Random

class Flee(
    res: DrawableResource,
    x: Float, y: Float,
    mapArea: TiledArea,
    val distanceMap: DistanceMap,
    graph: PlatformGraph
) : WalkingDino(res, x, y, mapArea, graph) {

    enum class State { IDLE, MOVING, FLEEING }

    companion object {
        const val FLEE_TRIGGER_TILES = 6
        const val FLEE_RELEASE_TILES = 15
        const val DEBUG_FLEE = true
    }

    val fleeSpeed = 25f

    private var state = State.IDLE

    override fun reset(x: Float, y: Float) {
        super.reset(x, y)
        state = State.IDLE
    }

    override fun update() {
        if (isDead) return

        if (stunTimer > 0) {
            stunTimer--
            currentPath = null
            applyPhysics()
            return
        }

        if (jumpCooldown > 0) jumpCooldown--

        val i = floor(x / mapArea.w).toInt()
        val j = floor((y + radius - 1f) / mapArea.h).toInt()
        val distToPlayer = distanceMap[i, j]

        if (distToPlayer != null && distToPlayer <= FLEE_TRIGGER_TILES && state != State.FLEEING) {
            state = State.FLEEING
            currentPath = null
        } else if ((distToPlayer == null || distToPlayer > FLEE_RELEASE_TILES) && state == State.FLEEING) {
            switchToIdle()
        }

        when (state) {
            State.FLEEING -> {
                if (!isOnGround) {
                    dirX = fleeingDirX
                } else {
                    val path = currentPath
                    if (path == null || path.isDone) {
                        recomputeFleePath(i, j)
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
                            if (effectiveStep.dirX != 0f) fleeingDirX = effectiveStep.dirX
                            dirX = if (effectiveStep.dirX != 0f) effectiveStep.dirX else fleeingDirX
                            if (effectiveStep.action == MoveAction.JUMP && jumpCooldown <= 0 && j > 0) {
                                jump()
                                jumpCooldown = 50
                            }
                        } else {
                            dirX = fleeingDirX
                        }
                    }
                }
            }
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

        val currentSpeed = if (state == State.FLEEING) fleeSpeed else speed
        moveX(dirX * currentSpeed, currentSpeed)
        applyPhysics()
    }

    private fun recomputeFleePath(i: Int, j: Int) {
        val myTile = i to j
        val playerTile = distanceMap.targetTile
        val pathSteps = graph.findFleePathTo(myTile, playerTile)
        currentPath = if (pathSteps.isNotEmpty()) PathPlan(pathSteps) else null
        stepTimeout = 0
    }

    private fun switchToIdle() {
        state = State.IDLE
        dirX = 0f
        currentPath = null
        idleTimer = Random.nextInt(25, 101)
    }

    override fun paint(drawScope: DrawScope, elapsed: Long) {
        drawScope.drawCircle(
            color = Color.Yellow,
            radius = radius,
            center = Offset(x, y)
        )
    }
}

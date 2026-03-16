package fr.iutlens.mmi.demo.components

import androidx.compose.ui.geometry.Rect
import fr.iutlens.mmi.demo.game.sprite.PhysicsSprite
import fr.iutlens.mmi.demo.game.sprite.TiledArea
import fr.iutlens.mmi.demo.utils.DistanceMap
import fr.iutlens.mmi.demo.utils.MoveAction
import fr.iutlens.mmi.demo.utils.PathPlan
import fr.iutlens.mmi.demo.utils.PlatformGraph
import org.jetbrains.compose.resources.DrawableResource
import kotlin.math.floor
import kotlin.random.Random

enum class DinoBehavior { INO, FLEE, ATTACK }

class Dino(
    res: DrawableResource,
    x: Float, y: Float,
    mapArea: TiledArea,
    val behavior: DinoBehavior,
    val graph: PlatformGraph,
    val distanceMap: DistanceMap? = null,
    gravity: Float = 8f,
    jumpForce: Float = -110f,
) : PhysicsSprite(res, x, y, mapArea, gravity, jumpForce) {

    val scoreValue: Int = when (behavior) {
        DinoBehavior.INO -> 1
        DinoBehavior.FLEE -> 2
        DinoBehavior.ATTACK -> 3
    }

    var stunTimer = 0
    var jumpCooldown = 0
    val radius = 100f
    var isDead = false

    override val boundingBox: Rect
        get() = Rect(x - radius, y - radius, x + radius, y + radius)

    private var idleTimer: Int = Random.nextInt(10, 100)
    private var dirX: Float = 0f
    private var currentPath: PathPlan? = null
    private var lastDirX: Float = 0f
    private var stepTimeout: Int = 0

    private val walkSpeed = 20f
    private val fleeSpeed = 40f
    private val attackSpeed = 35f

    private enum class InoState { IDLE, MOVING }
    private var inoState = InoState.IDLE

    private enum class FleeState { IDLE, MOVING, FLEEING }
    private var fleeState = FleeState.IDLE

    private var pathRefreshTimer: Int = 0

    companion object {
        const val STEP_TIMEOUT = 300
        const val FLEE_TRIGGER_TILES = 6
        const val FLEE_RELEASE_TILES = 15
        const val PATH_REFRESH_INTERVAL = 25
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

        when (behavior) {
            DinoBehavior.INO -> updateIno(i, j)
            DinoBehavior.FLEE -> updateFlee(i, j)
            DinoBehavior.ATTACK -> updateAttack(i, j)
        }
    }

    private fun updateIno(i: Int, j: Int) {
        when (inoState) {
            InoState.IDLE -> {
                dirX = 0f
                if (--idleTimer <= 0) {
                    startMoving()
                    if (currentPath != null) inoState = InoState.MOVING
                    else idleTimer = Random.nextInt(25, 101)
                }
            }
            InoState.MOVING -> {
                if (followPath(i, j)) {
                    inoState = InoState.IDLE
                    dirX = 0f
                    currentPath = null
                    idleTimer = Random.nextInt(25, 101)
                }
            }
        }
        moveX(dirX * walkSpeed, walkSpeed)
        applyPhysics()
    }

    private fun updateFlee(i: Int, j: Int) {
        val dm = distanceMap ?: run { applyPhysics(); return }
        val distToPlayer = dm[i, j]

        if (distToPlayer != null && distToPlayer <= FLEE_TRIGGER_TILES && fleeState != FleeState.FLEEING) {
            fleeState = FleeState.FLEEING
            currentPath = null
        } else if ((distToPlayer == null || distToPlayer > FLEE_RELEASE_TILES) && fleeState == FleeState.FLEEING) {
            fleeState = FleeState.IDLE
            dirX = 0f
            currentPath = null
            idleTimer = Random.nextInt(25, 101)
        }

        when (fleeState) {
            FleeState.FLEEING -> {
                if (!isOnGround) {
                    dirX = lastDirX
                } else {
                    val path = currentPath
                    if (path == null || path.isDone) {
                        val steps = graph.findFleePathTo(i to j, dm.targetTile)
                        currentPath = if (steps.isNotEmpty()) PathPlan(steps) else null
                        stepTimeout = 0
                    } else {
                        followPath(i, j)
                    }
                }
            }
            FleeState.IDLE -> {
                dirX = 0f
                if (--idleTimer <= 0) {
                    startMoving()
                    if (currentPath != null) fleeState = FleeState.MOVING
                    else idleTimer = Random.nextInt(25, 101)
                }
            }
            FleeState.MOVING -> {
                if (followPath(i, j)) {
                    fleeState = FleeState.IDLE
                    dirX = 0f
                    currentPath = null
                    idleTimer = Random.nextInt(25, 101)
                }
            }
        }

        val speed = if (fleeState == FleeState.FLEEING) fleeSpeed else walkSpeed
        moveX(dirX * speed, speed)
        applyPhysics()
    }

    private fun updateAttack(i: Int, j: Int) {
        val dm = distanceMap ?: run { applyPhysics(); return }

        if (--pathRefreshTimer <= 0) {
            val steps = graph.findPath(i to j, dm.targetTile)
            currentPath = if (steps.isNotEmpty()) PathPlan(steps) else null
            stepTimeout = 0
            pathRefreshTimer = PATH_REFRESH_INTERVAL
        }

        if (!isOnGround) {
            dirX = lastDirX
        } else {
            val path = currentPath
            if (path == null || path.isDone) {
                val steps = graph.findPath(i to j, dm.targetTile)
                currentPath = if (steps.isNotEmpty()) PathPlan(steps) else null
                stepTimeout = 0
            } else {
                followPath(i, j)
            }
        }

        moveX(dirX * attackSpeed, attackSpeed)
        applyPhysics()
    }

    private fun startMoving() {
        val i = floor(x / mapArea.w).toInt()
        val j = floor((y + radius - 1f) / mapArea.h).toInt()
        val steps = graph.findPath(i to j, graph.randomStandable() ?: return)
        if (steps.isEmpty()) return
        currentPath = PathPlan(steps)
        stepTimeout = 0
    }

    private fun followPath(i: Int, j: Int): Boolean {
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
            if (effectiveStep.dirX != 0f) lastDirX = effectiveStep.dirX
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

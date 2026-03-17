package fr.iutlens.mmi.demo.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import fr.iutlens.mmi.demo.game.sprite.TiledArea
import fr.iutlens.mmi.demo.utils.DistanceMap
import fr.iutlens.mmi.demo.utils.PathPlan
import fr.iutlens.mmi.demo.utils.PlatformGraph
import org.jetbrains.compose.resources.DrawableResource
import kotlin.math.floor
import kotlin.random.Random

abstract class GenericDino(
    val type: DinoType,
    res: DrawableResource,
    x: Float, y: Float,
    mapArea: TiledArea,
    graph: PlatformGraph
) : WalkingDino(res, x, y, mapArea, graph) {

    override val scoreValue get() = type.scoreValue

    companion object {
        const val PATH_REFRESH_INTERVAL = 25
    }

    override fun paint(drawScope: DrawScope, elapsed: Long) {
        val color = type.color
        if (color != null) {
            drawScope.drawCircle(color = color, radius = radius, center = Offset(x, y))
        } else {
            super.paint(drawScope, elapsed)
        }
    }

    override fun update() {
        if (isDead) return
        if (stunTimer > 0) {
            stunTimer--
            onStun()
            applyPhysics()
            return
        }
        if (jumpCooldown > 0) jumpCooldown--
        val i = floor(x / mapArea.w).toInt()
        val j = floor((y + halfHeight - 1f) / mapArea.h).toInt()
        updateBehavior(i, j)
    }

    protected open fun onStun() {}

    protected abstract fun updateBehavior(i: Int, j: Int)
}

// --- Wander ---

open class WanderDino(
    type: DinoType,
    res: DrawableResource,
    x: Float, y: Float,
    mapArea: TiledArea,
    graph: PlatformGraph
) : GenericDino(type, res, x, y, mapArea, graph) {

    private val b get() = type.behavior as DinoBehavior.Wander
    private var wanderMoving = false

    override fun reset(x: Float, y: Float) {
        super.reset(x, y)
        wanderMoving = false
    }

    override fun updateBehavior(i: Int, j: Int) {
        if (!wanderMoving) {
            dirX = 0f
            if (--idleTimer <= 0) {
                startMoving()
                if (currentPath != null) wanderMoving = true
                else idleTimer = Random.nextInt(25, 101)
            }
        } else {
            val done = followPath(i, j)
            if (done) {
                wanderMoving = false
                dirX = 0f
                currentPath = null
                idleTimer = Random.nextInt(25, 101)
            }
        }
        moveX(dirX * b.speed, b.speed)
        applyPhysics()
    }
}

// --- Flee from player ---

open class FleeDino(
    type: DinoType,
    res: DrawableResource,
    x: Float, y: Float,
    mapArea: TiledArea,
    val distanceMap: DistanceMap,
    graph: PlatformGraph
) : GenericDino(type, res, x, y, mapArea, graph) {

    private val b get() = type.behavior as DinoBehavior.FleeFromPlayer

    private enum class FleePhase { IDLE, MOVING, FLEEING }
    private var fleePhase = FleePhase.IDLE

    override fun reset(x: Float, y: Float) {
        super.reset(x, y)
        fleePhase = FleePhase.IDLE
    }

    override fun onStun() {
        currentPath = null
    }

    override fun updateBehavior(i: Int, j: Int) {
        val dm = distanceMap
        val distToPlayer = dm[i, j]

        if (distToPlayer != null && distToPlayer <= b.triggerTiles && fleePhase != FleePhase.FLEEING) {
            fleePhase = FleePhase.FLEEING
            currentPath = null
        } else if ((distToPlayer == null || distToPlayer > b.releaseTiles) && fleePhase == FleePhase.FLEEING) {
            fleePhase = FleePhase.IDLE
            dirX = 0f
            currentPath = null
            idleTimer = Random.nextInt(25, 101)
        }

        val currentSpeed = if (fleePhase == FleePhase.FLEEING) b.fleeSpeed else b.walkSpeed

        when (fleePhase) {
            FleePhase.FLEEING -> {
                if (!isOnGround) {
                    dirX = savedPathDirX
                } else {
                    val path = currentPath
                    if (path == null || path.isDone) recomputeFleePath(i, j, dm)
                    else followPath(i, j)
                }
            }
            FleePhase.IDLE -> {
                dirX = 0f
                if (--idleTimer <= 0) {
                    startMoving()
                    if (currentPath != null) fleePhase = FleePhase.MOVING
                    else idleTimer = Random.nextInt(25, 101)
                }
            }
            FleePhase.MOVING -> {
                val done = followPath(i, j)
                if (done) {
                    fleePhase = FleePhase.IDLE
                    dirX = 0f
                    currentPath = null
                    idleTimer = Random.nextInt(25, 101)
                }
            }
        }

        moveX(dirX * currentSpeed, currentSpeed)
        applyPhysics()
    }

    private fun recomputeFleePath(i: Int, j: Int, dm: DistanceMap) {
        val steps = graph.findFleePathTo(i to j, dm.targetTile)
        currentPath = if (steps.isNotEmpty()) PathPlan(steps) else null
        stepTimeout = 0
    }
}

// --- Chase player ---

open class ChaseDino(
    type: DinoType,
    res: DrawableResource,
    x: Float, y: Float,
    mapArea: TiledArea,
    val distanceMap: DistanceMap,
    graph: PlatformGraph
) : GenericDino(type, res, x, y, mapArea, graph) {

    private val b get() = type.behavior as DinoBehavior.ChasePlayer
    private var pathRefreshTimer = 0

    override fun reset(x: Float, y: Float) {
        super.reset(x, y)
        pathRefreshTimer = 0
    }

    override fun updateBehavior(i: Int, j: Int) {
        val dm = distanceMap

        if (--pathRefreshTimer <= 0) {
            recomputeAttackPath(i, j, dm)
            pathRefreshTimer = PATH_REFRESH_INTERVAL
        }

        if (!isOnGround) {
            dirX = lastDirX
        } else {
            val path = currentPath
            if (path == null || path.isDone) {
                recomputeAttackPath(i, j, dm)
            } else {
                val done = followPath(i, j)
                if (dirX != 0f) lastDirX = dirX
                if (done) recomputeAttackPath(i, j, dm)
            }
        }

        moveX(dirX * b.speed, b.speed)
        applyPhysics()
    }

    private fun recomputeAttackPath(i: Int, j: Int, dm: DistanceMap) {
        val steps = graph.findPath(i to j, dm.targetTile)
        currentPath = if (steps.isNotEmpty()) PathPlan(steps) else null
        stepTimeout = 0
    }
}

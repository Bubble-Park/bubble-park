package fr.iutlens.mmi.demo.components

import androidx.compose.ui.graphics.drawscope.DrawScope
import fr.iutlens.mmi.demo.game.sprite.TiledArea
import fr.iutlens.mmi.demo.utils.DistanceMap
import fr.iutlens.mmi.demo.utils.PathPlan
import fr.iutlens.mmi.demo.utils.PlatformGraph
import org.jetbrains.compose.resources.DrawableResource
import kotlin.math.floor
import kotlin.random.Random

open class GenericDino(
    res: DrawableResource,
    x: Float, y: Float,
    mapArea: TiledArea,
    val type: DinoType,
    private val distanceMap: DistanceMap? = null,
    graph: PlatformGraph
) : WalkingDino(res, x, y, mapArea, graph) {

    override val scoreValue get() = type.scoreValue

    override fun paint(drawScope: DrawScope, elapsed: Long) {
        val color = type.color
        if (color != null) {
            drawScope.drawCircle(color = color, radius = radius, center = androidx.compose.ui.geometry.Offset(x, y))
        } else {
            super.paint(drawScope, elapsed)
        }
    }

    // État Wander
    private var wanderMoving = false

    // État Flee
    private enum class FleePhase { IDLE, MOVING, FLEEING }
    private var fleePhase = FleePhase.IDLE

    // État Chase
    private var pathRefreshTimer = 0
    private var lastDirX = 0f

    companion object {
        const val PATH_REFRESH_INTERVAL = 25
    }

    override fun reset(x: Float, y: Float) {
        super.reset(x, y)
        wanderMoving = false
        fleePhase = FleePhase.IDLE
        pathRefreshTimer = 0
        lastDirX = 0f
    }

    override fun update() {
        if (isDead) return

        if (stunTimer > 0) {
            stunTimer--
            if (type.behavior is DinoBehavior.FleeFromPlayer) currentPath = null
            applyPhysics()
            return
        }

        if (jumpCooldown > 0) jumpCooldown--

        val i = floor(x / mapArea.w).toInt()
        val j = floor((y + radius - 1f) / mapArea.h).toInt()

        when (val b = type.behavior) {
            is DinoBehavior.Wander -> updateWander(i, j, b)
            is DinoBehavior.FleeFromPlayer -> updateFlee(i, j, b)
            is DinoBehavior.ChasePlayer -> updateChase(i, j, b)
        }
    }

    // --- Wander ---

    private fun updateWander(i: Int, j: Int, b: DinoBehavior.Wander) {
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

    // --- Flee from player ---

    private fun updateFlee(i: Int, j: Int, b: DinoBehavior.FleeFromPlayer) {
        val dm = distanceMap ?: return
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
                    dirX = fleeingDirX
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

    // --- Chase player ---

    private fun updateChase(i: Int, j: Int, b: DinoBehavior.ChasePlayer) {
        val dm = distanceMap ?: return

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
                // followPath met à jour dirX — on mémorise la dernière direction connue
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

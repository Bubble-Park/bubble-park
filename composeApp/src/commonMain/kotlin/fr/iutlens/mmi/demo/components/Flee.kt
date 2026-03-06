package fr.iutlens.mmi.demo.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import fr.iutlens.mmi.demo.game.sprite.TiledArea
import fr.iutlens.mmi.demo.utils.DistanceMap
import fr.iutlens.mmi.demo.utils.MoveAction
import fr.iutlens.mmi.demo.utils.PathPlan
import fr.iutlens.mmi.demo.utils.PlatformGraph
import fr.iutlens.mmi.demo.utils.fleeDiagnostic
import org.jetbrains.compose.resources.DrawableResource
import kotlin.math.floor
import kotlin.random.Random

class Flee(
    res: DrawableResource,
    x: Float, y: Float,
    mapArea: TiledArea,
    val distanceMap: DistanceMap,
    val graph: PlatformGraph
) : Dino(res, x, y, mapArea) {

    enum class State { IDLE, MOVING, FLEEING }

    companion object {
        const val FLEE_TRIGGER_TILES = 6
        const val FLEE_RELEASE_TILES = 8
        const val STEP_TIMEOUT = 300
        const val DEBUG_FLEE = true  // DEBUG — mettre false ou supprimer avec PlatformGraphDebug.kt
    }

    val normalSpeed = 20f
    val fleeSpeed = 40f

    private var state = State.IDLE
    private var idleTimer = Random.nextInt(10, 100)
    private var dirX = 0f
    private var moveTarget = 0
    private var moveDone = 0f
    private var fleeingDirX = 0f
    private var currentPath: PathPlan? = null
    private var stepTimeout = 0

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
                            // WALK : doit être sur la bonne colonne ET rangée
                            MoveAction.WALK -> i == step.tile.first && j == step.tile.second
                            // FALL : la chute peut décaler horizontalement — suffit d'avoir atterri à la bonne rangée
                            MoveAction.FALL -> j >= step.tile.second
                            // JUMP : suffit d'être sur la bonne rangée (ou au-dessus si overshoot)
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
                        // Applique l'étape active (peut être la suivante après advance)
                        val activeStep = path.current
                        if (activeStep != null) {
                            if (activeStep.dirX != 0f) fleeingDirX = activeStep.dirX
                            dirX = if (activeStep.dirX != 0f) activeStep.dirX else fleeingDirX
                            if (activeStep.action == MoveAction.JUMP && jumpCooldown <= 0 && j > 0) {
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
                if (--idleTimer <= 0) startMoving()
            }
            State.MOVING -> {
                if (isBlockedInDir(dirX, i)) {
                    switchToIdle()
                } else {
                    moveDone += normalSpeed
                    if (moveDone >= moveTarget) {
                        switchToIdle()
                    } else if (isOnGround && jumpCooldown <= 0 && hasPlatformAbove(i, j) && Random.nextFloat() < 0.02f) {
                        jump()
                        jumpCooldown = 50
                    }
                }
            }
        }

        val currentSpeed = if (state == State.FLEEING) fleeSpeed else normalSpeed
        moveX(dirX * currentSpeed, currentSpeed)
        applyPhysics()
    }

    private fun recomputeFleePath(i: Int, j: Int) {
        val myTile = i to j
        val playerTile = distanceMap.targetTile
        val pathSteps = graph.findFleePathTo(myTile, distanceMap.distances, playerTile)
        @Suppress("ConstantConditionIf")
        if (DEBUG_FLEE) println(graph.fleeDiagnostic(myTile, distanceMap.distances, playerTile))
        currentPath = if (pathSteps.isNotEmpty()) PathPlan(pathSteps) else null
        stepTimeout = 0
    }

    private fun switchToIdle() {
        state = State.IDLE
        dirX = 0f
        currentPath = null
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
            color = Color.Yellow,
            radius = radius,
            center = Offset(x, y)
        )
    }
}

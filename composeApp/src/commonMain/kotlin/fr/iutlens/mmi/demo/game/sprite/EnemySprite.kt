package fr.iutlens.mmi.demo.game.sprite

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import fr.iutlens.mmi.demo.utils.DistanceMap
import org.jetbrains.compose.resources.DrawableResource
import kotlin.math.abs
import kotlin.math.floor

enum class EnemyBehavior {
    ATTACK,
    FLEE
}

class EnemySprite(
    res: DrawableResource,
    x: Float,
    y: Float,
    mapArea: TiledArea,
    val distanceMap: DistanceMap,
    val behavior: EnemyBehavior,
    var speed: Float = if (behavior == EnemyBehavior.FLEE) 32f else 22f
) : PhysicsSprite(res, x, y, mapArea, gravity = 8f, jumpForce = -110f) {

    var stunTimer = 0
    var jumpCooldown = 0
    val radius = 60f
    var isDead = false

    private var fleeRetreatTimer = 0
    private var fleeRetreatDir = 0f
    private var fleeDescending = false

    override val boundingBox: Rect
        get() = Rect(x - radius, y - radius, x + radius, y + radius)

    override fun paint(drawScope: DrawScope, elapsed: Long) {
        val color = if (behavior == EnemyBehavior.ATTACK) Color.Red else Color.Blue
        drawScope.drawCircle(
            color = color,
            radius = radius,
            center = androidx.compose.ui.geometry.Offset(x, y)
        )
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

        val dirX = when (behavior) {
            EnemyBehavior.ATTACK -> computeAttackDirX(i, j)
            EnemyBehavior.FLEE   -> computeFleeDirX(i, j)
        }

        if (isOnGround && jumpCooldown <= 0) {
            val shouldJump = when (behavior) {
                EnemyBehavior.ATTACK -> shouldAttackJump(i, j, dirX)
                EnemyBehavior.FLEE   -> shouldFleeJump(i, j, dirX)
            }
            if (shouldJump) {
                jump()
                jumpCooldown = if (behavior == EnemyBehavior.FLEE) 30 else 50
            }
        }

        moveX(dirX * speed, speed)
        applyPhysics()
        super.update()
    }

    // --- ATTACK ---

    private fun computeAttackDirX(i: Int, j: Int): Float {
        val targetTile = distanceMap.next(i to j)
        val playerX = distanceMap.target.boundingBox.center.x
        return if (targetTile != null) {
            when {
                targetTile.first < i -> -1f
                targetTile.first > i ->  1f
                targetTile.second > j -> {
                    val cx = targetTile.first * mapArea.w + mapArea.w / 2f
                    if (x < cx) 1f else if (x > cx) -1f else 0f
                }
                else -> 0f
            }
        } else {
            if (x < playerX) 1f else if (x > playerX) -1f else 0f
        }
    }

    private fun shouldAttackJump(i: Int, j: Int, dirX: Float): Boolean {
        val targetTile = distanceMap.next(i to j)
        if (targetTile != null && targetTile.second < j) return true
        if (dirX != 0f) {
            val nextX = x + dirX * mapArea.w * 0.6f
            if (isWall(nextX, y, checkPlatform = false)) {
                val wallI = floor(nextX / mapArea.w).toInt()
                if ((mapArea.tileMap.get(wallI, j - 1) ?: 0) == 0) return true
            }
        }
        return false
    }

    // --- FLEE ---

    private fun preferGoUp(j: Int, playerJ: Int): Boolean {
        val jBase = mapArea.tileMap.geometry.sizeY - 1
        if (j == 0) return false
        if (j >= jBase) return true
        val distIfUp   = abs(0     - playerJ)
        val distIfDown = abs(jBase - playerJ)
        return distIfUp >= distIfDown
    }

    private fun triggerRetreat(wantedDir: Float) {
        fleeRetreatTimer = 50
        fleeRetreatDir = -wantedDir
    }

    private fun isBlockedInDir(dir: Float, i: Int): Boolean {
        val mapSizeX = mapArea.tileMap.geometry.sizeX
        if (dir < 0f && i <= 0) return true
        if (dir > 0f && i >= mapSizeX - 1) return true
        if (!isOnGround) return false
        val nextX = x + dir * mapArea.w * 0.6f
        return isWall(nextX, y, checkPlatform = false)
    }

    private fun computeFleeDirX(i: Int, j: Int): Float {
        val playerX = distanceMap.target.boundingBox.center.x
        val playerJ = floor((distanceMap.target.boundingBox.bottom - 1f) / mapArea.h).toInt()
        val jBase   = mapArea.tileMap.geometry.sizeY - 1

        if (fleeDescending) {
            val wantedDir = if (x < playerX) -1f else 1f
            val goUp = preferGoUp(j, playerJ)
            if (j >= jBase || goUp) {
                fleeDescending = false
            } else {
                return wantedDir
            }
        }

        if (fleeRetreatTimer > 0) {
            if (j >= jBase) {
                fleeRetreatTimer = 0
            } else {
                fleeRetreatTimer--
                return if (isBlockedInDir(fleeRetreatDir, i)) 0f else fleeRetreatDir
            }
        }

        val wantedDir = if (x < playerX) -1f else 1f

        if (isBlockedInDir(wantedDir, i) && j < jBase) {
            triggerRetreat(wantedDir)
            return fleeRetreatDir
        }

        val nextI = i + if (wantedDir > 0f) 1 else -1
        val nextCode      = mapArea.tileMap.get(nextI, j) ?: -1
        val nextBelowCode = mapArea.tileMap.get(nextI, j + 1) ?: -1
        val voidAhead = nextCode == 0 && nextBelowCode == 0

        if (voidAhead) {
            val goUp = preferGoUp(j, playerJ)
            return if (!goUp) {
                fleeDescending = true
                wantedDir
            } else {
                triggerRetreat(wantedDir); fleeRetreatDir
            }
        }

        return wantedDir
    }

    private fun shouldFleeJump(i: Int, j: Int, dirX: Float): Boolean {
        val playerJ = floor((distanceMap.target.boundingBox.bottom - 1f) / mapArea.h).toInt()
        val checkDir = if (dirX > 0f) 1 else if (dirX < 0f) -1 else 0

        if (preferGoUp(j, playerJ)) {
            val dirsToCheck = if (checkDir != 0) listOf(0, checkDir, -checkDir) else listOf(0)
            for (dy in 1..5) {
                for (di in dirsToCheck) {
                    val code  = mapArea.tileMap.get(i + di, j - dy) ?: 0
                    val above = mapArea.tileMap.get(i + di, j - dy - 1) ?: 0
                    if (code in 1..3 && above == 0) return true
                }
            }
        }

        if (dirX != 0f) {
            val nextX = x + dirX * mapArea.w * 0.6f
            if (isWall(nextX, y, checkPlatform = false)) {
                val wallI = floor(nextX / mapArea.w).toInt()
                if ((mapArea.tileMap.get(wallI, j - 1) ?: 0) == 0) return true
            }
        }

        return false
    }
}

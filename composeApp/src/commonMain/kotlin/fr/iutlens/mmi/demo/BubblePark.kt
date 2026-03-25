package fr.iutlens.mmi.demo

import fr.iutlens.mmi.demo.components.Player
import fr.iutlens.mmi.demo.components.Bullet
import fr.iutlens.mmi.demo.components.dino.GenericDino
import fr.iutlens.mmi.demo.components.dino.Trex
import fr.iutlens.mmi.demo.components.dino.Parasaur
import fr.iutlens.mmi.demo.components.bonus.LifeBonus
import fr.iutlens.mmi.demo.components.dino.Compy
import fr.iutlens.mmi.demo.data.LevelGenerator
import fr.iutlens.mmi.demo.game.Chrono
import fr.iutlens.mmi.demo.game.DifficultyConfig
import fr.iutlens.mmi.demo.game.DifficultyManager
import fr.iutlens.mmi.demo.game.LevelDifficulty
import fr.iutlens.mmi.demo.game.GameData
import fr.iutlens.mmi.demo.game.Score
import fr.iutlens.mmi.demo.game.sprite.Sprite
import fr.iutlens.mmi.demo.game.sprite.TiledArea
import fr.iutlens.mmi.demo.game.sprite.mutableSpriteListOf
import fr.iutlens.mmi.demo.game.sprite.toTileMap
import fr.iutlens.mmi.demo.game.transform.Constraint
import fr.iutlens.mmi.demo.game.transform.GenericTransform
import fr.iutlens.mmi.demo.game.sprite.EnemySprite
import fr.iutlens.mmi.demo.utils.DistanceMap
import fr.iutlens.mmi.demo.utils.PlatformGraph
import fr.iutlens.mmi.demo.utils.distanceMap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.math.ceil
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.math.floor
import kotlin.math.abs

class BubblePark : GameData() {

    companion object {
        // Tile de départ du joueur (startX=1.5, sizeY=23 - startY=2.5)
        private const val PLAYER_INIT_TILE_I = 1
        private const val PLAYER_INIT_TILE_J = 20
        private const val INITIAL_SPAWN_RATIO = 0.3f
    }

    val score = Score()
    var chrono = Chrono((DifficultyConfig.TOTAL_LEVEL_TIME * 1000f).toLong())
    lateinit var player: Player
    private lateinit var tileArea: TiledArea
    private lateinit var platformGraph: PlatformGraph
    private lateinit var distanceMap: DistanceMap

    var onLevelEnd: ((hasNextLevel: Boolean) -> Unit)? = null
    var levelIndex by mutableStateOf(0)

    private var spawnTimerMs = 0L
    private var levelElapsedMs = 0L
    private lateinit var currentLevelDiff: LevelDifficulty

    private var lifeBonusTimerMs = 0L
    private var nextLifeBonusDurationMs = 0L

    private val activeBullets = mutableListOf<Bullet>()
    private val activeEnemies = mutableListOf<EnemySprite>()
    private val activeGenericDinos = mutableListOf<GenericDino>()

    fun loadNextLevel() = loadLevel(levelIndex + 1)

    fun loadLevel(index: Int) {
        levelIndex = index
        currentLevelDiff = DifficultyManager.getLevelDifficulty(index + 1)
        chrono = Chrono((DifficultyConfig.TOTAL_LEVEL_TIME * 1000f).toLong())
        spawnTimerMs = 0L
        levelElapsedMs = 0L
        lifeBonusTimerMs = 0L
        nextLifeBonusDurationMs = Random.nextLong(15000L, 25000L)

        val levelData = LevelGenerator.generate(index)
        val tileMap = levelData.mapString.toTileMap(levelData.mapCode)
        tileArea = TiledArea(levelData.tileSetRes, tileMap)

        val savedLife = if (::player.isInitialized) player.life else 3
        player = Player(
            res = Res.drawable.bubblechtein_sprites,
            x = levelData.startX * tileArea.w,
            y = (tileMap.geometry.sizeY - levelData.startY) * tileArea.h,
            mapArea = tileArea,
            joystickProvider = { game.joystickPosition },
            jumpActionProvider = { game.actionButtonB },
            bulletRes = Res.drawable.bubble_sprite,
            elapsedProvider = { game.elapsed },
            onBulletCreated = { bullet -> (game.spriteList as? MutableList<Sprite>)?.add(bullet) },
            initialLife = savedLife
        )

        platformGraph = PlatformGraph(tileArea, jumpHeight = 6)
        distanceMap = tileArea.distanceMap(player, platformGraph)

        createGame(
            background = tileArea,
            spriteList = mutableSpriteListOf<Sprite>(player),
            transform = GenericTransform(Constraint.Fill(tileArea))
        )

        spawnInitialDinos()

        game.animation(20) {
            handleCollisions()

            levelElapsedMs += 20
            spawnTimerMs += 20
            lifeBonusTimerMs += 20

            val localDiff = DifficultyManager.updateLocalDifficulty(
                currentLevelDiff.difficulty, levelElapsedMs / 1000f
            )
            val (spawnDelayS, currentMaxDino) = DifficultyManager.getLiveValues(localDiff)

            if (chrono.isFinished()) {
                game.paused = true
                onLevelEnd?.invoke(true)
                return@animation
            }

            if (spawnTimerMs >= (spawnDelayS * 1000).toLong()) {
                spawnTimerMs = 0L
                trySpawnNextDino(currentMaxDino)
            }

            if (lifeBonusTimerMs >= nextLifeBonusDurationMs) {
                lifeBonusTimerMs = 0L
                nextLifeBonusDurationMs = Random.nextLong(15000L, 25000L)
                val bonusX = Random.nextFloat() * (tileArea.tileMap.geometry.sizeX - 4) * tileArea.w + 2 * tileArea.w
                (game.spriteList as? MutableList<Sprite>)?.add(LifeBonus(bonusX, 0f))
            }

            distanceMap.update()

            val mapHeight = tileArea.tileMap.geometry.sizeY * tileArea.h
            (game.spriteList as? MutableList<Sprite>)?.apply {
                removeAll { (it as? Bullet)?.isStopped == true }
                removeAll { (it as? EnemySprite)?.isDead == true }
                removeAll { (it as? GenericDino)?.isDead == true }
                removeAll { it is LifeBonus && (it.collected || it.y > mapHeight) }
            }

            game.spriteList.update()
            game.invalidate()
        }
    }

    private fun handleCollisions() {
        activeBullets.clear()
        activeEnemies.clear()
        activeGenericDinos.clear()

        for (sprite in game.spriteList) {
            when {
                sprite is Bullet && !sprite.isStopped -> activeBullets.add(sprite)
                sprite is EnemySprite && !sprite.isDead -> activeEnemies.add(sprite)
                sprite is GenericDino && !sprite.isDead -> activeGenericDinos.add(sprite)
                sprite is LifeBonus && !sprite.collected -> {
                    if (sprite.boundingBox.overlaps(player.boundingBox)) {
                        player.heal()
                        sprite.collected = true
                    }
                }
            }
        }

        // EnemySprite damage player
        for (enemy in activeEnemies) {
            if (enemy.stunTimer > 0) continue
            if (enemy.boundingBox.overlaps(player.boundingBox)) {
                if (player.takeDamage()) enemy.stunTimer = 50
            }
        }

        for (dino in activeGenericDinos) {
            if (dino.isCaptured) continue
            if (!dino.type.damagesPlayer) continue
            if (dino.stunTimer > 0) continue
            if (dino.boundingBox.overlaps(player.boundingBox)) {
                if (player.takeDamage()) dino.stunTimer = 50
            }
        }

        // Joueur collecte un dino capturé
        for (dino in activeGenericDinos) {
            if (!dino.isCaptured) continue
            if (player.boundingBox.overlaps(dino.boundingBox)) {
                dino.isDead = true
                score.add(dino.scoreValue)
                break
            }
        }

        // Bullets vs enemies
        for (bullet in activeBullets) {
            for (enemy in activeEnemies) {
                if (enemy.isDead) continue
                if (bullet.boundingBox.overlaps(enemy.boundingBox)) {
                    enemy.isDead = true
                    score.add(enemy.scoreValue)
                    bullet.explode()
                    break
                }
            }
            if (bullet.isStopped) continue
            for (dino in activeGenericDinos) {
                if (dino.isDead || dino.isCaptured) continue
                if (bullet.boundingBox.overlaps(dino.boundingBox)) {
                    dino.isCaptured = true
                    bullet.explode()
                    break
                }
            }
        }
    }

    private fun findSpawnPoint(refTileI: Int, refTileJ: Int): Pair<Float, Float>? {
        fun collectSpawns(minDistI: Int, minDistJ: Int): List<Pair<Int, Int>> {
            val result = mutableListOf<Pair<Int, Int>>()
            for (i in 0 until tileArea.tileMap.geometry.sizeX) {
                for (j in 0 until tileArea.tileMap.geometry.sizeY - 1) {
                    val current = tileArea.tileMap.get(i, j) ?: 0
                    val below = tileArea.tileMap.get(i, j + 1) ?: 0
                    if (current == 0 && below in 1..7) {
                        if (abs(i - refTileI) > minDistI && abs(j - refTileJ) > minDistJ) {
                            result.add(i to j)
                        }
                    }
                }
            }
            return result
        }

        val spawns = collectSpawns(6, 3).ifEmpty { collectSpawns(3, 1) }
        if (spawns.isEmpty()) return null

        val (si, sj) = spawns[Random.nextInt(spawns.size)]
        return Pair(si * tileArea.w + tileArea.w / 2f, sj * tileArea.h + tileArea.h / 2f)
    }

    private fun findSpawnPoint(): Pair<Float, Float>? {
        val playerI = floor(player.x / tileArea.w).toInt()
        val playerJ = floor(player.y / tileArea.h).toInt()
        return findSpawnPoint(playerI, playerJ)
    }

    private fun spawnInitialDinos() {
        val initialCount = ceil(currentLevelDiff.maxDino * INITIAL_SPAWN_RATIO).toInt()
        val targetTrex    = (initialCount * DifficultyConfig.RATIO_CHASE).roundToInt()
        val targetCompy   = (initialCount * DifficultyConfig.RATIO_WANDER).roundToInt()
        val targetParasaur = initialCount - targetTrex - targetCompy
        var spawnedTrex = 0
        var spawnedCompy = 0
        var spawnedParasaur = 0
        val sprites = game.spriteList as? MutableList<Sprite> ?: return

        repeat(initialCount) {
            val chaseNeeded  = (targetTrex - spawnedTrex).coerceAtLeast(0)
            val wanderNeeded = (targetCompy - spawnedCompy).coerceAtLeast(0)
            val fleeNeeded   = (targetParasaur - spawnedParasaur).coerceAtLeast(0)
            val total = chaseNeeded + wanderNeeded + fleeNeeded
            if (total == 0) return@repeat

            findSpawnPoint(PLAYER_INIT_TILE_I, PLAYER_INIT_TILE_J)?.let { (x, y) ->

                val roll = Random.nextInt(total)
                when {
                    roll < chaseNeeded -> {
                        sprites.add(Trex(Res.drawable.trex_sprite, x, y, tileArea, distanceMap, platformGraph))
                        spawnedTrex++
                    }
                    roll < chaseNeeded + wanderNeeded -> {
                        sprites.add(Compy(Res.drawable.compy_sprite, x, y, tileArea, platformGraph))
                        spawnedCompy++
                    }
                    else -> {
                        sprites.add(Parasaur(Res.drawable.parasaur_sprite, x, y, tileArea, distanceMap, platformGraph))
                        spawnedParasaur++
                    }
                }
            }
        }
    }

    private fun trySpawnNextDino(maxDino: Int) {
        val activeTrex    = activeGenericDinos.count { it is Trex }
        val activeCompy   = activeGenericDinos.count { it is Compy }
        val activeParasaur = activeGenericDinos.count { it is Parasaur }
        if (activeTrex + activeCompy + activeParasaur >= maxDino) return

        val targetTrex    = (maxDino * DifficultyConfig.RATIO_CHASE).roundToInt()
        val targetCompy   = (maxDino * DifficultyConfig.RATIO_WANDER).roundToInt()
        val targetParasaur = maxDino - targetTrex - targetCompy

        val chaseNeeded  = (targetTrex - activeTrex).coerceAtLeast(0)
        val wanderNeeded = (targetCompy - activeCompy).coerceAtLeast(0)
        val fleeNeeded   = (targetParasaur - activeParasaur).coerceAtLeast(0)
        val total = chaseNeeded + wanderNeeded + fleeNeeded
        if (total == 0) return

        val sprites = game.spriteList as? MutableList<Sprite> ?: return
        findSpawnPoint()?.let { (x, y) ->
            val roll = Random.nextInt(total)
            when {
                roll < chaseNeeded -> sprites.add(Trex(Res.drawable.trex_sprite, x, y, tileArea, distanceMap, platformGraph))
                roll < chaseNeeded + wanderNeeded -> sprites.add(Compy(Res.drawable.compy_sprite, x, y, tileArea, platformGraph))
                else -> sprites.add(Parasaur(Res.drawable.parasaur_sprite, x, y, tileArea, distanceMap, platformGraph))
            }
        }
    }

    init {
        loadLevel(0)
    }
}

package fr.iutlens.mmi.demo

import fr.iutlens.mmi.demo.components.Player
import fr.iutlens.mmi.demo.components.Bullet
import fr.iutlens.mmi.demo.components.dino.GenericDino
import fr.iutlens.mmi.demo.components.dino.Trex
import fr.iutlens.mmi.demo.components.dino.Raptor
import fr.iutlens.mmi.demo.components.dino.Dodo
import fr.iutlens.mmi.demo.components.dino.Gallimimus
import fr.iutlens.mmi.demo.components.dino.Parasaur
import fr.iutlens.mmi.demo.components.bonus.Bonus
import fr.iutlens.mmi.demo.components.bonus.LifeBonus
import fr.iutlens.mmi.demo.components.bonus.SlowBonus
import fr.iutlens.mmi.demo.components.bonus.FastAmmoBonus
import fr.iutlens.mmi.demo.game.SlowEffect
import fr.iutlens.mmi.demo.game.FastAmmoEffect
import fr.iutlens.mmi.demo.components.dino.Compy
import fr.iutlens.mmi.demo.components.dino.Triceratops
import fr.iutlens.mmi.demo.components.dino.Stegosaurus
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
import fr.iutlens.mmi.demo.utils.GameSound
import fr.iutlens.mmi.demo.utils.PlatformGraph
import fr.iutlens.mmi.demo.utils.distanceMap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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

    data class ScorePopup(val id: Long, val worldX: Float, val worldY: Float, val points: Int)
    val scorePopups = mutableStateListOf<ScorePopup>()
    private var popupCounter = 0L

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

    private var bonusTimerMs = 0L
    private var lastBonusIndex = -1

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
        bonusTimerMs = 0L
        SlowEffect.reset()
        FastAmmoEffect.reset()

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
            if (SlowEffect.isActive) SlowEffect.timer--
            if (FastAmmoEffect.isActive) FastAmmoEffect.timer--

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

            if (FastAmmoEffect.isActive) player.shoot(delayMs = FastAmmoEffect.shootDelayMs)

            bonusTimerMs += 20

            if (bonusTimerMs >= 15000L) {
                bonusTimerMs = 0L
                val available = (0..2).filter { it != lastBonusIndex }
                val pick = available[Random.nextInt(available.size)]
                lastBonusIndex = pick
                val bonusX = Random.nextFloat() * (tileArea.tileMap.geometry.sizeX - 10) * tileArea.w + 5 * tileArea.w
                val bonus: Bonus = when (pick) {
                    0 -> LifeBonus(bonusX, 0f, player)
                    1 -> SlowBonus(bonusX, 0f)
                    else -> FastAmmoBonus(bonusX, 0f)
                }
                (game.spriteList as? MutableList<Sprite>)?.add(bonus)
            }

            distanceMap.update()

            val mapHeight = tileArea.tileMap.geometry.sizeY * tileArea.h
            (game.spriteList as? MutableList<Sprite>)?.apply {
                removeAll { (it as? Bullet)?.isStopped == true }
                removeAll { (it as? EnemySprite)?.isDead == true }
                removeAll { (it as? GenericDino)?.isDead == true }
                removeAll { it is Bonus && (it.collected || it.y > mapHeight) }
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
                sprite is Bonus && !sprite.collected -> {
                    if (sprite.boundingBox.overlaps(player.boundingBox)) {
                        sprite.onCollect()
                        sprite.collected = true
                    }
                }
            }
        }

        // EnemySprite damage player
        for (enemy in activeEnemies) {
            if (enemy.stunTimer > 0) continue
            if (enemy.boundingBox.overlaps(player.boundingBox)) {
                if (player.takeDamage()) {
                    GameSound.playHit(player.life + 1)
                    enemy.stunTimer = 50
                }
            }
        }

        for (dino in activeGenericDinos) {
            if (dino.isCaptured) continue
            if (!dino.type.damagesPlayer) continue
            if (dino.stunTimer > 0) continue
            if (dino.boundingBox.overlaps(player.boundingBox)) {
                if (player.takeDamage()) {
                    GameSound.playHit(player.life + 1)
                    dino.stunTimer = 50
                }
            }
        }

        // Joueur collecte un dino capturé
        for (dino in activeGenericDinos) {
            if (!dino.isCaptured) continue
            if (player.boundingBox.overlaps(dino.boundingBox)) {
                dino.isDead = true
                score.add(dino.scoreValue)
                scorePopups.add(ScorePopup(popupCounter++, dino.x, dino.y, dino.scoreValue))
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
                    scorePopups.add(ScorePopup(popupCounter++, enemy.x, enemy.y, enemy.scoreValue))
                    bullet.explode()
                    break
                }
            }
            if (bullet.isStopped || bullet.isExploding) continue
            for (dino in activeGenericDinos) {
                if (dino.isDead || dino.isCaptured) continue
                if (bullet.boundingBox.overlaps(dino.boundingBox)) {
                    dino.currentHitCount++
                    dino.onHitByBullet()
                    if (dino.currentHitCount >= dino.effectiveHitCount) {
                        dino.isCaptured = true
                        dino.currentHitCount = 0
                    } else {
                        dino.stunTimer = 50
                    }
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

        val chaseTotal         = (initialCount * DifficultyConfig.RATIO_CHASE).roundToInt()
        val targetTrex         = chaseTotal / 2
        val targetRaptor       = chaseTotal - targetTrex
        val wanderTotal        = (initialCount * DifficultyConfig.RATIO_WANDER).roundToInt()
        val targetCompy        = wanderTotal / 2
        val targetDodo         = wanderTotal - targetCompy
        val fleeTotal          = (initialCount * DifficultyConfig.RATIO_FLEE).roundToInt()
        val targetParasaur     = fleeTotal / 2
        val targetGallimimus   = fleeTotal - targetParasaur
        val defensiveTotal     = initialCount - chaseTotal - wanderTotal - fleeTotal
        val targetTriceratops  = defensiveTotal / 2
        val targetStegosaurus  = defensiveTotal - targetTriceratops

        var spawnedTrex = 0
        var spawnedRaptor = 0
        var spawnedCompy = 0
        var spawnedDodo = 0
        var spawnedParasaur = 0
        var spawnedGallimimus = 0
        var spawnedTriceratops = 0
        var spawnedStegosaurus = 0
        val sprites = game.spriteList as? MutableList<Sprite> ?: return

        repeat(initialCount) {
            val trexNeeded        = (targetTrex - spawnedTrex).coerceAtLeast(0)
            val raptorNeeded      = (targetRaptor - spawnedRaptor).coerceAtLeast(0)
            val compyNeeded       = (targetCompy - spawnedCompy).coerceAtLeast(0)
            val dodoNeeded        = (targetDodo - spawnedDodo).coerceAtLeast(0)
            val parasaurNeeded    = (targetParasaur - spawnedParasaur).coerceAtLeast(0)
            val gallimimusNeeded  = (targetGallimimus - spawnedGallimimus).coerceAtLeast(0)
            val triceNeeded       = (targetTriceratops - spawnedTriceratops).coerceAtLeast(0)
            val stegoNeeded       = (targetStegosaurus - spawnedStegosaurus).coerceAtLeast(0)
            val total = trexNeeded + raptorNeeded + compyNeeded + dodoNeeded + parasaurNeeded + gallimimusNeeded + triceNeeded + stegoNeeded

            if (total == 0) return@repeat

            findSpawnPoint(PLAYER_INIT_TILE_I, PLAYER_INIT_TILE_J)?.let { (x, y) ->
                val roll = Random.nextInt(total)
                when {
                    roll < trexNeeded -> {
                        sprites.add(Trex(Res.drawable.trex_sprite, x, y, tileArea, distanceMap, platformGraph))
                        spawnedTrex++
                    }
                    roll < trexNeeded + raptorNeeded -> {
                        sprites.add(Raptor(Res.drawable.raptor_sprite, x, y, tileArea, distanceMap, platformGraph))
                        spawnedRaptor++
                    }
                    roll < trexNeeded + raptorNeeded + compyNeeded -> {
                        sprites.add(Compy(Res.drawable.compy_sprite, x, y, tileArea, platformGraph))
                        spawnedCompy++
                    }
                    roll < trexNeeded + raptorNeeded + compyNeeded + dodoNeeded -> {
                        sprites.add(Dodo(Res.drawable.dodo_sprite, x, y, tileArea, platformGraph))
                        spawnedDodo++
                    }
                    roll < trexNeeded + raptorNeeded + compyNeeded + dodoNeeded + parasaurNeeded -> {
                        sprites.add(Parasaur(Res.drawable.parasaur_sprite, x, y, tileArea, distanceMap, platformGraph))
                        spawnedParasaur++
                    }
                    roll < trexNeeded + raptorNeeded + compyNeeded + dodoNeeded + parasaurNeeded + gallimimusNeeded -> {
                        sprites.add(Gallimimus(Res.drawable.gallimimus_sprite, x, y, tileArea, distanceMap, platformGraph))
                        spawnedGallimimus++
                    }
                    roll < trexNeeded + raptorNeeded + compyNeeded + dodoNeeded + parasaurNeeded + gallimimusNeeded + triceNeeded -> {
                        sprites.add(Triceratops(Res.drawable.trice_sprite, x, y, tileArea, distanceMap, platformGraph))
                        spawnedTriceratops++
                    }
                    else -> {
                        sprites.add(Stegosaurus(Res.drawable.stego_sprite, x, y, tileArea, distanceMap, platformGraph))
                        spawnedStegosaurus++
                    }
                }
            }
        }
    }

    private fun trySpawnNextDino(maxDino: Int) {

        val activeTrex        = activeGenericDinos.count { it is Trex }
        val activeRaptor      = activeGenericDinos.count { it is Raptor }
        val activeCompy       = activeGenericDinos.count { it is Compy }
        val activeDodo        = activeGenericDinos.count { it is Dodo }
        val activeParasaur    = activeGenericDinos.count { it is Parasaur }
        val activeGallimimus  = activeGenericDinos.count { it is Gallimimus }
        val activeTriceratops = activeGenericDinos.count { it is Triceratops }
        val activeStegosaurus = activeGenericDinos.count { it is Stegosaurus }
        if (activeTrex + activeRaptor + activeCompy + activeDodo + activeParasaur + activeGallimimus + activeTriceratops + activeStegosaurus >= maxDino) return

        val chaseTotal        = (maxDino * DifficultyConfig.RATIO_CHASE).roundToInt()
        val targetTrex        = chaseTotal / 2
        val targetRaptor      = chaseTotal - targetTrex
        val wanderTotal       = (maxDino * DifficultyConfig.RATIO_WANDER).roundToInt()
        val targetCompy       = wanderTotal / 2
        val targetDodo        = wanderTotal - targetCompy
        val fleeTotal         = (maxDino * DifficultyConfig.RATIO_FLEE).roundToInt()
        val targetParasaur    = fleeTotal / 2
        val targetGallimimus  = fleeTotal - targetParasaur
        val defensiveTotal    = maxDino - chaseTotal - wanderTotal - fleeTotal
        val targetTriceratops = defensiveTotal / 2
        val targetStegosaurus = defensiveTotal - targetTriceratops

        val trexNeeded        = (targetTrex - activeTrex).coerceAtLeast(0)
        val raptorNeeded      = (targetRaptor - activeRaptor).coerceAtLeast(0)
        val compyNeeded       = (targetCompy - activeCompy).coerceAtLeast(0)
        val dodoNeeded        = (targetDodo - activeDodo).coerceAtLeast(0)
        val parasaurNeeded    = (targetParasaur - activeParasaur).coerceAtLeast(0)
        val gallimimusNeeded  = (targetGallimimus - activeGallimimus).coerceAtLeast(0)
        val triceNeeded       = (targetTriceratops - activeTriceratops).coerceAtLeast(0)
        val stegoNeeded       = (targetStegosaurus - activeStegosaurus).coerceAtLeast(0)
        val total = trexNeeded + raptorNeeded + compyNeeded + dodoNeeded + parasaurNeeded + gallimimusNeeded + triceNeeded + stegoNeeded

        if (total == 0) return

        val sprites = game.spriteList as? MutableList<Sprite> ?: return
        findSpawnPoint()?.let { (x, y) ->
            val roll = Random.nextInt(total)
            when {
                roll < trexNeeded -> sprites.add(Trex(Res.drawable.trex_sprite, x, y, tileArea, distanceMap, platformGraph))
                roll < trexNeeded + raptorNeeded -> sprites.add(Raptor(Res.drawable.raptor_sprite, x, y, tileArea, distanceMap, platformGraph))
                roll < trexNeeded + raptorNeeded + compyNeeded -> sprites.add(Compy(Res.drawable.compy_sprite, x, y, tileArea, platformGraph))
                roll < trexNeeded + raptorNeeded + compyNeeded + dodoNeeded -> sprites.add(Dodo(Res.drawable.dodo_sprite, x, y, tileArea, platformGraph))
                roll < trexNeeded + raptorNeeded + compyNeeded + dodoNeeded + parasaurNeeded -> sprites.add(Parasaur(Res.drawable.parasaur_sprite, x, y, tileArea, distanceMap, platformGraph))
                roll < trexNeeded + raptorNeeded + compyNeeded + dodoNeeded + parasaurNeeded + gallimimusNeeded -> sprites.add(Gallimimus(Res.drawable.gallimimus_sprite, x, y, tileArea, distanceMap, platformGraph))
                roll < trexNeeded + raptorNeeded + compyNeeded + dodoNeeded + parasaurNeeded + gallimimusNeeded + triceNeeded -> sprites.add(Triceratops(Res.drawable.trice_sprite, x, y, tileArea, distanceMap, platformGraph))
                else -> sprites.add(Stegosaurus(Res.drawable.stego_sprite, x, y, tileArea, distanceMap, platformGraph))
            }
        }
    }

    init {
        loadLevel(0)
    }
}

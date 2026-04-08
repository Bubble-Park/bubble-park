package fr.iutlens.mmi.demo

import fr.iutlens.mmi.demo.components.Player
import fr.iutlens.mmi.demo.components.Bullet
import fr.iutlens.mmi.demo.components.dino.GenericDino
import fr.iutlens.mmi.demo.components.dino.WalkingDino
import fr.iutlens.mmi.demo.components.dino.Trex
import fr.iutlens.mmi.demo.components.dino.Raptor
import fr.iutlens.mmi.demo.components.dino.Dodo
import fr.iutlens.mmi.demo.components.dino.Gigano
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
import fr.iutlens.mmi.demo.game.SpawnRatios
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
import fr.iutlens.mmi.demo.game.BossDifficultyConfig
import fr.iutlens.mmi.demo.game.BossConfig
import fr.iutlens.mmi.demo.game.upgrade.Upgrade
import fr.iutlens.mmi.demo.game.upgrade.UpgradeManager

class BubblePark : GameData() {

    companion object {
        // Tile de départ du joueur (startX=1.5, sizeY=23 - startY=2.5)
        private const val PLAYER_INIT_TILE_I = 1
        private const val PLAYER_INIT_TILE_J = 20
        private const val INITIAL_SPAWN_RATIO = 0.3f

        // Combo settings
        const val COMBO_TRIGGER_COUNT = 2           // nb de captures avant que le multiplicateur s'active
        const val COMBO_RESET_INTERVAL_MS = 3000L   // délai sans capture avant reset du combo (ms)

        // Bonus spawn interval (manches normales)
        const val BONUS_INTERVAL_START_MS = 12000L  // délai au niveau 1
        const val BONUS_INTERVAL_MIN_MS   = 4000L   // délai minimum atteint au niveau 15
        const val BONUS_INTERVAL_MAX_LEVEL = 14     // index du niveau où l'on atteint le minimum (niveau 15)

        // Bonus spawn interval (manches boss)
        const val BOSS_BONUS_INTERVAL_MS = 3000L
    }

    data class ScorePopup(val id: Long, val worldX: Float, val worldY: Float, val points: Int)
    val scorePopups = mutableStateListOf<ScorePopup>()
    private var popupCounter = 0L

    var bonusCollectedCount by mutableStateOf(0)
    var comboMultiplier by mutableStateOf(1)
    var comboTimeRemainingMs by mutableStateOf(0L)
    private var captureCount = 0
    private var lastCaptureMs = -1L

    /** Appelé quand un dino devient isCaptured (bullet le touche suffisamment). */
    private fun onDinoCaptured() {
        captureCount++
        lastCaptureMs = levelElapsedMs
        comboTimeRemainingMs = COMBO_RESET_INTERVAL_MS
        comboMultiplier = maxOf(1, captureCount - COMBO_TRIGGER_COUNT + 1)
    }

    /** Reset le combo si l'intervalle sans capture est dépassé. */
    private fun checkComboReset() {
        if (lastCaptureMs < 0) return
        val elapsed = levelElapsedMs - lastCaptureMs
        if (elapsed > COMBO_RESET_INTERVAL_MS) {
            captureCount = 0
            lastCaptureMs = -1L
            comboMultiplier = 1
            comboTimeRemainingMs = 0L
        } else {
            comboTimeRemainingMs = COMBO_RESET_INTERVAL_MS - elapsed
        }
    }

    /** Points accordés quand le joueur collecte un dino capturé dans la bulle. */
    private fun collectCapturedDino(scoreValue: Int, x: Float, y: Float) {
        val earned = scoreValue * comboMultiplier
        score.add(earned)
        scorePopups.add(ScorePopup(popupCounter++, x, y, earned))
    }

    val upgradeManager = UpgradeManager()
    var showBonusIntro by mutableStateOf(false)
    var showUpgradeScreen by mutableStateOf(false)
    var upgradeChoices by mutableStateOf<List<Upgrade>>(emptyList())

    val score = Score()
    var chrono = Chrono((DifficultyConfig.TOTAL_LEVEL_TIME * 1000f).toLong())
    lateinit var player: Player
    private lateinit var tileArea: TiledArea
    private lateinit var platformGraph: PlatformGraph
    private lateinit var distanceMap: DistanceMap

    val gameWorldWidth: Float
        get() = (tileArea.tileMap.geometry.sizeX * tileArea.w).toFloat()

    var onLevelEnd: ((hasNextLevel: Boolean) -> Unit)? = null
    var levelIndex by mutableStateOf(0)

    private var spawnTimerMs = 0L
    private var levelElapsedMs = 0L
    private lateinit var currentLevelDiff: LevelDifficulty
    private lateinit var spawnRatios: SpawnRatios

    private var bonusTimerMs = 0L
    private var lastBonusIndex = -1

    var isBossRound by mutableStateOf(false)
    var bossGigano: Gigano? by mutableStateOf(null)
    private var bossWaveTimerMs = 0L
    private var bossBonusTimerMs = 0L

    private val activeBullets = mutableListOf<Bullet>()
    private val activeEnemies = mutableListOf<EnemySprite>()
    private val activeGenericDinos = mutableListOf<GenericDino>()

    fun loadNextLevel() = loadLevel(levelIndex + 1)

    fun loadLevel(index: Int) {
        levelIndex = index
        currentLevelDiff = DifficultyManager.getLevelDifficulty(index + 1)
        spawnRatios = DifficultyManager.getSpawnRatios(index + 1)
        chrono = Chrono((DifficultyConfig.TOTAL_LEVEL_TIME * 1000f).toLong())
        chrono.pause()
        spawnTimerMs = 0L
        levelElapsedMs = 0L
        bonusTimerMs = 0L
        SlowEffect.reset()
        FastAmmoEffect.reset()
        comboMultiplier = 1
        comboTimeRemainingMs = 0L
        captureCount = 0
        lastCaptureMs = -1L

        val levelData = LevelGenerator.generate(index)
        val tileMap = levelData.mapString.toTileMap(levelData.mapCode)
        val decorScales = listOf('i', 'j', 'k')
            .map { levelData.mapCode.indexOf(it) }
            .filter { it >= 0 }
            .associateWith { 2f..3f }
        val isFirstLevel = !::player.isInitialized
        tileArea = TiledArea(levelData.tileSetRes, tileMap, decorScales).also {
            if (isFirstLevel) it.popDelay = 2550L
        }
        val maxLife = upgradeManager.getMaxLife()
        val savedLife = if (isFirstLevel) maxLife else player.life.coerceIn(1, maxLife)
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
            initialLife = savedLife,
            initialMaxLife = maxLife
        )
        player.spawnDelay = 0L
        upgradeManager.restoreStats(player)

        platformGraph = PlatformGraph(tileArea, jumpHeight = 6)
        distanceMap = tileArea.distanceMap(player, platformGraph)

        createGame(
            background = tileArea,
            spriteList = mutableSpriteListOf<Sprite>(player),
            transform = GenericTransform(Constraint.Fill(tileArea))
        )

        isBossRound = false
        bossGigano = null
        bossWaveTimerMs = 0L
        bossBonusTimerMs = 0L

        if (BossDifficultyConfig.isBossLevel(levelIndex)) {
            startBossRound()
        } else {
            spawnInitialDinos()
        }

        if (!isFirstLevel) {
            findSpawnPoint()?.let { (x, _) ->
                (game.spriteList as? MutableList<Sprite>)?.add(LifeBonus(x, 0f, player))
            }
        }

        game.paused = true

        game.animation(20) {
            if (player.isDeathAnimationComplete) {
                player.update()
                game.invalidate()
                return@animation
            }

            checkComboReset()
            handleCollisions()

            levelElapsedMs += 20
            if (SlowEffect.isActive) SlowEffect.timer--
            if (FastAmmoEffect.isActive) FastAmmoEffect.timer--

            if (isBossRound) {
                bossWaveTimerMs += 20
                bossBonusTimerMs += 20
                val bossConfig = BossDifficultyConfig.getConfig(levelIndex)
                if (bossWaveTimerMs >= bossConfig.spawnIntervalMs) {
                    bossWaveTimerMs = 0L
                    spawnBossWave(bossConfig.spawnCount)
                }
                if (bossBonusTimerMs >= BOSS_BONUS_INTERVAL_MS) {
                    bossBonusTimerMs = 0L
                    spawnRandomBonus()
                }
                if (bossGigano?.isDead == true) {
                    isBossRound = false
                    bossGigano = null
                    game.paused = true
                    chrono.pause()
                    val choices = upgradeManager.getRandomCandidates(3)
                    if (choices.isNotEmpty()) {
                        upgradeChoices = choices
                        showBonusIntro = true
                    } else {
                        onLevelEnd?.invoke(true)
                    }
                    return@animation
                }
            } else {
                val localDiff = DifficultyManager.updateLocalDifficulty(
                    currentLevelDiff.difficulty, levelElapsedMs / 1000f
                )
                val (spawnDelayS, currentMaxDino) = DifficultyManager.getLiveValues(localDiff)

                if (chrono.isFinished()) {
                    game.paused = true
                    onLevelEnd?.invoke(true)
                    return@animation
                }

                spawnTimerMs += 20
                if (spawnTimerMs >= (spawnDelayS * 1000).toLong()) {
                    spawnTimerMs = 0L
                    trySpawnNextDino(currentMaxDino)
                }
            }

            if (FastAmmoEffect.isActive) player.shoot()

            if (!isBossRound) {
                bonusTimerMs += 20
                val bonusInterval = run {
                    val t = (levelIndex.toFloat() / BONUS_INTERVAL_MAX_LEVEL).coerceIn(0f, 1f)
                    (BONUS_INTERVAL_START_MS - (BONUS_INTERVAL_START_MS - BONUS_INTERVAL_MIN_MS) * t).toLong()
                }
                if (bonusTimerMs >= bonusInterval) {
                    bonusTimerMs = 0L
                    spawnRandomBonus()
                }
            }

            distanceMap.update()

            val mapHeight = tileArea.tileMap.geometry.sizeY * tileArea.h
            (game.spriteList as? MutableList<Sprite>)?.removeAll { sprite ->
                (sprite is Bullet     && sprite.isStopped)
                || (sprite is EnemySprite && sprite.isDead)
                || (sprite is GenericDino && sprite.isDead)
                || (sprite is Bonus    && (sprite.collected || sprite.y > mapHeight))
            }

            game.spriteList.update()
            game.invalidate()
        }
    }

    private fun findGiganoSpawnPoint(): Pair<Float, Float>? {
        val playerTileI = floor(player.x / tileArea.w).toInt()
        val sizeX = tileArea.tileMap.geometry.sizeX
        val sizeY = tileArea.tileMap.geometry.sizeY
        val oppositeI = if (playerTileI < sizeX / 2) sizeX - 3 else 2
        for (j in sizeY - 2 downTo 0) {
            val current = tileArea.tileMap.get(oppositeI, j) ?: continue
            val below = tileArea.tileMap.get(oppositeI, j + 1) ?: continue
            if (current == 0 && (below in 1..7 || below == 14)) {  // 14 = '*' (sol)
                return oppositeI * tileArea.w + tileArea.w / 2f to (j - 4) * tileArea.h + tileArea.h / 2f
            }
        }
        return null
    }

    private fun spawnRandomBonus() {
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

    private fun startBossRound() {
        isBossRound = true
        bossWaveTimerMs = 0L
        bossBonusTimerMs = 0L
        val bossConfig = BossDifficultyConfig.getConfig(levelIndex)
        val sprites = game.spriteList as? MutableList<Sprite> ?: return
        spawnRandomBonus()
        findGiganoSpawnPoint()?.let { (x, y) ->
            val gigano = Gigano(
                res = Res.drawable.gigano_sprite,
                x = x, y = y,
                mapArea = tileArea,
                distanceMap = distanceMap,
                graph = platformGraph,
                speed = bossConfig.speed,
                hitCount = bossConfig.hitCount
            )
            gigano.elapsedSpawnDelay = tileArea.spawnEndMs()
            bossGigano = gigano
            sprites.add(gigano)
        }
    }

    private fun spawnBossWave(count: Int) {
        val sprites = game.spriteList as? MutableList<Sprite> ?: return
        repeat(count) {
            findSpawnPoint()?.let { (x, y) ->
                val dino = if (Random.nextBoolean())
                    Trex(Res.drawable.trex_sprite, x, y, tileArea, distanceMap, platformGraph)
                else
                    Raptor(Res.drawable.raptor_sprite, x, y, tileArea, distanceMap, platformGraph)
                sprites.add(dino)
            }
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
                    if (!sprite.collected && !sprite.isCollecting && sprite.boundingBox.overlaps(player.boundingBox)) {
                        sprite.startCollect()
                        bonusCollectedCount++
                        GameSound.playBonus()
                    }
                }
            }
        }

        // EnemySprite damage player
        for (enemy in activeEnemies) {
            if (enemy.stunTimer > 0) continue
            if (enemy.boundingBox.overlaps(player.boundingBox)) {
                if (player.takeDamage()) {
                    if (player.isDead) GameSound.playDown() else GameSound.playHit(player.life + 1)
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
                    if (player.isDead) GameSound.playDown() else GameSound.playHit(player.life + 1)
                    dino.stunTimer = WalkingDino.ATTACK_STUN_DURATION
                }
            }
        }

        // Joueur collecte un dino capturé
        for (dino in activeGenericDinos) {
            if (!dino.isCaptured) continue
            if (player.boundingBox.overlaps(dino.boundingBox)) {
                dino.isDead = true
                val baseScore = if (dino.capturedByDiagonal) (dino.scoreValue * 1.2f).toInt() else dino.scoreValue
                collectCapturedDino(baseScore, dino.x, dino.y)
                GameSound.playPointCombo(comboMultiplier)
                break
            }
        }

        // Bullets vs enemies
        for (bullet in activeBullets) {
            for (enemy in activeEnemies) {
                if (enemy.isDead) continue
                if (bullet.boundingBox.overlaps(enemy.boundingBox)) {
                    enemy.isDead = true
                    val points = if (bullet.isDiagonal) (enemy.scoreValue * 1.2f).toInt() else enemy.scoreValue
                    score.add(points)
                    scorePopups.add(ScorePopup(popupCounter++, enemy.x, enemy.y, points))
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
                        dino.capturedByDiagonal = bullet.isDiagonal
                        dino.currentHitCount = 0
                        onDinoCaptured()
                        bullet.capturesMade++
                        if (bullet.capturesMade >= bullet.maxCaptures) bullet.explode()
                    } else {
                        if (!dino.isStunImmune) dino.stunTimer = WalkingDino.HIT_STUN_DURATION
                        bullet.explode()
                    }
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

        val chaseTotal         = (initialCount * spawnRatios.chase).roundToInt()
        val giganoLevel        = false  // Gigano spawn uniquement via boss round
        val targetTrex         = if (giganoLevel) chaseTotal / 3 else chaseTotal / 2
        val targetRaptor       = if (giganoLevel) chaseTotal / 3 else chaseTotal - targetTrex
        val targetGigano       = if (giganoLevel) chaseTotal - targetTrex - targetRaptor else 0
        val wanderTotal        = (initialCount * spawnRatios.wander).roundToInt()
        val targetCompy        = wanderTotal / 2
        val targetDodo         = wanderTotal - targetCompy
        val fleeTotal          = (initialCount * spawnRatios.flee).roundToInt()
        val targetParasaur     = fleeTotal / 2
        val targetGallimimus   = fleeTotal - targetParasaur
        val defensiveTotal     = initialCount - chaseTotal - wanderTotal - fleeTotal
        val targetTriceratops  = defensiveTotal / 2
        val targetStegosaurus  = defensiveTotal - targetTriceratops

        var spawnedTrex = 0
        var spawnedRaptor = 0
        var spawnedGigano = 0
        var spawnedCompy = 0
        var spawnedDodo = 0
        var spawnedParasaur = 0
        var spawnedGallimimus = 0
        var spawnedTriceratops = 0
        var spawnedStegosaurus = 0
        val sprites = game.spriteList as? MutableList<Sprite> ?: return
        val dinoSpawnDelay = tileArea.spawnEndMs() + tileArea.popDelay

        repeat(initialCount) {
            val trexNeeded        = (targetTrex - spawnedTrex).coerceAtLeast(0)
            val raptorNeeded      = (targetRaptor - spawnedRaptor).coerceAtLeast(0)
            val giganoNeeded      = (targetGigano - spawnedGigano).coerceAtLeast(0)
            val compyNeeded       = (targetCompy - spawnedCompy).coerceAtLeast(0)
            val dodoNeeded        = (targetDodo - spawnedDodo).coerceAtLeast(0)
            val parasaurNeeded    = (targetParasaur - spawnedParasaur).coerceAtLeast(0)
            val gallimimusNeeded  = (targetGallimimus - spawnedGallimimus).coerceAtLeast(0)
            val triceNeeded       = (targetTriceratops - spawnedTriceratops).coerceAtLeast(0)
            val stegoNeeded       = (targetStegosaurus - spawnedStegosaurus).coerceAtLeast(0)
            val total = trexNeeded + raptorNeeded + giganoNeeded + compyNeeded + dodoNeeded + parasaurNeeded + gallimimusNeeded + triceNeeded + stegoNeeded

            if (total == 0) return@repeat

            findSpawnPoint(PLAYER_INIT_TILE_I, PLAYER_INIT_TILE_J)?.let { (x, y) ->
                val roll = Random.nextInt(total)
                fun <T : WalkingDino> T.withDelay() = also { it.elapsedSpawnDelay = dinoSpawnDelay }
                when {
                    roll < trexNeeded -> {
                        sprites.add(Trex(Res.drawable.trex_sprite, x, y, tileArea, distanceMap, platformGraph).withDelay())
                        spawnedTrex++
                    }
                    roll < trexNeeded + raptorNeeded -> {
                        sprites.add(Raptor(Res.drawable.raptor_sprite, x, y, tileArea, distanceMap, platformGraph).withDelay())
                        spawnedRaptor++
                    }
                    roll < trexNeeded + raptorNeeded + giganoNeeded -> {
                        sprites.add(Gigano(Res.drawable.gigano_sprite, x, y, tileArea, distanceMap, platformGraph).withDelay())
                        spawnedGigano++
                    }
                    roll < trexNeeded + raptorNeeded + giganoNeeded + compyNeeded -> {
                        sprites.add(Compy(Res.drawable.compy_sprite, x, y, tileArea, platformGraph).withDelay())
                        spawnedCompy++
                    }
                    roll < trexNeeded + raptorNeeded + giganoNeeded + compyNeeded + dodoNeeded -> {
                        sprites.add(Dodo(Res.drawable.dodo_sprite, x, y, tileArea, platformGraph).withDelay())
                        spawnedDodo++
                    }
                    roll < trexNeeded + raptorNeeded + giganoNeeded + compyNeeded + dodoNeeded + parasaurNeeded -> {
                        sprites.add(Parasaur(Res.drawable.parasaur_sprite, x, y, tileArea, distanceMap, platformGraph).withDelay())
                        spawnedParasaur++
                    }
                    roll < trexNeeded + raptorNeeded + giganoNeeded + compyNeeded + dodoNeeded + parasaurNeeded + gallimimusNeeded -> {
                        sprites.add(Gallimimus(Res.drawable.galliminus_sprite, x, y, tileArea, distanceMap, platformGraph).withDelay())
                        spawnedGallimimus++
                    }
                    roll < trexNeeded + raptorNeeded + giganoNeeded + compyNeeded + dodoNeeded + parasaurNeeded + gallimimusNeeded + triceNeeded -> {
                        sprites.add(Triceratops(Res.drawable.trice_sprite, x, y, tileArea, distanceMap, platformGraph).withDelay())
                        spawnedTriceratops++
                    }
                    else -> {
                        sprites.add(Stegosaurus(Res.drawable.stego_sprite, x, y, tileArea, distanceMap, platformGraph).withDelay())
                        spawnedStegosaurus++
                    }
                }
            }
        }
    }

    private fun trySpawnNextDino(maxDino: Int) {

        val activeTrex        = activeGenericDinos.count { it is Trex }
        val activeRaptor      = activeGenericDinos.count { it is Raptor }
        val activeGigano      = activeGenericDinos.count { it is Gigano }
        val activeCompy       = activeGenericDinos.count { it is Compy }
        val activeDodo        = activeGenericDinos.count { it is Dodo }
        val activeParasaur    = activeGenericDinos.count { it is Parasaur }
        val activeGallimimus  = activeGenericDinos.count { it is Gallimimus }
        val activeTriceratops = activeGenericDinos.count { it is Triceratops }
        val activeStegosaurus = activeGenericDinos.count { it is Stegosaurus }
        if (activeTrex + activeRaptor + activeGigano + activeCompy + activeDodo + activeParasaur + activeGallimimus + activeTriceratops + activeStegosaurus >= maxDino) return

        val chaseTotal        = (maxDino * spawnRatios.chase).roundToInt()
        val giganoLevel       = false  // Gigano spawn uniquement via boss round
        val targetTrex        = if (giganoLevel) chaseTotal / 3 else chaseTotal / 2
        val targetRaptor      = if (giganoLevel) chaseTotal / 3 else chaseTotal - targetTrex
        val targetGigano      = if (giganoLevel) chaseTotal - targetTrex - targetRaptor else 0
        val wanderTotal       = (maxDino * spawnRatios.wander).roundToInt()
        val targetCompy       = wanderTotal / 2
        val targetDodo        = wanderTotal - targetCompy
        val fleeTotal         = (maxDino * spawnRatios.flee).roundToInt()
        val targetParasaur    = fleeTotal / 2
        val targetGallimimus  = fleeTotal - targetParasaur
        val defensiveTotal    = maxDino - chaseTotal - wanderTotal - fleeTotal
        val targetTriceratops = defensiveTotal / 2
        val targetStegosaurus = defensiveTotal - targetTriceratops

        val trexNeeded        = (targetTrex - activeTrex).coerceAtLeast(0)
        val raptorNeeded      = (targetRaptor - activeRaptor).coerceAtLeast(0)
        val giganoNeeded      = (targetGigano - activeGigano).coerceAtLeast(0)
        val compyNeeded       = (targetCompy - activeCompy).coerceAtLeast(0)
        val dodoNeeded        = (targetDodo - activeDodo).coerceAtLeast(0)
        val parasaurNeeded    = (targetParasaur - activeParasaur).coerceAtLeast(0)
        val gallimimusNeeded  = (targetGallimimus - activeGallimimus).coerceAtLeast(0)
        val triceNeeded       = (targetTriceratops - activeTriceratops).coerceAtLeast(0)
        val stegoNeeded       = (targetStegosaurus - activeStegosaurus).coerceAtLeast(0)
        val total = trexNeeded + raptorNeeded + giganoNeeded + compyNeeded + dodoNeeded + parasaurNeeded + gallimimusNeeded + triceNeeded + stegoNeeded

        if (total == 0) return

        val sprites = game.spriteList as? MutableList<Sprite> ?: return
        findSpawnPoint()?.let { (x, y) ->
            val roll = Random.nextInt(total)
            when {
                roll < trexNeeded -> sprites.add(Trex(Res.drawable.trex_sprite, x, y, tileArea, distanceMap, platformGraph))
                roll < trexNeeded + raptorNeeded -> sprites.add(Raptor(Res.drawable.raptor_sprite, x, y, tileArea, distanceMap, platformGraph))
                roll < trexNeeded + raptorNeeded + giganoNeeded -> sprites.add(Gigano(Res.drawable.gigano_sprite, x, y, tileArea, distanceMap, platformGraph))
                roll < trexNeeded + raptorNeeded + giganoNeeded + compyNeeded -> sprites.add(Compy(Res.drawable.compy_sprite, x, y, tileArea, platformGraph))
                roll < trexNeeded + raptorNeeded + giganoNeeded + compyNeeded + dodoNeeded -> sprites.add(Dodo(Res.drawable.dodo_sprite, x, y, tileArea, platformGraph))
                roll < trexNeeded + raptorNeeded + giganoNeeded + compyNeeded + dodoNeeded + parasaurNeeded -> sprites.add(Parasaur(Res.drawable.parasaur_sprite, x, y, tileArea, distanceMap, platformGraph))
                roll < trexNeeded + raptorNeeded + giganoNeeded + compyNeeded + dodoNeeded + parasaurNeeded + gallimimusNeeded -> sprites.add(Gallimimus(Res.drawable.galliminus_sprite, x, y, tileArea, distanceMap, platformGraph))
                roll < trexNeeded + raptorNeeded + giganoNeeded + compyNeeded + dodoNeeded + parasaurNeeded + gallimimusNeeded + triceNeeded -> sprites.add(Triceratops(Res.drawable.trice_sprite, x, y, tileArea, distanceMap, platformGraph))
                else -> sprites.add(Stegosaurus(Res.drawable.stego_sprite, x, y, tileArea, distanceMap, platformGraph))
            }
        }
    }

    fun startUpgradeFromBonus() {
        showBonusIntro = false
        showUpgradeScreen = true
    }

    fun selectUpgrade(upgrade: Upgrade) {
        upgradeManager.acquire(upgrade, player)
        showUpgradeScreen = false
        upgradeChoices = emptyList()
        game.paused = false
        onLevelEnd?.invoke(true)
    }

    init {
        loadLevel(4)
    }
}

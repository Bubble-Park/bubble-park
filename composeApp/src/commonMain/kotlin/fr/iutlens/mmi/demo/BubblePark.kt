package fr.iutlens.mmi.demo

import fr.iutlens.mmi.demo.components.Player
import fr.iutlens.mmi.demo.components.Bullet
import fr.iutlens.mmi.demo.components.GenericDino
import fr.iutlens.mmi.demo.components.Trex
import fr.iutlens.mmi.demo.components.Parasaur
import fr.iutlens.mmi.demo.data.LevelData
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
import kotlin.math.PI
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.math.floor
import kotlin.math.abs

class BubblePark : GameData() {

    val score = Score()
    val chrono by lazy { Chrono() }
    lateinit var player: Player
    private lateinit var tileArea: TiledArea
    private lateinit var platformGraph: PlatformGraph
    private lateinit var distanceMap: DistanceMap

    private var nextShotTime = 0L

    private var spawnTimerMs = 0L
    private var levelElapsedMs = 0L
    private lateinit var currentLevelDiff: LevelDifficulty

    private val activeBullets = mutableListOf<Bullet>()
    private val activeEnemies = mutableListOf<EnemySprite>()
    private val activeGenericDinos = mutableListOf<GenericDino>()

    private val levels = listOf(
        LevelData(
            mapString = """
                ..............................
                ..............................
                ..............................
                ..............................
                ...ef^?^?^?^?^?^?^?^?^?^?ab...
                ...gh%)%)%)%)%)%)%)%)%)%)cd...
                ..............................
                ..............................
                ..............................
                ..............................
                ...ef^?^?^?^?^?^?^?^?^?^?ab...
                ...gh%)%)%)%)%)%)%)%)%)%)cd...
                ..............................
                ..............................
                ..............................
                ..............................
                ...ef^?^?^?^?^?^?^?^?^?^?ab...
                ...gh%)%)%)%)%)%)%)%)%)%)cd...
                ..............................
                ..............................
                ..............................
                ******************************
                ##############################
            """.trimIndent(),
            tileSetRes = Res.drawable.environnement_map_sprite,
            startX = 1.5f,
            startY = 2.5f,
            mapCode = ".abef^?#ghcd)%*"
        )
    )

    fun loadLevel(index: Int) {
        currentLevelDiff = DifficultyManager.getLevelDifficulty(index + 1)
        spawnTimerMs = 0L
        levelElapsedMs = 0L

        val levelData = levels[index]
        val tileMap = levelData.mapString.toTileMap(levelData.mapCode)
        tileArea = TiledArea(levelData.tileSetRes, tileMap)

        player = Player(
            res = Res.drawable.bubblechtein_sprites,
            x = levelData.startX * tileArea.w,
            y = (tileMap.geometry.sizeY - levelData.startY) * tileArea.h,
            mapArea = tileArea,
            joystickProvider = { game.joystickPosition },
            jumpActionProvider = { game.actionButtonB }
        )

        platformGraph = PlatformGraph(tileArea, jumpHeight = 6)
        distanceMap = tileArea.distanceMap(player, platformGraph)

        createGame(
            background = tileArea,
            spriteList = mutableSpriteListOf<Sprite>(player),
            transform = GenericTransform(Constraint.Fill(tileArea))
        )

        game.animation(20) {
            handleCollisions()

            levelElapsedMs += 20
            spawnTimerMs += 20

            val localDiff = DifficultyManager.updateLocalDifficulty(
                currentLevelDiff.difficulty, levelElapsedMs / 1000f
            )
            val (spawnDelayS, currentMaxDino) = DifficultyManager.getLiveValues(localDiff)

            if (spawnTimerMs >= (spawnDelayS * 1000).toLong()) {
                spawnTimerMs = 0L
                trySpawnNextDino(currentMaxDino)
            }

            distanceMap.update()

            (game.spriteList as? MutableList<Sprite>)?.apply {
                removeAll { (it as? Bullet)?.isStopped == true }
                removeAll { (it as? EnemySprite)?.isDead == true }
                removeAll { (it as? GenericDino)?.isDead == true }
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
            if (!dino.type.damagesPlayer) continue
            if (dino.boundingBox.overlaps(player.boundingBox)) {
                player.takeDamage()
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
                if (dino.isDead) continue
                if (bullet.boundingBox.overlaps(dino.boundingBox)) {
                    dino.isDead = true
                    score.add(dino.scoreValue)
                    chrono.addTime(dino.type.timeBonus.toFloat())
                    bullet.explode()
                    break
                }
            }
        }
    }

    private fun findSpawnPoint(): Pair<Float, Float>? {
        val playerI = floor(player.x / tileArea.w).toInt()
        val playerJ = floor(player.y / tileArea.h).toInt()

        fun collectSpawns(minDistI: Int, minDistJ: Int): List<Pair<Int, Int>> {
            val result = mutableListOf<Pair<Int, Int>>()
            for (i in 0 until tileArea.tileMap.geometry.sizeX) {
                for (j in 0 until tileArea.tileMap.geometry.sizeY - 1) {
                    val current = tileArea.tileMap.get(i, j) ?: 0
                    val below = tileArea.tileMap.get(i, j + 1) ?: 0
                    if (current == 0 && below in 1..7) {
                        if (abs(i - playerI) > minDistI && abs(j - playerJ) > minDistJ) {
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

    private fun trySpawnNextDino(maxDino: Int) {
        val activeTrex = activeGenericDinos.count { it is Trex }
        val activeParasaur = activeGenericDinos.count { it is Parasaur }
        if (activeTrex + activeParasaur >= maxDino) return

        val targetTrex = (maxDino * DifficultyConfig.RATIO_CHASE).roundToInt()
        val targetParasaur = maxDino - targetTrex

        val spawnChase = activeTrex < targetTrex
        val spawnFlee = activeParasaur < targetParasaur
        if (!spawnChase && !spawnFlee) return

        val sprites = game.spriteList as? MutableList<Sprite> ?: return
        findSpawnPoint()?.let { (x, y) ->
            if (spawnChase) sprites.add(Trex(Res.drawable.trex_sprite, x, y, tileArea, distanceMap, platformGraph))
            else sprites.add(Parasaur(Res.drawable.bubble_sprite, x, y, tileArea, distanceMap, platformGraph))
        }
    }

    fun shoot(enableCollisions: Boolean = false, delayMs: Long = 300) {
        val now = game.elapsed
        if (now < nextShotTime) return
        nextShotTime = now + delayMs

        val step = PI / 4
        val quantizedAngle = round(player.lastAngle / step) * step
        val bullet = Bullet(player.x, player.y, quantizedAngle, tileArea, collides = enableCollisions, res = Res.drawable.bubble_sprite)
        (game.spriteList as? MutableList<Sprite>)?.add(bullet)
    }

    init {
        loadLevel(0)
    }
}

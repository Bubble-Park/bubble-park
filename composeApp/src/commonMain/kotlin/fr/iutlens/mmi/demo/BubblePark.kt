package fr.iutlens.mmi.demo

import fr.iutlens.mmi.demo.components.Player
import fr.iutlens.mmi.demo.components.Bullet
import fr.iutlens.mmi.demo.components.GenericDino
import fr.iutlens.mmi.demo.components.Trex
import fr.iutlens.mmi.demo.components.Parasaur
import fr.iutlens.mmi.demo.data.LevelData
import fr.iutlens.mmi.demo.game.Chrono
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
import kotlin.random.Random
import kotlin.math.floor
import kotlin.math.abs

class BubblePark : GameData() {

    val score = Score()
    val chrono = Chrono()
    lateinit var player: Player
    private lateinit var tileArea: TiledArea
    private lateinit var platformGraph: PlatformGraph
    private lateinit var distanceMap: DistanceMap

    private var nextShotTime = 0L

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

        findSpawnPoint()?.let { (spawnX, spawnY) ->
            val sprites = game.spriteList as? MutableList<Sprite> ?: return@let
            sprites.add(Trex(Res.drawable.trex_sprite, spawnX, spawnY, tileArea, distanceMap, platformGraph))
            sprites.add(Parasaur(Res.drawable.bubble_sprite, spawnX, spawnY, tileArea, distanceMap, platformGraph))
        }

        game.animation(20) {
            chrono.update(20)
            handleCollisions()

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
                    bullet.explode()
                    break
                }
            }
        }
    }

    private fun findSpawnPoint(): Pair<Float, Float>? {
        val validSpawns = mutableListOf<Pair<Int, Int>>()
        val playerI = floor(player.x / tileArea.w).toInt()
        val playerJ = floor(player.y / tileArea.h).toInt()

        for (i in 0 until tileArea.tileMap.geometry.sizeX) {
            for (j in 0 until tileArea.tileMap.geometry.sizeY - 1) {
                val currentCode = tileArea.tileMap.get(i, j) ?: 0
                val belowCode = tileArea.tileMap.get(i, j + 1) ?: 0

                if (currentCode == 0 && belowCode in 1..7) {
                    if (abs(i - playerI) > 3 || j != playerJ) {
                        validSpawns.add(Pair(i, j))
                    }
                }
            }
        }

        if (validSpawns.isEmpty()) return null
        val spawnPoint = validSpawns[Random.nextInt(validSpawns.size)]
        val spawnX = spawnPoint.first * tileArea.w + tileArea.w / 2f
        val spawnY = spawnPoint.second * tileArea.h + tileArea.h / 2f
        return Pair(spawnX, spawnY)
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

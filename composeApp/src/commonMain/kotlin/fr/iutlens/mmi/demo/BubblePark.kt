package fr.iutlens.mmi.demo


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.iutlens.mmi.demo.components.Player
import fr.iutlens.mmi.demo.components.Bullet
import fr.iutlens.mmi.demo.data.LevelData
import fr.iutlens.mmi.demo.game.GameData
import fr.iutlens.mmi.demo.game.GameView
import fr.iutlens.mmi.demo.game.sprite.Sprite
import fr.iutlens.mmi.demo.game.sprite.TiledArea
import fr.iutlens.mmi.demo.game.sprite.mutableSpriteListOf
import fr.iutlens.mmi.demo.game.sprite.toTileMap
import fr.iutlens.mmi.demo.game.transform.Constraint
import fr.iutlens.mmi.demo.game.transform.GenericTransform
import fr.iutlens.mmi.demo.utils.SpriteSheet
import kotlin.math.PI
import kotlin.math.round

class BubblePark : GameData() {

    private lateinit var player: Player
    private lateinit var tileArea: TiledArea

    private var nextShotTime = 0L

    private val levels = listOf(
        LevelData(
            mapString = """
                ..............................
                ..............................
                ..............................
                ...l######################r...
                ..............................
                ..............................
                ..............................
                ..............................
                ...l######################r...
                ..............................
                ..............................
                ..............................
                ..............................
                ...l######################r...
                ..............................
                ..............................
                ..............................
                ..............................
                ...l######################r...
                ..............................
                ..............................
                ..............................
                ..............................
                l############################r
            """.trimIndent(),
            tileSetRes = Res.drawable.plateformes_spritesheet,
            startX = 1.5f,
            startY = 2.5f,
            mapCode = ".l#r"
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

        createGame(
            background = tileArea,
            spriteList = mutableSpriteListOf<Sprite>(player),
            transform = GenericTransform(Constraint.Fill(tileArea))
        )

        game.animation(20) {
            (game.spriteList as? MutableList<Sprite>)?.apply {
                removeAll { (it as? Bullet)?.isStopped == true }
            }
            game.spriteList.update()
            game.invalidate()
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


@Preview(showBackground = true, device = "spec:width=852dp,height=393dp,dpi=240")
@Composable
fun BubbleParkPreview() {
    SpriteSheet.load(Res.drawable.niveau1_fond, 1, 1)
    SpriteSheet.load(Res.drawable.plateformes_spritesheet, 4, 1)
    SpriteSheet.load(Res.drawable.bubblechtein_sprites, 10, 3, filterQuality = FilterQuality.High)
    SpriteSheet.load(Res.drawable.bubble_sprite, 4, 3, filterQuality = FilterQuality.High)
    val gameData = BubblePark()

    Box(Modifier.fillMaxSize()) {

        androidx.compose.foundation.Image(
            painter = org.jetbrains.compose.resources.painterResource(Res.drawable.niveau1_fond),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.FillBounds
        )

        GameView(
            modifier = Modifier.fillMaxSize(), // On a retiré .background(Color.DarkGray)
            gameData = gameData
        )

        Joystick(modifier = Modifier
            .size(200.dp)
            .align(Alignment.BottomStart)
            .padding(32.dp)
        ) { gameData.game.joystickPosition = it }
    }
}

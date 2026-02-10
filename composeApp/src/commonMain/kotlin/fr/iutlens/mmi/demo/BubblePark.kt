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

    private val levels = listOf(
        LevelData(
            mapString = """
                ..............................
                ..............................
                ..............................
                ...#|#|#|#|#|#|#|#|#|#|#|#|...
                ..............................
                ..............................
                ..............................
                ..............................
                ...#|#|#|#|#|#|#|#|#|#|#|#|...
                ..............................
                ..............................
                ..............................
                ..............................
                ...#|#|#|#|#|#|#|#|#|#|#|#|...
                ..............................
                ..............................
                ..............................
                ..............................
                ...#|#|#|#|#|#|#|#|#|#|#|#|...
                ..............................
                ..............................
                ..............................
                ..............................
                #|#|#|#|#|#|#|#|#|#|#|#|#|#|#!
            """.trimIndent(),
            tileSetRes = Res.drawable.sprites_bubblepark_map_v1,
            startX = 1.5f,
            startY = 2.5f,
            mapCode = ".#|"
        )
    )

    fun loadLevel(index: Int) {
        val levelData = levels[index]
        val tileMap = levelData.mapString.toTileMap(levelData.mapCode)
        val tileArea = TiledArea(levelData.tileSetRes, tileMap)
        
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

    fun shoot() {
        val step = PI / 4
        val quantizedAngle = round(player.lastAngle / step) * step
        val bullet = Bullet(player.x, player.y, quantizedAngle)
        (game.spriteList as? MutableList<Sprite>)?.add(bullet)
    }

    init {
        loadLevel(0)
    }
}


@Preview(showBackground = true, device = "spec:width=852dp,height=393dp,dpi=240")
@Composable
fun BubbleParkPreview() {
    SpriteSheet.load(Res.drawable.sprites_bubblepark_map_v1, 3, 1)
    SpriteSheet.load(Res.drawable.bubblechtein_sprites, 10, 3, filterQuality = FilterQuality.High)
    val gameData = BubblePark()

    Box(Modifier.fillMaxSize()) {
        GameView(modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray),
            gameData)
        Joystick(modifier = Modifier
            .size(200.dp)
            .align(Alignment.BottomStart)
            .padding(32.dp)
        ) { gameData.game.joystickPosition = it }
    }
}

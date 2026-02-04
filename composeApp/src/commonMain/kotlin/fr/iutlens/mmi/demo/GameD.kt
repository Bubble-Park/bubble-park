package fr.iutlens.mmi.demo

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import fr.iutlens.mmi.demo.game.GameData
import fr.iutlens.mmi.demo.game.GameView
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.game.sprite.spriteListOf
import fr.iutlens.mmi.demo.game.sprite.tiledArea
import fr.iutlens.mmi.demo.game.sprite.toHexTileMap
import fr.iutlens.mmi.demo.game.transform.Constraint
import fr.iutlens.mmi.demo.game.transform.GenericTransform
import fr.iutlens.mmi.demo.utils.SpriteSheet


class GameD : GameData() {
    val map = """
        .....
        ....o
        .o...
        ..o..
        .....
        """.trimIndent().toHexTileMap("o.")
    val tiledArea = Res.drawable.hex.tiledArea(map)

    init {
        createGame(
            background = tiledArea,
            spriteList = spriteListOf<BasicSprite>(),
            transform = GenericTransform(
                Constraint.Fill(tiledArea)
            )
        )
        game.onTap = ::changeState
    }

    fun changeState(coord : Offset){
        val (x,y) = coord
        val i = x / tiledArea.w
        val j = y / tiledArea.h
        val value = tiledArea.tileMap[i, j]
        if (value != null) map[i, j] = 1 - value
    }
}

@Preview
@Composable
fun GameDPreview() {
    SpriteSheet.load(Res.drawable.hex, 2, 1)
    val gameData = GameD()
    GameView(modifier = Modifier.fillMaxSize(),gameData)
}
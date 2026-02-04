package fr.iutlens.mmi.demo

import androidx.compose.ui.geometry.Offset
import fr.iutlens.mmi.demo.game.GameData
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.game.sprite.mutableSpriteListOf
import fr.iutlens.mmi.demo.game.sprite.tiledArea
import fr.iutlens.mmi.demo.game.sprite.toHexTileMap
import fr.iutlens.mmi.demo.game.transform.Constraint
import fr.iutlens.mmi.demo.game.transform.GenericTransform


class GameD : GameData() {
    val map = """
        .....
        ....o
        .o...
        ..o..
        .....
        """.trimIndent().toHexTileMap("o.")
    val tiledArea = Res.drawable.hex.tiledArea(map)
    val list = mutableSpriteListOf<BasicSprite>() // Notre liste de sprites

    init {
        createGame(
            background = tiledArea,
            spriteList = list,
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


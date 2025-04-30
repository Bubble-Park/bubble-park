package fr.iutlens.mmi.demo


import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.game.sprite.contains
import fr.iutlens.mmi.demo.game.sprite.get
import fr.iutlens.mmi.demo.game.sprite.mutableSpriteListOf
import fr.iutlens.mmi.demo.game.sprite.tiledArea
import fr.iutlens.mmi.demo.game.sprite.toHexTileMap
import fr.iutlens.mmi.demo.game.transform.Constraint
import fr.iutlens.mmi.demo.game.transform.GenericTransform

fun makeGameD(): Game {
    val map = """
        .....
        ....o
        .o...
        ..o..
        .....
        """.trimIndent().toHexTileMap("o.")
    val tiledArea = Res.drawable.hex.tiledArea(map)
    val list = mutableSpriteListOf<BasicSprite>() // Notre liste de sprites

    val game = Game(background = tiledArea,
        spriteList = list,
        transform = GenericTransform(
            Constraint.Fill(tiledArea)
        )
    ){ (x,y) ->
        val i = x /tiledArea.w
        val j = y /tiledArea.h
        val value = tiledArea.tileMap[i,j]
        if (value != null) map[i,j] = 1 - value
    }

    return game
}
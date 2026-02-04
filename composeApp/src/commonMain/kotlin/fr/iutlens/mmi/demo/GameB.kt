package fr.iutlens.mmi.demo


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import fr.iutlens.mmi.demo.game.GameData
import fr.iutlens.mmi.demo.game.GameView
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.game.sprite.Sprite
import fr.iutlens.mmi.demo.game.sprite.TiledArea
import fr.iutlens.mmi.demo.game.sprite.get
import fr.iutlens.mmi.demo.game.sprite.mutableSpriteListOf
import fr.iutlens.mmi.demo.game.sprite.toTileMap
import fr.iutlens.mmi.demo.game.transform.Constraint
import fr.iutlens.mmi.demo.game.transform.GenericTransform
import fr.iutlens.mmi.demo.utils.SpriteSheet

import kotlin.random.Random


class GameB : GameData() {

    val map = """
        1222232222225
        677778777777A
        BCCCCCCCCCCCG
        BCCCCCCCCCCCG
        BCCCCCCCCCCCG
        BCCCCCCCCCCCG
        BCCCCCCCCCCCG
        BCCCCCCCCCCCG
        BCCCCCCCCCCCG
        122DE222DE225
        677IJ777IJ77A
    """.trimIndent().toTileMap(
        "12345" +
                "6789A" +
                "BCDEF" +
                "GHIJK")
    val tiledArea = TiledArea(Res.drawable.decor,map)
    val spriteList = mutableSpriteListOf<BasicSprite>() // Notre liste de sprites

    init {
        repeat(7){ // On crée plusieurs sprites aléatoires
            spriteList.add(
                BasicSprite(
                    Res.drawable.perso,
                    (tiledArea.tileMap.geometry.sizeX*Random.nextFloat()*tiledArea.w),
                    (tiledArea.tileMap.geometry.sizeY*Random.nextFloat()*tiledArea.h),
                    (0..2).random())
            )
        }

        createGame(background = tiledArea,
            spriteList = spriteList,
            transform = GenericTransform(
                Constraint.Fill(tiledArea)
            )
        )

        var current : Sprite? = null

        game.onDragStart = { (x,y) ->
            current = spriteList[x, y]
        }

        game.onDragMove = { (x,y)->
            (current as? BasicSprite)?.let {
                it.x = x
                it.y = y
                game.invalidate()
            }
        }
    }
}

@Composable
fun GameBPreview() {
    SpriteSheet.load(Res.drawable.decor, 5, 4)
    SpriteSheet.load(Res.drawable.perso, 3, 1)
    val game = GameB()

    GameView(modifier = Modifier.fillMaxSize(),game)

}
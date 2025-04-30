package fr.iutlens.mmi.demo


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import fr.iutlens.mmi.demo.game.Game
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

fun makeGameB(): Game {
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
    val tileMap = TiledArea(Res.drawable.decor,map)
   // val sprite = BasicSprite(R.drawable.car,3.5f*tileMap.w,2.5f*tileMap.h)
    val list = mutableSpriteListOf<BasicSprite>() // Notre liste de sprites
    repeat(7){ // On crée plusieurs sprites aléatoires
        list.add(
            BasicSprite(
                Res.drawable.perso,
            (tileMap.tileMap.geometry.sizeX*Random.nextFloat()*tileMap.w),
            (tileMap.tileMap.geometry.sizeY*Random.nextFloat()*tileMap.h),
            (0..2).random())
        )
    }


    val game = Game(background = tileMap,
        spriteList = list,
        transform = GenericTransform(
            Constraint.Fill(tileMap)
        )
    )

    var current : Sprite? = null

    game.onDragStart = { (x,y) ->
        current = list[x, y]
    }

    game.onDragMove = { (x,y)->
        (current as? BasicSprite)?.let {
            it.x = x
            it.y = y
            game.invalidate()
        }
    }

    return game
}


@Composable
fun GameBPreview() {
    SpriteSheet.load(Res.drawable.decor, 5, 4)
    SpriteSheet.load(Res.drawable.perso, 3, 1)
    val game = makeGameB()

    game.View(modifier = Modifier.fillMaxSize())

}
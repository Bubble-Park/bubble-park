package fr.iutlens.mmi.demo


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.game.sprite.EnemySprite
import fr.iutlens.mmi.demo.game.sprite.TiledArea
import fr.iutlens.mmi.demo.game.sprite.compose
import fr.iutlens.mmi.demo.game.sprite.spriteListOf
import fr.iutlens.mmi.demo.game.sprite.toMutableTileMap
import fr.iutlens.mmi.demo.game.transform.Constraint
import fr.iutlens.mmi.demo.game.transform.GenericTransform
import fr.iutlens.mmi.demo.utils.Music
import fr.iutlens.mmi.demo.utils.SpriteSheet
import fr.iutlens.mmi.demo.utils.distanceMap


import kotlin.math.floor

fun makeGameA(): Game {
    val map = """
            ####.################.#######
            #.............#.....#.......#
            #.#####.#####...###.#.#.###.#
            #.....#.#.....#...#.#.#.....#
            #####...#.#######.#...#.#####
            #.....#.....#...#.#.#.......#
            #.###.#####.#.#.#.#.#.#####.#
            #.............#.............#
        """.trimIndent().toMutableTileMap(
              "!-^¨I" +
                   "'_HTJ" +
                   "|.() " +
                   "L#[] ")

    val doubleMap = map.compose(0,map.sizeY,map) // Etend map en ajoutant une copie de map en 0,map.sizeY
    val tileMap = TiledArea(Res.drawable.decor,doubleMap)
    val sprite = BasicSprite(Res.drawable.perso,3.5f*tileMap.w,3.5f*tileMap.h)

    // Création de la carte des distances
    val distance = tileMap.distanceMap(sprite){ i,j -> get(i,j) == 11}

    //On construit la liste de sprites, avec des ennemis
    val spriteList = spriteListOf(sprite,
        EnemySprite(Res.drawable.perso,23.5f*tileMap.w,1.5f*tileMap.h,distance,0.05f),
        EnemySprite(Res.drawable.perso,3.5f*tileMap.w,13.5f*tileMap.h,distance),
        EnemySprite(Res.drawable.perso,23.5f*tileMap.w,13.5f*tileMap.h,distance),
        )

    val game =  Game(background = tileMap,
        spriteList = spriteList,
        transform = GenericTransform(
            Constraint.Fill(tileMap) // sprite est centré (verticalement), et on affiche au moins 8 cases
            )
    ).apply {
        padAction = {(dx,dy) ->
            val nextX = sprite.x + dx * tileMap.w
            val nextY = sprite.y + dy * tileMap.h
            if (tileMap.possible(nextX,nextY)){
                sprite.x = nextX
                sprite.y = nextY
                distance.update() // Mise à jour des distances
                invalidate()
            }
            Music.playSound("files/message.mp3")

        }
    }

    // Animation toutes les 20ms (pour bouger les ennemis)
    game.animationDelayMs = 20
    game.update = {
        spriteList.update()
        game.invalidate()
    }
    return game

}

fun TiledArea.possible(x: Float, y: Float): Boolean {
    val i =  floor(x / w).toInt()
    val j =  floor(y / h).toInt()
    if ( i !in 0..< sizeX || j !in 0 ..< sizeY) return false
    val code = get(i,j)
    return code == 11
}


//@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun GameAPreview() {
    SpriteSheet.load(Res.drawable.decor, 5, 4)
    SpriteSheet.load(Res.drawable.perso, 3, 1)
    val game = makeGameA()
    Box(Modifier.fillMaxSize()){
        game.View(modifier = androidx.compose.ui.Modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Color.Black))
        val action = game.padAction ?: return@Box
        Pad(
            Modifier
                .size(200.dp)
                .align(Alignment.BottomStart)
                .padding(16.dp),
            action = action
        )
    }
}
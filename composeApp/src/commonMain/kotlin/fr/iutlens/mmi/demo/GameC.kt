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
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.ui.unit.dp
import fr.iutlens.mmi.demo.game.GameData
import fr.iutlens.mmi.demo.game.GameView
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.game.sprite.TiledArea

import fr.iutlens.mmi.demo.game.sprite.spriteListOf
import fr.iutlens.mmi.demo.game.sprite.toTileMap
import fr.iutlens.mmi.demo.game.transform.Constraint

import fr.iutlens.mmi.demo.game.transform.GenericTransform
import fr.iutlens.mmi.demo.utils.SpriteSheet



class GameC : GameData() {

    val map = """
        ----^------^-----^--^¨--¨-------¨--
        ____H______H_____H__HT__T_______T__
        ...................................
        ...................................
        !---^-I----^-I---^-|..L-¨----!--¨--
        '___H_J____H_J___H_|..L_T____'__T__
        |.....L......L.....|..L......|.....
        |.....L......L.....|..L......|.....
        !--()----()----()--|..L--()----()--
        '__[]____[]____[]__|..L__[]____[]__
        ###################!^¨I############
        ###################'HTJ############
    """.trimIndent().toTileMap(
        "!-^¨I" +
                "'_HTJ" +
                "|.() " +
                "L#[] ")
    val tiledArea = TiledArea(Res.drawable.decor,map)

    val sprite = BasicSprite(Res.drawable.perso,3.5f*tiledArea.w,2f*tiledArea.h)

    init {
        createGame(background = tiledArea,
            spriteList = spriteListOf(sprite),
            transform = GenericTransform(
                Constraint.Focus(tiledArea,sprite,10)
            )
        )

        game.animation(20)  {
            game.invalidate()
            val position = game.joystickPosition ?: return@animation
            if (!position.isCentered){
                    sprite.x += position.x*tiledArea.w/4
                    sprite.y += position.y*tiledArea.h/4
                }
            }
        }
}

@Preview
@Composable
fun GameCPreview() {
    SpriteSheet.load(Res.drawable.decor, 5, 4)
    SpriteSheet.load(Res.drawable.perso, 3, 1)
    val gameData = GameC()

    Box(Modifier.fillMaxSize()){
        GameView(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
            gameData)
        Joystick(modifier = Modifier
            .size(200.dp)
            .align(Alignment.BottomStart)
            .padding(16.dp),
        ) { gameData.game.joystickPosition = it }
    }
}
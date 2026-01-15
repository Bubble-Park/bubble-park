package fr.iutlens.mmi.demo.game

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import fr.iutlens.mmi.demo.game.sprite.Sprite
import fr.iutlens.mmi.demo.game.sprite.SpriteList
import fr.iutlens.mmi.demo.game.transform.CameraTransform

open class GameData {

    lateinit var game  : Game

    fun createGame(
        background : Sprite,
        spriteList : SpriteList<*>,
        transform: CameraTransform,
        onDragStart: (Game.(Offset) -> Unit)? = null,
        onDragMove:  (Game.(Offset) -> Unit)? = null,
        onTap: (Game.(Offset)-> Unit)? = null
    ) : Game {
        game = Game(background,spriteList,transform,onDragStart,onDragMove,onTap)
        return game
    }
}

@Composable
fun GameView(modifier : Modifier = Modifier, gameData: GameData){
    gameData.game.View(modifier)
}
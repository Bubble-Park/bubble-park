package fr.iutlens.mmi.demo.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import fr.iutlens.mmi.demo.game.sprite.Sprite
import fr.iutlens.mmi.demo.game.sprite.SpriteList
import fr.iutlens.mmi.demo.game.transform.CameraTransform

open class GameData {

    private var _game: Game? by mutableStateOf(null)
    var game: Game
        get() = _game ?: error("Game not initialized")
        set(value) { _game = value }

    fun createGame(
        background : Sprite,
        spriteList : SpriteList<*>,
        transform: CameraTransform
    ) : Game {
        game = Game(background,spriteList,transform)
        return game
    }
}

@Composable
fun GameView(modifier : Modifier = Modifier, gameData: GameData){
    gameData.game.View(modifier)
}
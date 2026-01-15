package fr.iutlens.mmi.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.unit.dp
import fr.iutlens.mmi.demo.game.GameView
import fr.iutlens.mmi.demo.utils.Music
import fr.iutlens.mmi.demo.utils.SpriteSheet

@Composable
fun MultiGame(modifier : Modifier = Modifier, selection : Int){
    SpriteSheet.load(Res.drawable.decor,  5, 4, 1)
    SpriteSheet.load(Res.drawable.hex,  2, 1, 1)
    SpriteSheet.load(Res.drawable.perso,  3, 1, filterQuality = FilterQuality.High)
    Music.loadSound("files/message.mp3")


    val gameA = remember(SpriteSheet[Res.drawable.decor]) { GameA() }
    val gameB = remember(SpriteSheet[Res.drawable.decor]) { GameB() }
    val gameC = remember(SpriteSheet[Res.drawable.decor]) { GameC() }
    val gameD = remember(SpriteSheet[Res.drawable.decor]) { GameD() }
    val game = arrayOf(gameA,gameB,gameC,gameD)[selection]


    Box(modifier) {
        GameView(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            game
        )
        game.game.padAction?.let { action ->
            Pad(
                Modifier
                    .size(200.dp)
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                action = action
            )
        }
        if (game == gameC) {
            Joystick(
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
            ) { game.game.joystickPosition = it }
        }
    }
}
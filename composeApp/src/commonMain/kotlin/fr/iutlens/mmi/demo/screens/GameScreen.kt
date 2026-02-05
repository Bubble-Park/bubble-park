package fr.iutlens.mmi.demo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.unit.dp
import fr.iutlens.mmi.demo.BubblePark
import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.bubblechtein_sprites
import fr.iutlens.mmi.demo.game.GameView
import fr.iutlens.mmi.demo.sprites_bubblepark_map_v1
import fr.iutlens.mmi.demo.ui.Controllers
import fr.iutlens.mmi.demo.utils.SpriteSheet

@Composable
fun GameScreen(onExit: () -> Unit) {
    SpriteSheet.load(Res.drawable.sprites_bubblepark_map_v1, 3, 1)
    SpriteSheet.load(Res.drawable.bubblechtein_sprites, 10, 3, filterQuality = FilterQuality.High)

    val gameData = remember { BubblePark() }

    Box(Modifier.fillMaxSize()) {
        GameView(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.DarkGray),
            gameData = gameData
        )

        // Contrôles
        Controllers(
            modifier = Modifier.fillMaxSize(),
            onJoystickChange = { gameData.game.joystickPosition = it },
            onActionA = { pressed -> /* Action A (Saut ?) */ },
            onActionB = { pressed -> /* Action B (Attaque ?) */ }
        )
    }
}

package fr.iutlens.mmi.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.unit.dp
import fr.iutlens.mmi.demo.utils.Music
import fr.iutlens.mmi.demo.utils.Music.mute
import fr.iutlens.mmi.demo.utils.SpriteSheet
import fr.iutlens.mmi.demo.utils.settings
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview



@Composable
@Preview
fun App() {
    MaterialTheme {
        SpriteSheet.load(Res.drawable.decor,  5, 4, 1)
        SpriteSheet.load(Res.drawable.perso,  3, 1, filterQuality = FilterQuality.High)
        Music.loadSound("files/message.mp3")

        Music("files/jungle.mp3")

        val gameA = remember(SpriteSheet[Res.drawable.decor]) { makeGameA() }
        val gameB = remember(SpriteSheet[Res.drawable.decor]) { makeGameB() }
        val gameC = remember(SpriteSheet[Res.drawable.decor]) { makeGameC() }
        var count by remember { settings.int["count", 0] }

        var game by remember(gameA) { mutableStateOf(gameA) }
        Box(Modifier.fillMaxSize()) {
            game.View(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            )
            game.padAction?.let { action ->
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
                ) { game.joystickPosition = it }
            }
        }

        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.padding(horizontal = 4.dp)) {
            Button(modifier = Modifier.padding(4.dp), onClick = { game = gameA }) {
                Text(text = "Game A")
            }
            Button(modifier = Modifier.padding(4.dp), onClick = { game = gameB }) {
                Text(text = "Game B")
            }
            Button(modifier = Modifier.padding(4.dp), onClick = { game = gameC }) {
                Text(text = "Game C")
            }
            Button(modifier = Modifier.padding(4.dp), onClick = { count++}) {
                Text(text = "$count")
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(modifier = Modifier.padding(4.dp), onClick = { mute = !mute }) {
                Icon(painter = painterResource(
                    if (mute) Res.drawable.volume_mute
                    else Res.drawable.volume_full),"")
            }
        }
    }
}
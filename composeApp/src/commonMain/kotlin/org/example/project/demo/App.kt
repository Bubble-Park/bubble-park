package fr.iutlens.mmi.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.iutlens.mmi.demo.utils.SpriteSheet
import kmptest.composeapp.generated.resources.Res
import kmptest.composeapp.generated.resources.decor
import kmptest.composeapp.generated.resources.perso
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun MyApp(){
    SpriteSheet.load(Res.drawable.decor, 5, 4, 1)
    SpriteSheet.load(Res.drawable.perso, 3, 1)


    val gameA = remember({makeGameA()})
    val gameB = remember({makeGameB()})
    val gameC = remember({makeGameC()})

    var game by remember{ mutableStateOf(gameA) }
    Box(Modifier.fillMaxSize()){
        game.View(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black))
        game.padAction?.let { action ->
            Pad(
                Modifier
                    .size(200.dp)
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                action = action)
        }
        if (game == gameC){
            Joystick(modifier = Modifier
                .size(200.dp)
                .align(Alignment.BottomStart)
                .padding(16.dp),
                ) { game.joystickPosition = it }
        }
    }
    Row( horizontalArrangement = Arrangement.SpaceBetween){
        Button(modifier = Modifier.padding(4.dp), onClick = { game = gameA }) {
            Text(text = "Game A")
        }
        Button(modifier = Modifier.padding(4.dp), onClick = { game = gameB }) {
            Text(text = "Game B")
        }
        Button(modifier = Modifier.padding(4.dp), onClick = { game = gameC }) {
            Text(text = "Game C")
        }
    }
}






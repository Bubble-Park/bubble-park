package fr.iutlens.mmi.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.iutlens.mmi.demo.utils.Music
import fr.iutlens.mmi.demo.utils.Music.mute
import fr.iutlens.mmi.demo.utils.savedSettings
import org.jetbrains.compose.resources.painterResource


@Composable
fun App(modifier: Modifier = Modifier) {
    MaterialTheme {

        var count by remember { savedSettings["count", 0] }
        var selection by remember { mutableStateOf(0) }
        Music("files/jungle.mp3")

        MultiGame(Modifier.fillMaxSize(), selection)

        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.padding(horizontal = 4.dp)) {
            Button(modifier = Modifier.padding(4.dp), onClick = { selection = 0 }) {
                Text(text = "A")
            }
            Button(modifier = Modifier.padding(4.dp), onClick = { selection = 1 }) {
                Text(text = "B")
            }
            Button(modifier = Modifier.padding(4.dp), onClick = { selection = 2 }) {
                Text(text = "C")
            }
            Button(modifier = Modifier.padding(4.dp), onClick = { selection = 3 }) {
                Text(text = "D")
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
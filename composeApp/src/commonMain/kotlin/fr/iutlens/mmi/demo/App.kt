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
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.unit.dp
import fr.iutlens.mmi.demo.utils.Music
import fr.iutlens.mmi.demo.utils.Music.mute
import fr.iutlens.mmi.demo.utils.SpriteSheet
import fr.iutlens.mmi.demo.utils.savedSettings
import org.jetbrains.compose.resources.painterResource


@Composable
fun App(modifier: Modifier = Modifier) {
    MaterialTheme {
        BubbleParkPreview()
    }
}
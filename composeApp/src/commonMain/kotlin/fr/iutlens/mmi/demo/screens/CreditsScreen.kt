package fr.iutlens.mmi.demo.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.credits
import fr.iutlens.mmi.demo.quit
import org.jetbrains.compose.resources.painterResource

@Composable
fun CreditsScreen(onBack: () -> Unit) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.credits),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Image(
            painter = painterResource(Res.drawable.quit),
            contentDescription = "Retour",
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-8).dp)
                .size(110.dp)
                .clickable { onBack() }
        )
    }
}
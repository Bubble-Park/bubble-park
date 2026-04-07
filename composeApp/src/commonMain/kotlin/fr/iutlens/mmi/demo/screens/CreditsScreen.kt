package fr.iutlens.mmi.demo.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.logo_medium
import fr.iutlens.mmi.demo.goat
import fr.iutlens.mmi.demo.lucas
import fr.iutlens.mmi.demo.menu_accueil
import fr.iutlens.mmi.demo.quit
import fr.iutlens.mmi.demo.thomas
import org.jetbrains.compose.resources.painterResource

@Composable
fun CreditsScreen(onBack: () -> Unit) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val minDim = minOf(maxWidth, maxHeight)

        Image(
            painter = painterResource(Res.drawable.menu_accueil),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Image(
            painter = painterResource(Res.drawable.logo_medium),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = maxWidth * 0.06f)
                .size(minDim * 0.55f),
            contentScale = ContentScale.Fit
        )

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = maxWidth * 0.04f)
                .size(minDim * 0.75f),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.thomas),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .offset(x = -(minDim * 0.14f))
                    .rotate(-10f),
                contentScale = ContentScale.Fit
            )
            Image(
                painter = painterResource(Res.drawable.lucas),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .offset(x = minDim * 0.14f)
                    .rotate(10f),
                contentScale = ContentScale.Fit
            )
            Image(
                painter = painterResource(Res.drawable.goat),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }

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
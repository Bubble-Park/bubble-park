package fr.iutlens.mmi.demo.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.zIndex
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.goat
import fr.iutlens.mmi.demo.jade
import fr.iutlens.mmi.demo.logo_medium
import fr.iutlens.mmi.demo.lucas
import fr.iutlens.mmi.demo.menu_accueil
import fr.iutlens.mmi.demo.quit
import fr.iutlens.mmi.demo.thomas
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun CreditsScreen(onBack: () -> Unit) {
    val quitSlide = remember { Animatable(-130f) }
    val scaleLogo = remember { Animatable(0f) }
    val scaleLucas = remember { Animatable(0f) }
    val scaleThomas = remember { Animatable(0f) }
    val scaleJade = remember { Animatable(0f) }
    val scaleGoat = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch { quitSlide.animateTo(-8f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)) }
        launch { scaleLogo.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)) }
        delay(120)
        launch { scaleLucas.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)) }
        delay(120)
        launch { scaleJade.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)) }
        launch { scaleThomas.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)) }
        delay(120)
        launch { scaleGoat.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)) }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val minDim = minOf(maxWidth, maxHeight)
        val screenW = maxWidth
        val imgSize = minDim * 0.45f
        val overlap = -(minDim * 0.18f)

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
                .padding(start = screenW * 0.16f)
                .size(minDim * 0.55f)
                .scale(scaleLogo.value),
            contentScale = ContentScale.Fit
        )

        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = screenW * 0.02f)
                .offset(x = -(screenW * 0.18f), y = minDim * 0.10f),
            verticalArrangement = Arrangement.spacedBy(overlap),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(Res.drawable.lucas),
                contentDescription = null,
                modifier = Modifier.size(imgSize * 0.7f).offset(x = -(screenW * 0.04f)).scale(scaleLucas.value),
                contentScale = ContentScale.Fit
            )
            Image(
                painter = painterResource(Res.drawable.thomas),
                contentDescription = null,
                modifier = Modifier.size(imgSize).offset(x = screenW * 0.06f).scale(scaleThomas.value),
                contentScale = ContentScale.Fit
            )
            Image(
                painter = painterResource(Res.drawable.goat),
                contentDescription = null,
                modifier = Modifier.size(imgSize).offset(x = -(screenW * 0.05f), y = -(minDim * 0.08f)).scale(scaleGoat.value),
                contentScale = ContentScale.Fit
            )
        }

        Image(
            painter = painterResource(Res.drawable.jade),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = screenW * 0.04f)
                .size(imgSize)
                .scale(scaleJade.value),
            contentScale = ContentScale.Fit
        )

        val scope = androidx.compose.runtime.rememberCoroutineScope()
        Image(
            painter = painterResource(Res.drawable.quit),
            contentDescription = "Retour",
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = quitSlide.value.dp)
                .size(110.dp)
                .clickable {
                    scope.launch {
                        quitSlide.animateTo(-130f, tween(250))
                        onBack()
                    }
                }
        )
    }
}
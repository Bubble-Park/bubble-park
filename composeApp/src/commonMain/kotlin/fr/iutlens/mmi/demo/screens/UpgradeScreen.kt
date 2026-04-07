package fr.iutlens.mmi.demo.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.background
import fr.iutlens.mmi.demo.dino_font
import fr.iutlens.mmi.demo.dudu_font
import fr.iutlens.mmi.demo.game.upgrade.Upgrade
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

@Composable
fun UpgradeScreen(
    choices: List<Upgrade>,
    onUpgradeSelected: (Upgrade) -> Unit
) {
    val dinoFont = FontFamily(Font(Res.font.dino_font))
    val duduFont = FontFamily(Font(Res.font.dudu_font))

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenW = maxWidth.value
        val screenH = maxHeight.value

        Image(
            painter = painterResource(Res.drawable.background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = (screenW * 0.04f).dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height((screenH * 0.04f).dp))

            Text(
                text = "Choisissez une amélioration",
                fontFamily = duduFont,
                color = Color(0xFF474534),
                fontSize = (screenH * 0.09f).sp
            )

            Spacer(modifier = Modifier.height((screenH * 0.02f).dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                choices.forEachIndexed { index, upgrade ->
                    UpgradeCard(
                        upgrade = upgrade,
                        index = index,
                        dinoFont = dinoFont,
                        duduFont = duduFont,
                        screenW = screenW,
                        screenH = screenH,
                        onClick = { onUpgradeSelected(upgrade) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
internal fun UpgradeCard(
    upgrade: Upgrade,
    index: Int,
    dinoFont: FontFamily,
    duduFont: FontFamily,
    screenW: Float,
    screenH: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation = listOf(-5f, 7f, -6f).getOrElse(index) { 0f }
    val cardScale = remember { Animatable(0f) }

    LaunchedEffect(index) {
        delay(index * 120L)
        cardScale.animateTo(
            1f,
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
    }

    Box(
        modifier = modifier
            .scale(cardScale.value)
            .rotate(rotation)
            .clickable(onClick = onClick)
            .padding(25.dp),
        contentAlignment = Alignment.Center
    ) {
        val image = upgrade.imageRes
        if (image != null) {
            Image(
                painter = painterResource(image),
                contentDescription = upgrade.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.75f)
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = upgrade.name,
                    fontFamily = dinoFont,
                    color = Color(0xFF474534),
                    fontSize = (screenH * 0.10f).sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height((screenH * 0.025f).dp))
                Text(
                    text = upgrade.description,
                    fontFamily = duduFont,
                    color = Color(0xFF474534).copy(alpha = 0.75f),
                    fontSize = (screenH * 0.055f).sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

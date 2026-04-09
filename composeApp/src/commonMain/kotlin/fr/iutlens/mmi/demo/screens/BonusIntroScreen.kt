package fr.iutlens.mmi.demo.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.dudu_font
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.Font

@Composable
fun BonusIntroScreen(onDone: () -> Unit) {
    val duduFont = FontFamily(Font(Res.font.dudu_font))
    var countdownValue by remember { mutableStateOf(3) }

    LaunchedEffect(Unit) {
        countdownValue = 3
        delay(1000L)
        countdownValue = 2
        delay(1000L)
        countdownValue = 1
        delay(1000L)
        onDone()
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        val minDim = minOf(maxWidth, maxHeight)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "AMELIORATIONS",
                fontFamily = duduFont,
                color = Color(0xFFFF69B4),
                fontSize = (minDim.value * 0.28f).sp,
                textAlign = TextAlign.Center
            )

            BonusCountdownTick(value = countdownValue, minDim = minDim)
        }
    }
}

@Composable
private fun BonusCountdownTick(value: Int, minDim: Dp) {
    val duduFont = FontFamily(Font(Res.font.dudu_font))
    val scale = remember(value) { Animatable(0f) }

    LaunchedEffect(value) {
        scale.animateTo(
            1f,
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
    }

    Text(
        text = "$value",
        fontFamily = duduFont,
        color = Color(0xFFFF69B4),
        fontSize = (minDim.value * 0.18f).sp,
        modifier = Modifier.scale(scale.value),
        textAlign = TextAlign.Center
    )
}
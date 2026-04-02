package fr.iutlens.mmi.demo.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.dudu_font
import fr.iutlens.mmi.demo.player_heart
import fr.iutlens.mmi.demo.player_heart_empty
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

@Composable
fun ShowScore(score: Int, fontSize: TextUnit = 44.sp) {
    val duduFont = FontFamily(Font(Res.font.dudu_font))
    val scale = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
    }
    Text(
        text = "Score : $score",
        modifier = Modifier.padding(start = 16.dp).scale(scale.value),
        color = Color(0xFF474534),
        fontSize = fontSize,
        fontFamily = duduFont
    )
}

@Composable
fun ShowChrono(time: Float, fontSize: TextUnit = 44.sp) {
    val duduFont = FontFamily(Font(Res.font.dudu_font))
    val scale = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
    }
    Text(
        text = "Temps : ${time.toInt()}s",
        modifier = Modifier.padding(start = 16.dp).scale(scale.value),
        color = Color(0xFF474534),
        fontSize = fontSize,
        fontFamily = duduFont
    )
}

private val heartRotations = listOf(4f, -8f, 6f, -5f, 7f, -4f)

@Composable
fun ShowLife(life: Int, maxLife: Int = 3, heartSize: Dp = 72.dp) {
    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(maxLife) { index ->
            val isFullHeart = index < life
            val rotate = heartRotations.getOrElse(index) { if (index % 2 == 0) 4f else -6f }
            val iconRes = if (isFullHeart) Res.drawable.player_heart else Res.drawable.player_heart_empty
            val iconDesc = if (isFullHeart) "Coeur plein" else "Coeur vide"
            val heartScale = remember(index) { Animatable(0f) }
            LaunchedEffect(index) {
                delay(index * 80L)
                heartScale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
            }
            Image(
                painter = painterResource(iconRes),
                contentDescription = iconDesc,
                modifier = Modifier
                    .size(heartSize)
                    .rotate(rotate)
                    .scale(heartScale.value)
            )
        }
    }
}
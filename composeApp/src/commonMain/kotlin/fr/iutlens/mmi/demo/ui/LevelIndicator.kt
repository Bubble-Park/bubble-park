package fr.iutlens.mmi.demo.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.current_level
import fr.iutlens.mmi.demo.dudu_font
import fr.iutlens.mmi.demo.previous_level
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

@Composable
fun LevelIndicator(levelIndex: Int, minDim: Dp, modifier: Modifier = Modifier) {
    val duduFont = FontFamily(Font(Res.font.dudu_font))
    val pop = remember { Animatable(0f) }

    LaunchedEffect(levelIndex) {
        pop.snapTo(0f)
        pop.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
    }

    val currentLevel = levelIndex + 1
    val bigSize = minDim * 0.13f
    val smallSize = minDim * 0.09f
    val colorCurrent = Color(0xFFFF7EEA)
    val colorSide = Color(0xFFF1934D)

    Row(
        modifier = modifier.scale(pop.value),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(minDim * 0.015f)
    ) {
        if (levelIndex > 0) {
            LevelBadge(
                number = currentLevel - 1,
                size = smallSize,
                textColor = colorSide,
                isCurrent = false,
                duduFont = duduFont
            )
        }
        LevelBadge(
            number = currentLevel,
            size = bigSize,
            textColor = colorCurrent,
            isCurrent = true,
            duduFont = duduFont
        )
        LevelBadge(
            number = currentLevel + 1,
            size = smallSize,
            textColor = colorSide,
            isCurrent = false,
            duduFont = duduFont
        )
    }
}

@Composable
private fun LevelBadge(number: Int, size: Dp, textColor: Color, isCurrent: Boolean, duduFont: FontFamily) {
    val imageRes = if (isCurrent) Res.drawable.current_level else Res.drawable.previous_level
    Box(contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = null,
            modifier = Modifier.size(size),
            contentScale = ContentScale.Fit
        )
        Text(
            text = "$number",
            fontFamily = duduFont,
            color = textColor,
            fontSize = (size.value * 0.45f).sp
        )
    }
}
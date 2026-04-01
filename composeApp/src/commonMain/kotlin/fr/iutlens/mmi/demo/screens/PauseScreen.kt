package fr.iutlens.mmi.demo.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.background
import fr.iutlens.mmi.demo.damage_border
import fr.iutlens.mmi.demo.dudu_font
import fr.iutlens.mmi.demo.head_bubblechtein
import fr.iutlens.mmi.demo.ui.ShowLife
import org.jetbrains.compose.resources.Font
import androidx.compose.ui.text.font.FontFamily
import org.jetbrains.compose.resources.painterResource

@Composable
fun PauseScreen(life: Int, score: Int, damageScale: Float, onResume: () -> Unit, onQuit: () -> Unit) {
    val duduFont = FontFamily(Font(Res.font.dudu_font))

    val infiniteTransition = rememberInfiniteTransition(label = "pause_rot")
    val rotReprendre by infiniteTransition.animateFloat(
        initialValue = -1.5f, targetValue = 2f,
        animationSpec = infiniteRepeatable(tween(2800, easing = LinearEasing), RepeatMode.Reverse),
        label = "rotReprendre"
    )
    val rotQuitter by infiniteTransition.animateFloat(
        initialValue = -6f, targetValue = -3f,
        animationSpec = infiniteRepeatable(tween(3600, easing = LinearEasing), RepeatMode.Reverse),
        label = "rotQuitter"
    )
    val rotVolume by infiniteTransition.animateFloat(
        initialValue = 2f, targetValue = -1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Reverse),
        label = "rotVolume"
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenW = maxWidth.value
        val screenH = maxHeight.value

        val contentWidthFraction = 0.64f
        val scoreFontSize = (screenH * 0.13f).sp

        Image(
            painter = painterResource(Res.drawable.background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Image(
            painter = painterResource(Res.drawable.damage_border),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize().alpha(0.8f).scale(damageScale)
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxHeight()
                .fillMaxWidth(contentWidthFraction)
                .padding(top = (screenH * 0.09f).dp, start = (screenW * 0.06f).dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().weight(0.5f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(Res.drawable.head_bubblechtein),
                    contentDescription = null,
                    modifier = Modifier.fillMaxHeight(0.65f).aspectRatio(1f)
                )
                Box(modifier = Modifier.padding(end = (screenW * 0.02f).dp)) {
                    ShowLife(life, heartSize = (screenH * 0.12f).dp)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.25f)
                    .padding(horizontal = (screenW * 0.04f).dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Score", fontFamily = duduFont, color = Color(0xFF474534), fontSize = scoreFontSize)
                Text(text = "$score", fontFamily = duduFont, color = Color(0xFF474534), fontSize = scoreFontSize)
            }

            Spacer(modifier = Modifier.weight(0.25f))
        }

        VolumeButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = (screenH * 0.04f).dp, end = (screenW * 0.02f).dp)
                .size((screenH * 0.15f).dp)
                .rotate(rotVolume)
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = (screenW * 0.01f).dp, bottom = (screenH * 0.05f).dp)
                .fillMaxHeight(0.65f)
                .width((screenW * 0.42f).dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            MenuButton(
                onClick = onResume,
                text = "REPRENDRE",
                fontFamily = duduFont,
                fillColor = Color(0xFFF1934D),
                outlineColor = Color.Transparent,
                fontSize = (screenH * 0.11f).sp,
                strokeWidth = 0f,
                modifier = Modifier.rotate(rotReprendre)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(x = (screenW * 0.04f).dp, y = -(screenH * 0.06f).dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                MenuButton(
                    onClick = onQuit,
                    text = "QUITTER",
                    fontFamily = duduFont,
                    fillColor = Color.Black,
                    outlineColor = Color.Transparent,
                    fontSize = (screenH * 0.10f).sp,
                    strokeWidth = 0f,
                    modifier = Modifier.rotate(rotQuitter)
                )
            }
        }
    }
}
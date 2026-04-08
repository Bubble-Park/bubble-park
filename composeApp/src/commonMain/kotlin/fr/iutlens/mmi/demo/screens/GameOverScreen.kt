package fr.iutlens.mmi.demo.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.background
import fr.iutlens.mmi.demo.damage_border
import fr.iutlens.mmi.demo.dudu_font
import fr.iutlens.mmi.demo.game.sprite.squareWaveRotation
import fr.iutlens.mmi.demo.trou
import fr.iutlens.mmi.demo.ui.LevelIndicator
import fr.iutlens.mmi.demo.ui.ShowLife
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

@Composable
fun GameOverScreen(score: Int, levelIndex: Int = 0, onReplay: () -> Unit, onQuit: () -> Unit) {
    val duduFont = FontFamily(Font(Res.font.dudu_font))
    val displayedScore = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        displayedScore.animateTo(score.toFloat(), tween(durationMillis = 1200))
    }

    var elapsed by remember { mutableStateOf(0L) }
    LaunchedEffect(Unit) {
        val start = withFrameMillis { it }
        while (true) { elapsed = withFrameMillis { it } - start }
    }

    val rotJouer = squareWaveRotation(elapsed * 0.002f, 2f)
    val rotQuitter = squareWaveRotation(elapsed * 0.0018f + 2f, 1.5f)
    val rotVolume = squareWaveRotation(elapsed * 0.0025f + 4f, 1.5f)

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
            modifier = Modifier.fillMaxSize().alpha(1f).scale(1f)
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxHeight()
                .fillMaxWidth(contentWidthFraction)
                .padding(top = (screenH * 0.09f).dp, start = (screenW * 0.1f).dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().weight(0.5f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(Res.drawable.trou),
                    contentDescription = null,
                    modifier = Modifier.fillMaxHeight(0.85f).aspectRatio(1f)
                )
                Box(modifier = Modifier.padding(end = (screenW * 0.02f).dp)) {
                    ShowLife(0, heartSize = (screenH * 0.12f).dp)
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
                Text(text = "${displayedScore.value.toInt()}", fontFamily = duduFont, color = Color(0xFF474534), fontSize = scoreFontSize)
            }

            Spacer(modifier = Modifier.weight(0.25f))
        }

        LevelIndicator(
            levelIndex = levelIndex,
            minDim = minOf(maxWidth, maxHeight),
            modifier = Modifier.align(Alignment.TopCenter).padding(top = (screenH * 0.04f).dp)
        )

        VolumeButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = (screenH * 0.2f).dp, end = (screenW * 0.1f).dp)
                .size((screenH * 0.15f).dp)
                .rotate(rotVolume)
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = (screenW * 0.1f).dp, bottom = (screenH * 0.2f).dp)
                .width((screenW * 0.42f).dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom
        ) {
            MenuButton(
                onClick = onReplay,
                text = "JOUER",
                fontFamily = duduFont,
                fillColor = Color(0xFFF1934D),
                outlineColor = Color.Transparent,
                fontSize = (screenH * 0.11f).sp,
                strokeWidth = 0f,
                modifier = Modifier.rotate(rotJouer)
            )
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
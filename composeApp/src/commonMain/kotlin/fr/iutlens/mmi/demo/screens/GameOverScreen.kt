package fr.iutlens.mmi.demo.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.iutlens.mmi.demo.PlayerDeathState
import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.background
import fr.iutlens.mmi.demo.bubblechtein_sprites
import fr.iutlens.mmi.demo.damage_border
import fr.iutlens.mmi.demo.dudu_font
import fr.iutlens.mmi.demo.game.sprite.squareWaveRotation
import fr.iutlens.mmi.demo.ui.LevelIndicator
import fr.iutlens.mmi.demo.ui.ShowLife
import fr.iutlens.mmi.demo.utils.SpriteSheet
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

@Composable
fun GameOverScreen(
    score: Int,
    levelIndex: Int = 0,
    deathState: PlayerDeathState = PlayerDeathState(0f, 1f, 0f, true),
    onReplay: () -> Unit,
    onQuit: () -> Unit
) {
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

    var fallY by remember { mutableStateOf(-200f) }
    var fallVy by remember { mutableStateOf(5f) }
    var fallRotation by remember { mutableStateOf(deathState.rotation) }
    val fallDir = if (deathState.facingRight) 1f else -1f
    val gravity = 5.5f
    val vyMax = 280f

    var canvasHeight by remember { mutableStateOf(1000f) }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameMillis { }
            fallVy = (fallVy + gravity).coerceAtMost(vyMax)
            fallY += fallVy / 2f
            fallRotation -= 3f * fallDir
            if (fallY > canvasHeight + 200f) {
                fallY = -200f
                fallVy = 5f
            }
        }
    }

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

        Canvas(modifier = Modifier.fillMaxSize()) {
            canvasHeight = size.height
            val scale = if (deathState.gameWorldWidth > 0f) size.width / deathState.gameWorldWidth else 1f
            val screenX = deathState.x * scale

            if (SpriteSheet.isLoaded(Res.drawable.bubblechtein_sprites)) {
                val sheet = SpriteSheet[Res.drawable.bubblechtein_sprites]
                val sw = (sheet.spriteWidth * scale).toInt()
                val sh = (sheet.spriteHeight * scale).toInt()
                withTransform({
                    translate(screenX, fallY)
                    rotate(fallRotation, pivot = Offset.Zero)
                    if (!deathState.facingRight) scale(-1f, 1f, pivot = Offset.Zero)
                }) {
                    sheet.paint(this, 3, -sw / 2, -sh / 2, size = IntSize(sw, sh))
                }
            }
        }

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
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
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
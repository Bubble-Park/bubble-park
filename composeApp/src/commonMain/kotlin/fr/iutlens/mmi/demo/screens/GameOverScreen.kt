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

private enum class FallPhase { FALLING, LANDED, STANDING_UP, STANDING, WALKING, DONE }

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

    var fallX by remember { mutableStateOf(0f) }
    var fallXInitialized by remember { mutableStateOf(false) }
    var fallY by remember { mutableStateOf(-200f) }
    var fallVy by remember { mutableStateOf(0f) }
    var fallRotation by remember { mutableStateOf(deathState.rotation) }
    var spriteNdx by remember { mutableStateOf(3) }
    var walkFacingRight by remember { mutableStateOf(false) }

    var canvasHeight by remember { mutableStateOf(1000f) }
    var canvasWidth by remember { mutableStateOf(1000f) }
    var currentScale by remember { mutableStateOf(1f) }
    var scaledSpriteH by remember { mutableStateOf(100f) }
    var scaledSpriteW by remember { mutableStateOf(100f) }

    var phase by remember { mutableStateOf(FallPhase.FALLING) }
    var phaseMs by remember { mutableStateOf(0L) }
    var landedY by remember { mutableStateOf(0f) }

    val gravity = 800f
    val vyMax = 1200f
    val standRotation = if (deathState.facingRight) -90f else 90f
    val walkSpeed = 320f

    LaunchedEffect(Unit) {
        var lastMs = withFrameMillis { it }
        while (true) {
            val nowMs = withFrameMillis { it }
            val dt = (nowMs - lastMs).coerceAtMost(100L)
            lastMs = nowMs

            if (!fallXInitialized) continue

            val dtSec = dt / 1000f
            when (phase) {
                FallPhase.FALLING -> {
                    fallVy = (fallVy + gravity * dtSec).coerceAtMost(vyMax)
                    fallY += fallVy * dtSec
                    fallRotation -= 180f * dtSec * if (deathState.facingRight) 1f else -1f
                    spriteNdx = 3
                    val floorY = canvasHeight - scaledSpriteH / 4f
                    if (fallY >= floorY) {
                        fallY = floorY
                        landedY = floorY
                        fallVy = 0f
                        fallRotation = standRotation
                        spriteNdx = 3
                        phase = FallPhase.LANDED
                        phaseMs = 0L
                    }
                }
                FallPhase.LANDED -> {
                    phaseMs += dt
                    if (phaseMs >= 1000L) {
                        phase = FallPhase.STANDING_UP
                        phaseMs = 0L
                    }
                }
                FallPhase.STANDING_UP -> {
                    phaseMs += dt
                    val progress = (phaseMs / 250f).coerceAtMost(1f)
                    fallRotation = standRotation * (1f - progress)
                    val walkY = canvasHeight - scaledSpriteH / 2f
                    fallY = landedY + (walkY - landedY) * progress
                    spriteNdx = 0
                    if (phaseMs >= 250L) {
                        fallRotation = 0f
                        fallY = walkY
                        phase = FallPhase.STANDING
                        phaseMs = 0L
                    }
                }
                FallPhase.STANDING -> {
                    phaseMs += dt
                    spriteNdx = 0
                    if (phaseMs >= 1000L) {
                        walkFacingRight = false
                        phase = FallPhase.WALKING
                        phaseMs = 0L
                    }
                }
                FallPhase.WALKING -> {
                    phaseMs += dt
                    fallX -= walkSpeed * dt / 1000f
                    spriteNdx = if ((phaseMs / 200L) % 2L == 0L) 0 else 1
                    if (fallX < -scaledSpriteW) {
                        phase = FallPhase.DONE
                    }
                }
                FallPhase.DONE -> {}
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

        Image(
            painter = painterResource(Res.drawable.damage_border),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize().alpha(1f).scale(1f)
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            canvasHeight = size.height
            canvasWidth = size.width
            val scale = if (deathState.gameWorldWidth > 0f) size.width / deathState.gameWorldWidth else 1f
            currentScale = scale
            if (!fallXInitialized) { fallX = deathState.x * size.width; fallXInitialized = true }

            if (SpriteSheet.isLoaded(Res.drawable.bubblechtein_sprites)) {
                val sheet = SpriteSheet[Res.drawable.bubblechtein_sprites]
                val sw = (sheet.spriteWidth * scale).toInt()
                val sh = (sheet.spriteHeight * scale).toInt()
                scaledSpriteH = sh.toFloat()
                scaledSpriteW = sw.toFloat()
                val facingRight = if (phase == FallPhase.WALKING) walkFacingRight else deathState.facingRight
                withTransform({
                    translate(fallX, fallY)
                    rotate(fallRotation, pivot = Offset.Zero)
                    if (!facingRight) scale(-1f, 1f, pivot = Offset.Zero)
                }) {
                    sheet.paint(this, spriteNdx, -sw / 2, -sh / 2, size = IntSize(sw, sh))
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxHeight()
                .fillMaxWidth(contentWidthFraction)
                .padding(top = (screenH * 0.09f).dp, start = (screenW * 0.1f).dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.75f)
                    .padding(horizontal = (screenW * 0.02f).dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShowLife(0, heartSize = (screenH * 0.12f).dp)
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
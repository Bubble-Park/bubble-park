package fr.iutlens.mmi.demo.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.iutlens.mmi.demo.game.DifficultyConfig
import fr.iutlens.mmi.demo.game.sprite.squareWaveRotation
import fr.iutlens.mmi.demo.game.upgrade.Upgrade
import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.dudu_font
import fr.iutlens.mmi.demo.head_bubblechtein
import fr.iutlens.mmi.demo.menu_accueil
import fr.iutlens.mmi.demo.panneau
import fr.iutlens.mmi.demo.ui.ShowLife
import fr.iutlens.mmi.demo.utils.SpriteSheet
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

@Composable
fun LevelPanel(
    levelNumber: Int,
    life: Int,
    maxLife: Int,
    dinoSprites: List<DrawableResource>,
    acquiredUpgrades: List<Upgrade> = emptyList(),
    onDone: () -> Unit
) {
    val duduFont = FontFamily(Font(Res.font.dudu_font))
    val scale = remember { Animatable(0f) }
    val rotation = remember { Animatable(-14f) }
    var elapsed by remember { mutableStateOf(0L) }

    LaunchedEffect(Unit) {
        val start = withFrameMillis { it }
        while (true) { elapsed = withFrameMillis { it } - start }
    }

    LaunchedEffect(Unit) {
        launch {
            scale.animateTo(1.15f, tween(220))
            scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
        }
        launch {
            rotation.animateTo(10f, tween(130))
            rotation.animateTo(-8f, tween(130))
            rotation.animateTo(6f, tween(120))
            rotation.animateTo(-4f, tween(110))
            rotation.animateTo(2f, tween(100))
            rotation.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
        }
        delay(2500L)
        onDone()
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val minDim = minOf(maxWidth, maxHeight)
        val screenW = maxWidth.value
        val screenH = maxHeight.value
        val panneauWidth = minDim * 1.1f
        val headSize = minDim * 0.22f
        val dinoHeadSize = minDim * 0.28f
        val heartSize = minDim * 0.15f
        val totalSecs = DifficultyConfig.TOTAL_LEVEL_TIME.toInt()
        val timeText = "${totalSecs / 60}:${(totalSecs % 60).toString().padStart(2, '0')}"

        Box(modifier = Modifier.fillMaxSize().background(Color.White))

        Image(
            painter = painterResource(Res.drawable.menu_accueil),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = minDim * 0.28f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(Res.drawable.head_bubblechtein),
                contentDescription = null,
                modifier = Modifier.size(headSize)
            )
            ShowLife(life = life, maxLife = maxLife, heartSize = heartSize)
            Text(
                text = timeText,
                fontFamily = duduFont,
                color = Color(0xFF474534),
                fontSize = (minDim.value * 0.07f).sp,
                modifier = Modifier.padding(vertical = minDim * 0.02f)
            )
            Row(
                modifier = Modifier.padding(top = minDim * 0.03f),
                horizontalArrangement = Arrangement.spacedBy(-dinoHeadSize * 0.70f)
            ) {
                val rotations = listOf(-7f, 5f, -10f, 8f, -4f, 11f, -6f, 9f, -12f)
                dinoSprites.forEachIndexed { i, sprite ->
                    DinoHead(spriteRes = sprite, size = dinoHeadSize, rotation = rotations.getOrElse(i) { 0f }, popDelay = i * 80L)
                }
            }
        }

        if (acquiredUpgrades.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = (screenW * 0.02f).dp)
                    .offset(y = (screenH * 0.12f).dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Bottom
            ) {
                acquiredUpgrades.forEachIndexed { i, upgrade ->
                    val rotCount = squareWaveRotation(elapsed * 0.006f + i * 1.5f, 8f)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "x${upgrade.acquiredCount}",
                            fontFamily = duduFont,
                            color = Color(0xFF474534),
                            fontSize = (screenH * 0.11f).sp,
                            modifier = Modifier.offset(y = (screenH * 0.04f).dp)
                        )
                        UpgradeCard(
                            upgrade = upgrade,
                            index = 0,
                            dinoFont = duduFont,
                            duduFont = duduFont,
                            screenW = screenW,
                            screenH = screenH,
                            onClick = {},
                            modifier = Modifier.size((screenH * 0.40f).dp)
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(y = (screenH * 0.08f).dp)
                .scale(scale.value)
                .rotate(rotation.value)
        ) {
            Image(
                painter = painterResource(Res.drawable.panneau),
                contentDescription = null,
                modifier = Modifier
                    .width(panneauWidth)
                    .aspectRatio(1.4f),
                contentScale = ContentScale.Fit
            )
            Text(
                text = "Niveau $levelNumber",
                fontFamily = duduFont,
                color = Color.White,
                fontSize = (minDim.value * 0.10f).sp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = panneauWidth * 0.22f)
            )
        }

        if (acquiredUpgrades.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = (screenW * 0.02f).dp)
                    .offset(y = (screenH * 0.12f).dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Bottom
            ) {
                acquiredUpgrades.forEachIndexed { i, upgrade ->
                    val rotCount = squareWaveRotation(elapsed * 0.006f + i * 1.5f, 8f)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "x${upgrade.acquiredCount}",
                            fontFamily = duduFont,
                            color = Color(0xFFFF7EEA),
                            fontSize = (screenH * 0.11f).sp,
                            modifier = Modifier.offset(y = (screenH * 0.04f).dp)
                        )
                        UpgradeCard(
                            upgrade = upgrade,
                            index = 0,
                            dinoFont = duduFont,
                            duduFont = duduFont,
                            screenW = screenW,
                            screenH = screenH,
                            onClick = {},
                            modifier = Modifier.size((screenH * 0.40f).dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun DinoHead(spriteRes: DrawableResource, size: Dp, rotation: Float = 0f, popDelay: Long = 0L, modifier: Modifier = Modifier) {
    val popScale = remember(spriteRes) { Animatable(0f) }
    LaunchedEffect(spriteRes) {
        delay(popDelay)
        popScale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
    }
    Canvas(modifier = modifier.size(size).scale(popScale.value).rotate(rotation)) {
        val sheet = SpriteSheet[spriteRes]
        val px = this.size.width.toInt()
        val sc = minOf(px.toFloat() / sheet.spriteWidth, px.toFloat() / sheet.spriteHeight)
        val drawW = (sheet.spriteWidth * sc).toInt()
        val drawH = (sheet.spriteHeight * sc).toInt()
        val drawX = (px - drawW) / 2
        val drawY = (px - drawH) / 2
        sheet.paint(this, 2, drawX, drawY, IntSize(drawW, drawH))
    }
}
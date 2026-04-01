package fr.iutlens.mmi.demo.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntSize
import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.background
import fr.iutlens.mmi.demo.compy_sprite
import fr.iutlens.mmi.demo.dodo_sprite
import fr.iutlens.mmi.demo.gallimimus_sprite
import fr.iutlens.mmi.demo.parasaur_sprite
import fr.iutlens.mmi.demo.quit
import fr.iutlens.mmi.demo.pause
import fr.iutlens.mmi.demo.raptor_sprite
import fr.iutlens.mmi.demo.stego_sprite
import fr.iutlens.mmi.demo.trice_sprite
import fr.iutlens.mmi.demo.trex_sprite
import fr.iutlens.mmi.demo.game.sprite.squareWaveRotation
import fr.iutlens.mmi.demo.utils.OnceSpriteLoaded
import fr.iutlens.mmi.demo.utils.SpriteSheet
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private val dinoSprites = listOf(
    Res.drawable.trex_sprite,
    Res.drawable.raptor_sprite,
    Res.drawable.parasaur_sprite,
    Res.drawable.gallimimus_sprite,
    Res.drawable.trice_sprite,
    Res.drawable.stego_sprite,
    Res.drawable.compy_sprite,
    Res.drawable.dodo_sprite,
)

@Composable
fun BestiaryScreen(onBack: () -> Unit) {
    var elapsed by remember { mutableStateOf(0L) }
    LaunchedEffect(Unit) {
        val start = withFrameMillis { it }
        while (true) { elapsed = withFrameMillis { it } - start }
    }

    SpriteSheet.load(Res.drawable.trex_sprite, 2, 2, filterQuality = FilterQuality.High)
    SpriteSheet.load(Res.drawable.raptor_sprite, 2, 2, filterQuality = FilterQuality.High)
    SpriteSheet.load(Res.drawable.parasaur_sprite, 2, 2, filterQuality = FilterQuality.High)
    SpriteSheet.load(Res.drawable.gallimimus_sprite, 2, 2, filterQuality = FilterQuality.High)
    SpriteSheet.load(Res.drawable.trice_sprite, 2, 2, filterQuality = FilterQuality.High)
    SpriteSheet.load(Res.drawable.stego_sprite, 2, 2, filterQuality = FilterQuality.High)
    SpriteSheet.load(Res.drawable.compy_sprite, 2, 2, filterQuality = FilterQuality.High)
    SpriteSheet.load(Res.drawable.dodo_sprite, 2, 2, filterQuality = FilterQuality.High)

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val minDim = minOf(maxWidth, maxHeight)
        val portraitSize = minDim * 0.45f
        val gridSpacing = minDim * 0.04f

        Image(
            painter = painterResource(Res.drawable.background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        OnceSpriteLoaded(*dinoSprites.toTypedArray()) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                verticalArrangement = Arrangement.spacedBy(gridSpacing),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (row in 0 until 2) {
                    Row(horizontalArrangement = Arrangement.spacedBy(gridSpacing)) {
                        for (col in 0 until 4) {
                            val idx = row * 4 + col
                            if (idx < dinoSprites.size) {
                                DinoHead(res = dinoSprites[idx], sizeDp = portraitSize, elapsed = elapsed, phaseOffset = idx * 1.3f)
                            }
                        }
                    }
                }
            }
        }

        Image(
            painter = painterResource(Res.drawable.quit),
            contentDescription = "Retour",
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-8).dp)
                .size(110.dp)
                .clickable { onBack() }
        )
    }
}

@Composable
private fun DinoHead(res: DrawableResource, sizeDp: Dp, elapsed: Long, phaseOffset: Float) {
    val rotation = squareWaveRotation(elapsed * 0.004f + phaseOffset, 7f)
    Canvas(modifier = Modifier.size(sizeDp).rotate(rotation)) {
        val sheet = SpriteSheet[res]
        val px = size.width.toInt()
        val scale = minOf(px.toFloat() / sheet.spriteWidth, px.toFloat() / sheet.spriteHeight)
        val drawW = (sheet.spriteWidth * scale).toInt()
        val drawH = (sheet.spriteHeight * scale).toInt()
        val drawX = (px - drawW) / 2
        val drawY = (px - drawH) / 2
        sheet.paint(this, 2, drawX, drawY, IntSize(drawW, drawH))
    }
}

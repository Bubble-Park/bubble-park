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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.compy_sprite
import fr.iutlens.mmi.demo.dino_font
import fr.iutlens.mmi.demo.dodo_sprite
import fr.iutlens.mmi.demo.gigano_sprite
import fr.iutlens.mmi.demo.galliminus_sprite
import fr.iutlens.mmi.demo.parasaur_sprite
import fr.iutlens.mmi.demo.arrow
import fr.iutlens.mmi.demo.quit
import fr.iutlens.mmi.demo.pause
import fr.iutlens.mmi.demo.raptor_sprite
import fr.iutlens.mmi.demo.stego_sprite
import fr.iutlens.mmi.demo.trice_sprite
import fr.iutlens.mmi.demo.trex_sprite
import fr.iutlens.mmi.demo.menu_accueil
import fr.iutlens.mmi.demo.game.sprite.squareWaveRotation
import fr.iutlens.mmi.demo.utils.OnceSpriteLoaded
import fr.iutlens.mmi.demo.utils.SpriteSheet
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

private val dinoPages = listOf(
    listOf(
        Res.drawable.trex_sprite,
        Res.drawable.raptor_sprite,
        Res.drawable.parasaur_sprite,
        Res.drawable.galliminus_sprite,
        Res.drawable.trice_sprite,
        Res.drawable.stego_sprite,
        Res.drawable.compy_sprite,
        Res.drawable.dodo_sprite,
    ),
    listOf(
        Res.drawable.gigano_sprite,
    )
)

private val dinoSprites = dinoPages.flatten()

@Composable
fun BestiaryScreen(onBack: () -> Unit) {
    var elapsed by remember { mutableStateOf(0L) }
    var currentPage by remember { mutableStateOf(0) }
    val dinoFont = FontFamily(Font(Res.font.dino_font))

    LaunchedEffect(Unit) {
        val start = withFrameMillis { it }
        while (true) { elapsed = withFrameMillis { it } - start }
    }

    SpriteSheet.load(Res.drawable.trex_sprite, 2, 2, filterQuality = FilterQuality.High)
    SpriteSheet.load(Res.drawable.raptor_sprite, 2, 2, filterQuality = FilterQuality.High)
    SpriteSheet.load(Res.drawable.parasaur_sprite, 2, 2, filterQuality = FilterQuality.High)
    SpriteSheet.load(Res.drawable.galliminus_sprite, 2, 2, filterQuality = FilterQuality.High)
    SpriteSheet.load(Res.drawable.trice_sprite, 2, 2, filterQuality = FilterQuality.High)
    SpriteSheet.load(Res.drawable.stego_sprite, 2, 2, filterQuality = FilterQuality.High)
    SpriteSheet.load(Res.drawable.compy_sprite, 2, 2, filterQuality = FilterQuality.High)
    SpriteSheet.load(Res.drawable.dodo_sprite, 2, 2, filterQuality = FilterQuality.High)
    SpriteSheet.load(Res.drawable.gigano_sprite, 2, 2, filterQuality = FilterQuality.High)

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val minDim = minOf(maxWidth, maxHeight)
        val portraitSize = minDim * 0.45f
        val gridSpacing = minDim * 0.04f
        val arrowSize = (minDim * 0.12f).value.sp
        val page = dinoPages[currentPage]

        Image(
            painter = painterResource(Res.drawable.menu_accueil),
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
                val rows = (page.size + 3) / 4
                for (row in 0 until rows) {
                    Row(horizontalArrangement = Arrangement.spacedBy(gridSpacing)) {
                        for (col in 0 until 4) {
                            val idx = row * 4 + col
                            if (idx < page.size) {
                                val globalIdx = dinoSprites.indexOf(page[idx])
                                DinoHead(res = page[idx], sizeDp = portraitSize, elapsed = elapsed, phaseOffset = globalIdx * 1.3f)
                            }
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-16).dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(Res.drawable.arrow),
                contentDescription = "Page précédente",
                modifier = Modifier
                    .size(minDim * 0.12f)
                    .rotate(180f)
                    .alpha(if (currentPage > 0) 1f else 0f)
                    .clickable(enabled = currentPage > 0) { currentPage-- }
            )
            Text(
                text = "${currentPage + 1} / ${dinoPages.size}",
                fontFamily = dinoFont,
                fontSize = arrowSize * 0.6f,
                color = Color.White
            )
            Image(
                painter = painterResource(Res.drawable.arrow),
                contentDescription = "Page suivante",
                modifier = Modifier
                    .size(minDim * 0.12f)
                    .alpha(if (currentPage < dinoPages.size - 1) 1f else 0f)
                    .clickable(enabled = currentPage < dinoPages.size - 1) { currentPage++ }
            )
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

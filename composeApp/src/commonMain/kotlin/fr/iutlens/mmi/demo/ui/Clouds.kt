package fr.iutlens.mmi.demo.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.nuage1
import fr.iutlens.mmi.demo.nuage2
import fr.iutlens.mmi.demo.game.sprite.squareWaveRotation
import org.jetbrains.compose.resources.painterResource

data class CloudLayer(
    val resIndex: Int,
    val speed: Float,
    val phaseRatio: Float,
    val yRatio: Float,
    val sizeRatio: Float,
    val rotationAmp: Float,
    val alpha: Float = 1f,
    val foreground: Boolean = false
)

val cloudLayers = listOf(
    CloudLayer(1, 0.06f, 0.00f, 0.01f, 0.45f, 3f, 1f, false), // très haut
    CloudLayer(0, 0.07f, 0.25f, 0.72f, 0.55f, 4f, 1f, false), // bas
    CloudLayer(1, 0.05f, 0.50f, 0.11f, 0.40f, 2f, 1f, false), // haut
    CloudLayer(0, 0.08f, 0.75f, 0.83f, 0.50f, 3f, 1f, false), // très bas
)

@Composable
fun BoxScope.CloudsOverlay(elapsed: Long, screenW: Float, screenH: Float, minDim: Dp, foreground: Boolean = false) {
    // Chargés une seule fois au niveau du composable, pas à chaque itération de forEach
    val painterNuage1 = painterResource(Res.drawable.nuage1)
    val painterNuage2 = painterResource(Res.drawable.nuage2)
    cloudLayers.filter { it.foreground == foreground }.forEach { cloud ->
        val cloudSizeDp = minDim * cloud.sizeRatio
        val cloudW = cloudSizeDp.value
        val total = screenW + cloudW * 2f
        val period = total / cloud.speed
        val delay = cloud.phaseRatio * period
        val adjustedElapsed = (elapsed.toFloat() - delay).coerceAtLeast(0f)
        val step = 3f
        val rawX = (adjustedElapsed * cloud.speed % total) - cloudW
        val cloudX = (rawX / step).toLong() * step
        val cloudY = screenH * cloud.yRatio
        val rotation = squareWaveRotation(elapsed * 0.001f + cloud.phaseRatio * 10f, cloud.rotationAmp)
        val painter = if (cloud.resIndex == 0) painterNuage1 else painterNuage2
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = cloudX.dp, y = cloudY.dp)
                .size(cloudSizeDp)
                .rotate(rotation)
                .alpha(cloud.alpha)
        )
    }
}

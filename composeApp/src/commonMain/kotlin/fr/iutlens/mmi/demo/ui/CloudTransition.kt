package fr.iutlens.mmi.demo.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.withTransform
import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.nuage1
import fr.iutlens.mmi.demo.nuage2
import org.jetbrains.compose.resources.painterResource
import kotlin.math.pow

private data class PassingCloud(
    val normX: Float,
    val sizeScale: Float,
    val speedFactor: Float,
    val delayFraction: Float,
    val rotation: Float,
    val imageIndex: Int
)

private val CLOUDS = listOf(
    PassingCloud(-0.22f, 2.1f, 1.00f, 0.00f,  4f, 1),
    PassingCloud(-0.04f, 2.0f, 1.20f, 0.10f,  7f, 0),
    PassingCloud( 0.14f, 2.0f, 0.82f, 0.20f, -4f, 1),
    PassingCloud( 0.24f, 2.1f, 0.80f, 0.08f, -5f, 1),
    PassingCloud( 0.54f, 2.0f, 1.30f, 0.18f, 11f, 0),
    PassingCloud( 0.84f, 2.1f, 0.75f, 0.05f, -8f, 1),
    PassingCloud( 1.08f, 2.0f, 1.10f, 0.28f, -3f, 0),
    PassingCloud( 0.10f, 2.1f, 0.85f, 0.38f, -6f, 1),
    PassingCloud( 0.42f, 2.0f, 1.25f, 0.48f, 10f, 0),
    PassingCloud( 0.74f, 2.1f, 0.90f, 0.58f,  8f, 1),
)

private fun easeInOut(t: Float): Float =
    if (t < 0.5f) 2f * t * t else 1f - (-2f * t + 2f).pow(2) / 2f

@Composable
fun CloudTransitionOverlay(progress: Float) {
    val painter1 = painterResource(Res.drawable.nuage1)
    val painter2 = painterResource(Res.drawable.nuage2)

    Canvas(modifier = Modifier.fillMaxSize()) {
        val sw = size.width
        val sh = size.height
        val baseSize = sh * 1.2f

for (cloud in CLOUDS) {
            val raw = ((progress - cloud.delayFraction) / (1f - cloud.delayFraction + 0.001f)).coerceIn(0f, 1f)
            val eased = easeInOut(raw.pow(1f / cloud.speedFactor))
            val cloudSize = baseSize * cloud.sizeScale
            val cy = (sh + cloudSize * 0.5f) + (-cloudSize * 1.2f - (sh + cloudSize * 0.5f)) * eased
            val cx = sw * cloud.normX
            val painter = if (cloud.imageIndex == 0) painter1 else painter2

            withTransform({
                translate(cx - cloudSize / 2f, cy - cloudSize / 2f)
                rotate(cloud.rotation, pivot = Offset(cloudSize / 2f, cloudSize / 2f))
            }) {
                with(painter) { draw(Size(cloudSize, cloudSize)) }
            }
        }
    }
}
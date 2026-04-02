package fr.iutlens.mmi.demo.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun Button(
    modifier: Modifier = Modifier,
    image: DrawableResource,
    onPress: () -> Unit = {},
    onRelease: () -> Unit = {}
) {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    Image(
        painter = painterResource(image),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier
            .scale(scale.value)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        scope.launch {
                            scale.animateTo(0.8f, tween(80))
                            scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
                        }
                        onPress()
                        tryAwaitRelease()
                        onRelease()
                    }
                )
            }
    )
}
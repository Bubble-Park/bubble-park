package fr.iutlens.mmi.demo.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun Button(
    modifier: Modifier = Modifier,
    image: DrawableResource,
    onPress: () -> Unit = {},
    onRelease: () -> Unit = {}
) {
    Image(
        painter = painterResource(image),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        onPress()
                        tryAwaitRelease()
                        onRelease()
                    }
                )
            }
    )
}

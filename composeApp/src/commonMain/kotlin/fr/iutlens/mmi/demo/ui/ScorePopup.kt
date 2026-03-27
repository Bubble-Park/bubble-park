package fr.iutlens.mmi.demo.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.offset
import fr.iutlens.mmi.demo.BubblePark
import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.dudu_font
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font

@Composable
fun ScorePopupText(
    popup: BubblePark.ScorePopup,
    screenXDp: Float,
    screenYDp: Float,
    onDone: () -> Unit
) {
    val duduFont = FontFamily(Font(Res.font.dudu_font))
    val scale = remember(popup.id) { Animatable(0f) }
    val offsetY = remember(popup.id) { Animatable(0f) }
    val alpha = remember(popup.id) { Animatable(1f) }

    LaunchedEffect(popup.id) {
        launch { scale.animateTo(1.5f, tween(120)) }
        kotlinx.coroutines.delay(120)
        launch { scale.animateTo(1.0f, tween(80)) }
        launch { offsetY.animateTo(-55f, tween(700)) }
        kotlinx.coroutines.delay(350)
        alpha.animateTo(0f, tween(350))
        onDone()
    }

    Text(
        text = "+${popup.points}",
        modifier = Modifier
            .offset(x = screenXDp.dp, y = (screenYDp + offsetY.value).dp)
            .scale(scale.value)
            .alpha(alpha.value),
        color = Color(0xFFFF69B4),
        fontSize = 28.sp,
        fontFamily = duduFont
    )
}

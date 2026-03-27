package fr.iutlens.mmi.demo.screens

import androidx.compose.animation.core.EaseInElastic
import androidx.compose.animation.core.EaseInExpo
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseInQuad
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import org.jetbrains.compose.resources.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.dino_font
import fr.iutlens.mmi.demo.logo
import fr.iutlens.mmi.demo.background
import fr.iutlens.mmi.demo.menu_content_fond
import fr.iutlens.mmi.demo.menu_nuages
import fr.iutlens.mmi.demo.menu_premier_plan_up
import fr.iutlens.mmi.demo.menu_second_plan
import fr.iutlens.mmi.demo.utils.Music
import fr.iutlens.mmi.demo.volume
import org.jetbrains.compose.resources.painterResource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.IntOffset
import fr.iutlens.mmi.demo.menu_volcan
import fr.iutlens.mmi.demo.ui.CloudsOverlay
import androidx.compose.runtime.withFrameMillis
import fr.iutlens.mmi.demo.utils.GameSound
import kotlinx.coroutines.delay

@Composable
fun MainMenu(onPlayClick: () -> Unit) {

    val dinoFont = FontFamily(
        Font(Res.font.dino_font)
    )

    // TEST TEMPORAIRE - sons
    GameSound.loadAll()
    LaunchedEffect(Unit) {
        delay(500) // Laisse le temps au SoundPool de charger
        GameSound.playHit(3)
    }

    var menuElapsed by remember { mutableStateOf(0L) }
    LaunchedEffect(Unit) {
        val start = withFrameMillis { it }
        while (true) {
            menuElapsed = withFrameMillis { it } - start
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val screenW = maxWidth.value
        val screenH = maxHeight.value

        // Offsets
        val dynamicLogoOffset = -(screenW * 0.20f).dp

        // Fonts size
        val playFontSize = (screenH * 0.19f).sp
        val creditsFontSize = (screenH * 0.17f).sp

        // Paddings
        val playPaddingStart = (screenW * 0.06f).dp
        val playPaddingTop = (screenH * 0.08f).dp
        val creditsPaddingStart = (screenW * 0.03f).dp

        // Strokes
        val dynamicStrokeWidth = screenH * 0.03f

        // Images de fond - Layer pour les animations
        Image (
            painter = painterResource(Res.drawable.background),
            contentDescription = "Fond du menu",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
        )

        // Nuages arrière-plan (derrière logo et boutons)
        CloudsOverlay(
            elapsed = menuElapsed,
            screenW = screenW,
            screenH = screenH,
            minDim = minOf(maxWidth, maxHeight),
            foreground = false
        )

        // Logo
        Image (
            painter = painterResource(Res.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = dynamicLogoOffset, y = 0.dp)
                .fillMaxWidth(0.33F)
        )

        MenuPanel(screenW, screenH) {
            MenuButton(
                onClick = onPlayClick,
                text = "JOUER",
                fontFamily = dinoFont,
                fillColor = Color(0xFF474534),
                outlineColor = Color.White,
                fontSize = playFontSize,
                strokeWidth = dynamicStrokeWidth,
                modifier = Modifier
                    .rotate(-4f)
                    .padding(start = playPaddingStart, top = playPaddingTop)
            )
            MenuButton(
                onClick = onPlayClick,
                text = "CREDITS",
                fontFamily = dinoFont,
                fillColor = Color.White,
                outlineColor = Color(0xFF474534),
                fontSize = creditsFontSize,
                strokeWidth = dynamicStrokeWidth,
                modifier = Modifier
                    .rotate(-7f)
                    .padding(start = creditsPaddingStart)
            )
            VolumeButton(
                modifier = Modifier
                    .size((screenH * 0.25f).dp)
                    .padding(start = creditsPaddingStart)
            )
        }
        
        // Version
        Text(
            text = "v1.0.0",
            color = Color.White,
            fontFamily = dinoFont,
            fontSize = (screenH * 0.05f).sp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start=8.dp)
                .alpha(0.7f)
        )
    }
}

@Composable
fun BoxScope.MenuPanel(
    screenW: Float,
    screenH: Float,
    content: @Composable ColumnScope.() -> Unit
) {
    val dynamicColumnWidth = (screenW * 0.36f).dp
    val dynamicColumnSpacing = -(screenH * 0.02f).dp

    Image(
        painter = painterResource(Res.drawable.menu_content_fond),
        contentDescription = null,
        alignment = Alignment.BottomEnd,
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .fillMaxHeight(0.75f)
    )
    Column(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .fillMaxHeight(0.75f)
            .width(dynamicColumnWidth),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(dynamicColumnSpacing),
        content = content
    )
}

@Composable
fun VolumeButton(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(Res.drawable.volume),
        contentDescription = if (Music.mute) "Son coupé" else "Son actif",
        modifier = modifier.clickable { Music.mute = !Music.mute }
    )
}

@Composable
fun OutlineText(
    text: String,
    fontFamily: FontFamily = FontFamily.Default,
    modifier: Modifier = Modifier,
    fillColor: Color = Color.White,
    outlineColor: Color = Color.Black,
    fontSize: TextUnit = 48.sp,
    strokeWidth: Float = 4f
) {
    val outineFontSize: TextUnit = (fontSize.value + 2).sp
    Box(modifier) {
        // Outline
        Text(
            text = text,
            fontSize = outineFontSize,
            color = outlineColor,
            fontFamily = fontFamily,
            style = TextStyle(
                drawStyle = Stroke(
                    width = strokeWidth,
                    join = StrokeJoin.Round
                )
            )
        )
        // Remplissage
        Text(
            text = text,
            fontSize = fontSize,
            color = fillColor,
            fontFamily = fontFamily
        )
    }
}

@Composable
fun MenuButton(
    onClick: () -> Unit,
    text: String,
    fontFamily: FontFamily = FontFamily.Default,
    modifier: Modifier = Modifier,
    fillColor: Color = Color.White,
    outlineColor: Color = Color.Black,
    fontSize: TextUnit = 48.sp,
    strokeWidth: Float = 4f
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
        elevation = ButtonDefaults.elevation(0.dp)
    ) {
        OutlineText(
            text = text,
            fontFamily = fontFamily,
            fillColor = fillColor,
            outlineColor = outlineColor,
            fontSize = fontSize,
            strokeWidth = strokeWidth,
            modifier = modifier
        )
    }
}

@Composable
fun animateOffsetBackground(
    durationMillis: Int = 5000,
    easing: Easing = EaseInOut,
    initialValue: Float = 0f,
    targetValue: Float = 0f
): Float {
    val infiniteTransition = rememberInfiniteTransition(label = "flottement_transition")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = initialValue,
        targetValue = targetValue,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = easing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flottement_y"
    )
    return offsetY
}
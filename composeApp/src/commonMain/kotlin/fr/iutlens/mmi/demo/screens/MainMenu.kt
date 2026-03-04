package fr.iutlens.mmi.demo.screens

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import fr.iutlens.mmi.demo.menu_background
import fr.iutlens.mmi.demo.menu_content_fond
import fr.iutlens.mmi.demo.menu_nuages
import fr.iutlens.mmi.demo.menu_premier_plan_up
import fr.iutlens.mmi.demo.menu_second_plan
import org.jetbrains.compose.resources.painterResource
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.IntOffset

@Composable
fun MainMenu(onPlayClick: () -> Unit) {

    val dinoFont = FontFamily(
        Font(Res.font.dino_font)
    )

    val infiniteTransition = rememberInfiniteTransition(label = "flottement_transition")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 35f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flottement_y"
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val screenW = maxWidth.value
        val screenH = maxHeight.value

        // Offsets
        val dynamicLogoOffset = -(screenW * 0.12f).dp
        val dynamicColumnWidth = (screenW * 0.36f).dp
        val dynamicColumnSpacing = -(screenH * 0.02f).dp

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
            painter = painterResource(Res.drawable.menu_background),
            contentDescription = "Fond du menu",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Image (
            painter = painterResource(Res.drawable.menu_nuages),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        )
        Image (
            painter = painterResource(Res.drawable.menu_second_plan),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )

        val offsetPremierPlan = animateOffsetBackground(durationMillis = 4000, initialValue = 30f)
        Image (
            painter = painterResource(Res.drawable.menu_premier_plan_up),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .offset { IntOffset(x = 0, y = offsetPremierPlan.dp.roundToPx()) }
        )

        // Logo
        Image (
            painter = painterResource(Res.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = dynamicLogoOffset, y = 0.dp)
                .fillMaxHeight(.55f)
        )

        // Images de contenu
        Image (
            painter = painterResource(Res.drawable.menu_content_fond),
            contentDescription = null,
            alignment = Alignment.BottomEnd,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .fillMaxHeight(.75f)
        )

        // Contenu du menu
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .fillMaxHeight(0.75f)
                .width(dynamicColumnWidth),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(dynamicColumnSpacing)
        ) {
            // Jouer
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
            // Crédits
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
        }
        
        // Version
        Text(
            text = "v1.0.0",
            color = Color.White,
            fontFamily = dinoFont,
            fontSize = (screenH * 0.06f).sp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
                .alpha(0.7f)
        )
    }
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
package fr.iutlens.mmi.demo.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import fr.iutlens.mmi.demo.game.sprite.squareWaveRotation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import fr.iutlens.mmi.demo.dudu_font
import fr.iutlens.mmi.demo.logo_medium
import fr.iutlens.mmi.demo.background
import fr.iutlens.mmi.demo.menu_accueil
import fr.iutlens.mmi.demo.menu_content_fond
import fr.iutlens.mmi.demo.menu_nuages
import fr.iutlens.mmi.demo.menu_premier_plan_up
import fr.iutlens.mmi.demo.menu_second_plan
import fr.iutlens.mmi.demo.utils.Music
import fr.iutlens.mmi.demo.volume_cut
import fr.iutlens.mmi.demo.volume_full
import fr.iutlens.mmi.demo.bestiaire
import org.jetbrains.compose.resources.painterResource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.IntOffset
import fr.iutlens.mmi.demo.menu_volcan
import fr.iutlens.mmi.demo.ui.CloudsOverlay
import androidx.compose.runtime.withFrameMillis

@Composable
fun MainMenu(onPlayClick: () -> Unit, onBestiaryClick: () -> Unit = {}, onCreditsClick: () -> Unit = {}) {

    val dinoFont = FontFamily(
        Font(Res.font.dudu_font)
    )

    var menuElapsed by remember { mutableStateOf(0L) }
    LaunchedEffect(Unit) {
        val start = withFrameMillis { it }
        while (true) {
            menuElapsed = withFrameMillis { it } - start
        }
    }

    val scaleLogo = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        scaleLogo.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
    }

    val rotLogo = squareWaveRotation(menuElapsed * 0.0015f, 2f)
    val rotJouer = squareWaveRotation(menuElapsed * 0.002f + 1f, 2f)
    val rotCredits = squareWaveRotation(menuElapsed * 0.0018f + 2f, 1.5f)
    val rotBestiaire = squareWaveRotation(menuElapsed * 0.0022f + 3f, 2f)
    val rotVolume = squareWaveRotation(menuElapsed * 0.0025f + 4f, 1.5f)

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
            painter = painterResource(Res.drawable.logo_medium),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = dynamicLogoOffset, y = 0.dp)
                .size(minOf(maxWidth, maxHeight) * 0.55f)
                .rotate(rotLogo)
                .scale(scaleLogo.value)
        )

        MenuPanel(
            screenW = screenW,
            screenH = screenH,
            dinoFont = dinoFont,
            onPlayClick = onPlayClick,
            onCreditsClick = onCreditsClick,
            onBestiaryClick = onBestiaryClick,
            rotJouer = rotJouer,
            rotCredits = rotCredits,
            rotBestiaire = rotBestiaire,
            rotVolume = rotVolume
        )
        
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
    dinoFont: FontFamily,
    onPlayClick: () -> Unit,
    onCreditsClick: () -> Unit,
    onBestiaryClick: () -> Unit,
    rotJouer: Float = -1.5f,
    rotCredits: Float = -6f,
    rotBestiaire: Float = -1f,
    rotVolume: Float = 2f,
) {
    val columnWidth = (screenW * 0.42f).dp
    val joueurFontSize = (screenH * 0.18f).sp
    val secondFontSize = (screenH * 0.10f).sp
    val volumeSize = (screenH * 0.15f).dp

    val scaleJouer = remember { Animatable(0f) }
    val scaleCredits = remember { Animatable(0f) }
    val scaleBestiaire = remember { Animatable(0f) }
    val scaleVolume = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch { scaleJouer.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)) }
        delay(120)
        launch { scaleCredits.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)) }
        launch { scaleBestiaire.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)) }
        delay(120)
        launch { scaleVolume.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)) }
    }

    Column(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(end = (screenW * 0.12f).dp, bottom = (screenH * 0.2f).dp)
            .fillMaxHeight(0.65f)
            .width(columnWidth),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        MenuButton(
            onClick = onPlayClick,
            text = "JOUER",
            fontFamily = dinoFont,
            fillColor = Color(0xFFF1934D),
            outlineColor = Color.Transparent,
            fontSize = joueurFontSize,
            strokeWidth = 0f,
            modifier = Modifier.rotate(rotJouer).scale(scaleJouer.value)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MenuButton(
                onClick = onCreditsClick,
                text = "Crédits",
                fontFamily = dinoFont,
                fillColor = Color.Black,
                outlineColor = Color.Transparent,
                fontSize = secondFontSize,
                strokeWidth = 0f,
                modifier = Modifier.rotate(rotCredits).scale(scaleCredits.value)
            )
            MenuButton(
                onClick = onBestiaryClick,
                text = "Bestiaire",
                fontFamily = dinoFont,
                fillColor = Color.Black,
                outlineColor = Color.Transparent,
                fontSize = secondFontSize,
                strokeWidth = 0f,
                modifier = Modifier.rotate(rotBestiaire).scale(scaleBestiaire.value)
            )
        }

        VolumeButton(modifier = Modifier.size(volumeSize).rotate(rotVolume).scale(scaleVolume.value))
    }
}

@Composable
fun VolumeButton(modifier: Modifier = Modifier) {
    val clickScale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    val icon = if (Music.mute) Res.drawable.volume_cut else Res.drawable.volume_full
    Image(
        painter = painterResource(icon),
        contentDescription = if (Music.mute) "Son coupé" else "Son actif",
        modifier = modifier
            .rotate(if (Music.mute) 25f else 0f)
            .scale(clickScale.value)
            .clickable {
                scope.launch {
                    clickScale.animateTo(1.3f, tween(80))
                    clickScale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
                }
                Music.mute = !Music.mute
            }
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
            maxLines = 1,
            softWrap = false,
            style = TextStyle(
                drawStyle = Stroke(
                    width = strokeWidth,
                    join = StrokeJoin.Round
                )
            )
        )
        Text(
            text = text,
            fontSize = fontSize,
            color = fillColor,
            fontFamily = fontFamily,
            maxLines = 1,
            softWrap = false,
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
    val clickScale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .scale(clickScale.value)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                scope.launch {
                    clickScale.animateTo(1.15f, tween(80))
                    clickScale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
                }
                onClick()
            }
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
            repeatMode = RepeatMode.Restart
        ),
        label = "flottement_y"
    )
    return offsetY
}
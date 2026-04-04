package fr.iutlens.mmi.demo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.unit.dp
import fr.iutlens.mmi.demo.BubblePark
import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.bubblechtein_sprites
import fr.iutlens.mmi.demo.parasaur_sprite
import fr.iutlens.mmi.demo.galliminus_sprite
import fr.iutlens.mmi.demo.background
import fr.iutlens.mmi.demo.bord_droit
import fr.iutlens.mmi.demo.bord_gauche
import fr.iutlens.mmi.demo.player_heart
import fr.iutlens.mmi.demo.slow_debuff
import fr.iutlens.mmi.demo.slow_bonus
import fr.iutlens.mmi.demo.trice_sprite
import fr.iutlens.mmi.demo.stego_sprite
import fr.iutlens.mmi.demo.gigano_sprite
import fr.iutlens.mmi.demo.game.DifficultyConfig
import fr.iutlens.mmi.demo.game.SlowEffect
import fr.iutlens.mmi.demo.game.FastAmmoEffect
import fr.iutlens.mmi.demo.fastammo_bonus
import fr.iutlens.mmi.demo.game.GameView
import fr.iutlens.mmi.demo.ui.Controllers
import fr.iutlens.mmi.demo.utils.GameSound
import fr.iutlens.mmi.demo.utils.SpriteSheet
import androidx.compose.material.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import fr.iutlens.mmi.demo.dudu_font

import androidx.compose.foundation.focusable
import androidx.compose.runtime.LaunchedEffect import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.IntSize
import fr.iutlens.mmi.demo.pause
import fr.iutlens.mmi.demo.JoystickPosition
import fr.iutlens.mmi.demo.bubble_sprite
import fr.iutlens.mmi.demo.ui.ShowChrono
import fr.iutlens.mmi.demo.ui.ShowLife
import fr.iutlens.mmi.demo.ui.ShowScore
import fr.iutlens.mmi.demo.ui.ScorePopupText
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.layout.ContentScale
import fr.iutlens.mmi.demo.environnement_map_sprite
import fr.iutlens.mmi.demo.niveau1_fond
import fr.iutlens.mmi.demo.trex_sprite
import fr.iutlens.mmi.demo.raptor_sprite
import fr.iutlens.mmi.demo.soleil
import fr.iutlens.mmi.demo.damage_border
import fr.iutlens.mmi.demo.game.sprite.squareWaveRotation
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.util.lerp
import fr.iutlens.mmi.demo.compy_sprite
import fr.iutlens.mmi.demo.dodo_sprite
import fr.iutlens.mmi.demo.tree
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.math.sin
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.IntOffset

import org.jetbrains.compose.resources.painterResource

@Composable
fun GameScreen(onExit: () -> Unit, onGameOver: (Int) -> Unit) {
    SpriteSheet.load(Res.drawable.niveau1_fond, 1, 1)
    SpriteSheet.load(Res.drawable.environnement_map_sprite, 5, 4, filterQuality = FilterQuality.High)
    SpriteSheet.load(Res.drawable.bubblechtein_sprites, 2, 2, filterQuality = FilterQuality.High)
    SpriteSheet.load(Res.drawable.bubble_sprite, 4, 3, filterQuality = FilterQuality.High)
    SpriteSheet.load(Res.drawable.trex_sprite, 2, 2, filterQuality = FilterQuality.High)
    SpriteSheet.load(Res.drawable.raptor_sprite, 2, 2, filterQuality = FilterQuality.High)
    SpriteSheet.load(Res.drawable.parasaur_sprite, 2, 2, filterQuality = FilterQuality.High)
    SpriteSheet.load(Res.drawable.galliminus_sprite, 2, 2, filterQuality = FilterQuality.High)
    SpriteSheet.load(Res.drawable.trice_sprite, 2, 2, filterQuality = FilterQuality.High)
    SpriteSheet.load(Res.drawable.stego_sprite, 2, 2, filterQuality = FilterQuality.High)
    SpriteSheet.load(Res.drawable.player_heart, 1, 1, filterQuality = FilterQuality.High)
    SpriteSheet.load(Res.drawable.compy_sprite, 2, 2, filterQuality = FilterQuality.High)
    SpriteSheet.load(Res.drawable.dodo_sprite, 2, 2, filterQuality = FilterQuality.High)
    SpriteSheet.load(Res.drawable.gigano_sprite, 2, 2, filterQuality = FilterQuality.High)
    SpriteSheet.load(Res.drawable.slow_bonus, 1, 1, filterQuality = FilterQuality.High)
    SpriteSheet.load(Res.drawable.slow_debuff, 1, 1, filterQuality = FilterQuality.High)
    SpriteSheet.load(Res.drawable.fastammo_bonus, 1, 1, filterQuality = FilterQuality.High)
    GameSound.loadAll()

    val gameData = remember { BubblePark() }
    var isPaused by remember { mutableStateOf(false) }
    fun lifeToScale(life: Int) = when (life) {
        3 -> 2f
        2 -> 1.3f
        else -> 1.1f
    }
    val damageScaleAnim = remember { Animatable(lifeToScale(gameData.player.life)) }
    val damagePulse by rememberInfiniteTransition(label = "damagePulse").animateFloat(
        initialValue = -0.015f,
        targetValue = 0.015f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "damagePulseFloat"
    )

    // Gestion du Clavier
    val focusRequester = remember { FocusRequester() }
    var keyState by remember { mutableStateOf(setOf<Key>()) }

    fun updateJoystickFromKeys(keys: Set<Key>) {
        var dx = 0f
        var dy = 0f
        if (Key.DirectionRight in keys) dx += 1f
        if (Key.DirectionLeft in keys) dx -= 1f
        if (Key.DirectionDown in keys) dy += 1f
        if (Key.DirectionUp in keys) dy -= 1f

        if (keys.isEmpty()) {
            gameData.game.joystickPosition = null
        } else {
            gameData.game.joystickPosition = JoystickPosition(
                Offset(dx + 1, dy + 1),
                IntSize(2, 2)
            )
        }
    }

    val shakeX = remember { Animatable(0f) }
    val shakeY = remember { Animatable(0f) }
    val scalePause = remember { Animatable(0f) }
    val scaleControllers = remember { Animatable(0f) }
    val clickScalePause = remember { Animatable(1f) }
    val borderSlide = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    // Ecran de jeu
    BoxWithConstraints(
        Modifier
            .fillMaxSize()
            .offset { IntOffset(shakeX.value.roundToInt(), shakeY.value.roundToInt()) }
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { event ->
                // Boutons
                if (event.key == Key.A) {
                    if (event.type == KeyEventType.KeyDown && !gameData.game.actionButtonA) {
                        gameData.player.shoot()
                    }

                    gameData.game.actionButtonA = (event.type == KeyEventType.KeyDown)
                    return@onKeyEvent true
                }

                if (event.key == Key.Z) {
                    gameData.game.actionButtonB = (event.type == KeyEventType.KeyDown)
                    return@onKeyEvent true
                }

                if (event.key in listOf(Key.DirectionUp, Key.DirectionDown, Key.DirectionLeft, Key.DirectionRight)) {
                    val newKeys = keyState.toMutableSet()
                    if (event.type == KeyEventType.KeyDown) newKeys.add(event.key)
                    else newKeys.remove(event.key)

                    keyState = newKeys
                    updateJoystickFromKeys(newKeys)
                    return@onKeyEvent true
                }
                false
            }
    ) {

        Image(
            painter = painterResource(Res.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        val screenW = maxWidth.value
        val screenH = maxHeight.value
        val minDim = minOf(maxWidth, maxHeight)
        val heartSize = minDim * 0.09f
        val uiFontSize = (minDim.value * 0.07f).sp
        val debuffIconSize = minDim * 0.07f
        val debuffFontSize = (minDim.value * 0.09f).sp
        val elapsed = gameData.game.elapsed
        val sunProgress = (1f - gameData.chrono.value / DifficultyConfig.TOTAL_LEVEL_TIME).coerceIn(0f, 1f)
        val sunX = lerp(-240f, screenW + 240f, sunProgress)
        val sunY = screenH * 0.3f - sin(sunProgress * PI).toFloat() * screenH * 0.6f
        val sunPhase = elapsed * PI.toFloat() / 500f
        val sunRotation = squareWaveRotation(sunPhase, 5f)

        Image(
            painter = painterResource(Res.drawable.soleil),
            contentDescription = null,
            modifier = Modifier
                .offset(x = sunX.dp, y = sunY.dp)
                .rotate(sunRotation)
                .size(240.dp)
        )

        // Arbre décoratif (derrière la grille)
        val treeXRatio = remember(gameData.levelIndex) { Random.nextFloat() }
        val treeSizeDp = minDim * 0.80f
        val treeX = treeXRatio * (screenW - treeSizeDp.value)
        val treeY = screenH - treeSizeDp.value * 0.95f
        Image(
            painter = painterResource(Res.drawable.tree),
            contentDescription = null,
            modifier = Modifier
                .offset(x = treeX.dp, y = treeY.dp)
                .size(treeSizeDp)
        )

        // Rendu du jeu
        GameView(
            modifier = Modifier.fillMaxSize(),
            gameData = gameData
        )

        // Score popups
        val density = LocalDensity.current
        val canvasWidthPx = with(density) { maxWidth.toPx() }
        val canvasHeightPx = with(density) { maxHeight.toPx() }
        val matrix = gameData.game.transform.getMatrix(Size(canvasWidthPx, canvasHeightPx))
        gameData.scorePopups.toList().forEach { popup ->
            val screenPosPx = matrix.map(Offset(popup.worldX, popup.worldY))
            val screenXDp = density.run { screenPosPx.x.toDp().value }
            val screenYDp = density.run { screenPosPx.y.toDp().value }
            ScorePopupText(
                popup = popup,
                screenXDp = screenXDp,
                screenYDp = screenYDp,
                onDone = { gameData.scorePopups.remove(popup) }
            )
        }

        Image(
            painter = painterResource(Res.drawable.damage_border),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize().alpha(0.2f).scale(damageScaleAnim.value + damagePulse)
        )

        Image(
            painter = painterResource(Res.drawable.bord_gauche),
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterStart)
                .graphicsLayer { translationX = -size.width * borderSlide.value }
        )

        Image(
            painter = painterResource(Res.drawable.bord_droit),
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterEnd)
                .graphicsLayer { translationX = size.width * borderSlide.value }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                ShowLife(gameData.player.life, maxLife = gameData.player.maxLife, heartSize = heartSize)
                ShowScore(gameData.score.get(), fontSize = uiFontSize)
                if (!gameData.isBossRound) ShowChrono(gameData.chrono.value, fontSize = uiFontSize)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = (minDim * 0.03f), end = (minDim * 0.03f))) {
                Image(
                    painter = painterResource(Res.drawable.pause),
                    contentDescription = "Pause",
                    modifier = Modifier
                        .size(minDim * 0.13f)
                        .padding(minDim * 0.01f)
                        .scale(scalePause.value)
                        .clickable {
                            isPaused = true
                            gameData.game.paused = true
                            gameData.chrono.pause()
                        }
                )
                val duduFont = FontFamily(Font(Res.font.dudu_font))
                if (SlowEffect.isActive) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.slow_bonus),
                            contentDescription = "Buff ralentissement",
                            modifier = Modifier.size(debuffIconSize)
                        )
                        Text(
                            text = "${(SlowEffect.timer / 50f).toInt()}s",
                            color = Color(0xFF474534),
                            fontSize = debuffFontSize,
                            fontFamily = duduFont
                        )
                    }
                }
                if (FastAmmoEffect.isActive) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.fastammo_bonus),
                            contentDescription = "Buff tir rapide",
                            modifier = Modifier.size(debuffIconSize)
                        )
                        Text(
                            text = "${(FastAmmoEffect.timer / 50f).toInt()}s",
                            color = Color(0xFF474534),
                            fontSize = debuffFontSize,
                            fontFamily = duduFont
                        )
                    }
                }
            }
        }

        // Barre de vie du boss
        val currentBossGigano = gameData.bossGigano
        if (gameData.isBossRound && currentBossGigano != null) {
            val maxHits = currentBossGigano.effectiveHitCount.toFloat()
            val hitsReceived = currentBossGigano.currentHitCount.toFloat()
            val hpFraction = (1f - hitsReceived / maxHits).coerceIn(0f, 1f)
            val duduFont = FontFamily(Font(Res.font.dudu_font))
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "GIGANO",
                    color = Color(0xFFCC2200),
                    fontSize = (minDim.value * 0.05f).sp,
                    fontFamily = duduFont
                )
                Box(
                    modifier = Modifier
                        .width((screenW * 0.45f).dp)
                        .height(14.dp)
                        .background(Color(0xFF333333), shape = RoundedCornerShape(7.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(hpFraction)
                            .background(Color(0xFFCC2200), shape = RoundedCornerShape(7.dp))
                    )
                }
            }
        }

        // Combo près du joueur (masqué si x1)
        if (gameData.comboMultiplier > 1) {
            val comboMatrix = gameData.game.transform.getMatrix(Size(canvasWidthPx, canvasHeightPx))
            val playerScreenPx = comboMatrix.map(Offset(gameData.player.x, gameData.player.y))
            val comboXDp = density.run { (playerScreenPx.x + 55f).toDp() }
            val comboYDp = density.run { (playerScreenPx.y - 30f).toDp() }
            val comboFont = FontFamily(Font(Res.font.dudu_font))
            val comboFadeThresholdMs = BubblePark.COMBO_RESET_INTERVAL_MS / 2f
            val comboScale = (gameData.comboTimeRemainingMs / comboFadeThresholdMs).coerceIn(0f, 1f)
            Text(
                text = "x${gameData.comboMultiplier}",
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = comboXDp, y = comboYDp)
                    .rotate(-12f)
                    .scale(comboScale),
                color = Color(0xFFFF69B4),
                fontSize = (minDim.value * 0.09f).sp,
                fontFamily = comboFont
            )
        }

        if (isPaused) {
            PauseScreen(
                life = gameData.player.life,
                maxLife = gameData.player.maxLife,
                score = gameData.score.get(),
                damageScale = damageScaleAnim.value + damagePulse,
                onResume = {
                    isPaused = false
                    gameData.game.paused = false
                    gameData.chrono.resume()
                    gameData.game.invalidate()
                },
                onQuit = onExit
            )
        } else if (gameData.showUpgradeScreen) {
            UpgradeScreen(
                choices = gameData.upgradeChoices,
                onUpgradeSelected = { upgrade -> gameData.selectUpgrade(upgrade) }
            )
        } else {
            Controllers(
                modifier = Modifier.fillMaxSize().scale(scaleControllers.value),
                onJoystickChange = { pos -> gameData.game.joystickPosition = pos },
                onActionA = { pressed ->
                    if (pressed && !gameData.game.actionButtonA) gameData.player.shoot()
                    gameData.game.actionButtonA = pressed
                },
                onActionB = { pressed -> gameData.game.actionButtonB = pressed }
            )
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(gameData.player.isDeathAnimationComplete, gameData.levelIndex) {
        if (gameData.player.isDeathAnimationComplete) onGameOver(gameData.score.get())
    }

    LaunchedEffect(Unit) {
        gameData.onLevelEnd = { hasNextLevel ->
            if (hasNextLevel) gameData.loadNextLevel()
            else onExit()
        }
    }

    LaunchedEffect(gameData.player.life, gameData.levelIndex) {
        damageScaleAnim.animateTo(lifeToScale(gameData.player.life), tween(500, easing = EaseInOut))
    }

    LaunchedEffect(gameData.levelIndex) {
        scalePause.snapTo(0f)
        scaleControllers.snapTo(0f)
        borderSlide.snapTo(1f)
        launch { scalePause.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)) }
        launch {
            kotlinx.coroutines.delay(150L)
            scaleControllers.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
        }
        launch {
            if (gameData.levelIndex == 0) kotlinx.coroutines.delay(2800L)
            borderSlide.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium))
        }
    }

    LaunchedEffect(gameData.comboMultiplier) {
        if (gameData.comboMultiplier <= 1) return@LaunchedEffect
        repeat(3) {
            launch { shakeX.animateTo(if (it % 2 == 0) 8f else -8f, tween(40)) }
            launch { shakeY.animateTo(if (it % 2 == 0) 5f else -5f, tween(40)) }
            kotlinx.coroutines.delay(40)
        }
        launch { shakeX.animateTo(0f, tween(40)) }
        launch { shakeY.animateTo(0f, tween(40)) }
    }
}
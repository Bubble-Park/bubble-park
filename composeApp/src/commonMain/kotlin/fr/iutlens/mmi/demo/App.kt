package fr.iutlens.mmi.demo

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import fr.iutlens.mmi.demo.menu_accueil
import fr.iutlens.mmi.demo.screens.GameOverScreen
import fr.iutlens.mmi.demo.screens.GameScreen
import fr.iutlens.mmi.demo.screens.MenuHost
import fr.iutlens.mmi.demo.ui.CloudTransitionOverlay
import org.jetbrains.compose.resources.painterResource

enum class GameState {
    MENU,
    PLAYING,
    GAME_OVER
}

@Composable
fun App(modifier: Modifier = Modifier) {
    var currentState by remember { mutableStateOf(GameState.MENU) }
    var lastScore by remember { mutableStateOf(0) }
    var lastLevel by remember { mutableStateOf(0) }
    var transitionKey by remember { mutableStateOf(0) }
    var showCloudOverlay by remember { mutableStateOf(false) }
    val coverProgress = remember { Animatable(0f) }

    LaunchedEffect(transitionKey) {
        if (transitionKey == 0) return@LaunchedEffect
        showCloudOverlay = true
        coverProgress.snapTo(0f)
        coverProgress.animateTo(0.5f, tween(1800))
        currentState = GameState.PLAYING
        coverProgress.animateTo(1f, tween(2800))
        showCloudOverlay = false
    }

    MaterialTheme {
        Box(modifier = modifier.fillMaxSize().background(Color.White)) {

            Image(
                painter = painterResource(Res.drawable.menu_accueil),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            when (currentState) {
                GameState.MENU -> MenuHost(
                    onPlayClick = { transitionKey++ }
                )
                GameState.PLAYING -> GameScreen(
                    onExit = { currentState = GameState.MENU },
                    onGameOver = { score, level -> lastScore = score; lastLevel = level; currentState = GameState.GAME_OVER }
                )
                GameState.GAME_OVER -> GameOverScreen(
                    score = lastScore,
                    levelIndex = lastLevel,
                    onReplay = { currentState = GameState.PLAYING },
                    onQuit = { currentState = GameState.MENU }
                )
            }

            if (showCloudOverlay) {
                CloudTransitionOverlay(progress = coverProgress.value)
            }
        }
    }
}

@Preview(device = "spec:width=852dp,height=393dp,dpi=240")
@Composable
fun AppPreview() {
    App()
}

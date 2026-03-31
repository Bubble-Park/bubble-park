package fr.iutlens.mmi.demo

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import fr.iutlens.mmi.demo.screens.BestiaryScreen
import fr.iutlens.mmi.demo.screens.GameOverScreen
import fr.iutlens.mmi.demo.screens.GameScreen
import fr.iutlens.mmi.demo.screens.MainMenu
import org.jetbrains.compose.resources.painterResource

enum class GameState {
    MENU,
    PLAYING,
    GAME_OVER,
    BESTIARY
}

@Composable
fun App(modifier: Modifier = Modifier) {
    var currentState by remember { mutableStateOf(GameState.MENU) }
    var lastScore by remember { mutableStateOf(0) }

    MaterialTheme {
        Box(modifier = modifier.fillMaxSize()) {
            // Fond partagé entre tous les écrans
            Image(
                painter = painterResource(Res.drawable.background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            AnimatedContent(
                targetState = currentState,
                transitionSpec = {
                    when {
                        targetState == GameState.PLAYING ->
                            slideInHorizontally(tween(450)) { it } togetherWith
                            slideOutHorizontally(tween(450)) { -it }
                        initialState == GameState.PLAYING ->
                            slideInHorizontally(tween(450)) { -it } togetherWith
                            slideOutHorizontally(tween(450)) { it }
                        else ->
                            fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                    }
                },
                modifier = Modifier.fillMaxSize(),
                label = "screen_transition"
            ) { state ->
                when (state) {
                    GameState.MENU -> MainMenu(
                        onPlayClick = { currentState = GameState.PLAYING },
                        onBestiaryClick = { currentState = GameState.BESTIARY }
                    )
                    GameState.BESTIARY -> BestiaryScreen(
                        onBack = { currentState = GameState.MENU }
                    )
                    GameState.PLAYING -> GameScreen(
                        onExit = { currentState = GameState.MENU },
                        onGameOver = { score -> lastScore = score; currentState = GameState.GAME_OVER }
                    )
                    GameState.GAME_OVER -> GameOverScreen(
                        score = lastScore,
                        onReplay = { currentState = GameState.PLAYING },
                        onQuit = { currentState = GameState.MENU }
                    )
                }
            }
        }
    }
}

@Preview(device = "spec:width=852dp,height=393dp,dpi=240")
@Composable
fun AppPreview() {
    App()
}

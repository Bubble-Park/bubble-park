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

    MaterialTheme {
        Box(modifier = modifier.fillMaxSize().background(Color.White)) {

            Image(
                painter = painterResource(Res.drawable.menu_accueil),
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
                    GameState.MENU -> MenuHost(
                        onPlayClick = { currentState = GameState.PLAYING }
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

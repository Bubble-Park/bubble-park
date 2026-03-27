package fr.iutlens.mmi.demo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import fr.iutlens.mmi.demo.screens.GameOverScreen
import fr.iutlens.mmi.demo.screens.GameScreen
import fr.iutlens.mmi.demo.screens.MainMenu

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
        Box(modifier = modifier.fillMaxSize()) {
            when (currentState) {
                GameState.MENU -> MainMenu(
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
@Preview(device = "spec:width=852dp,height=393dp,dpi=240")
@Composable
fun AppPreview() {
    App()
}
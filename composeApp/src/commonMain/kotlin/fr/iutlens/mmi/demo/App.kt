package fr.iutlens.mmi.demo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.iutlens.mmi.demo.screens.GameScreen
import fr.iutlens.mmi.demo.screens.MainMenu

enum class GameState {
    MENU,
    PLAYING
}

@Composable
fun App(modifier: Modifier = Modifier) {
    var currentState by remember { mutableStateOf(GameState.MENU) }

    MaterialTheme {
        Box(modifier = modifier.size(width = 852.dp, height = 393.dp)) {
            when (currentState) {
                GameState.MENU -> MainMenu(
                    onPlayClick = { currentState = GameState.PLAYING }
                )
                GameState.PLAYING -> GameScreen(
                    onExit = { currentState = GameState.MENU }
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

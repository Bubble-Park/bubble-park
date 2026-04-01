package fr.iutlens.mmi.demo.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import fr.iutlens.mmi.demo.utils.Music

private enum class MenuSubScreen { MAIN, CREDITS, BESTIARY }

@Composable
fun MenuHost(onPlayClick: () -> Unit) {
    var subScreen by remember { mutableStateOf(MenuSubScreen.MAIN) }

    Music("files/main_theme.mp3")

    AnimatedContent(
        targetState = subScreen,
        transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
        modifier = Modifier.fillMaxSize(),
        label = "menu_sub_transition"
    ) { screen ->
        when (screen) {
            MenuSubScreen.MAIN -> MainMenu(
                onPlayClick = onPlayClick,
                onCreditsClick = { subScreen = MenuSubScreen.CREDITS },
                onBestiaryClick = { subScreen = MenuSubScreen.BESTIARY }
            )
            MenuSubScreen.CREDITS -> CreditsScreen(
                onBack = { subScreen = MenuSubScreen.MAIN }
            )
            MenuSubScreen.BESTIARY -> BestiaryScreen(
                onBack = { subScreen = MenuSubScreen.MAIN }
            )
        }
    }
}

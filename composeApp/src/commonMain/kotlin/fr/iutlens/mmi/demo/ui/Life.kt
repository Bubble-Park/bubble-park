package fr.iutlens.mmi.demo.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.components.Player
import fr.iutlens.mmi.demo.player_heart
import fr.iutlens.mmi.demo.player_heart_empty
import org.jetbrains.compose.resources.painterResource

/**
 * Affiche la vie sur l'écran de jeu
 * @param player Vie du joueur à afficher
 */
@Composable
fun ShowLife(life: Int) {
    Row() {
        repeat(3) { index ->
            val isFullHeart = index < life
            val iconRes = if (isFullHeart) Res.drawable.player_heart else Res.drawable.player_heart_empty
            val iconDesc = if (isFullHeart) "Coeur plein" else "Coeur vide"
            Image(
                painter = painterResource(iconRes),
                contentDescription = iconDesc,
                modifier = Modifier
                    .size(32.dp)
            )
        }
    }
}
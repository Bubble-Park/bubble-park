package fr.iutlens.mmi.demo.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.dudu_font
import fr.iutlens.mmi.demo.player_heart
import fr.iutlens.mmi.demo.player_heart_empty
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

@Composable
fun ShowScore(score: Int, fontSize: TextUnit = 44.sp) {
    val duduFont = FontFamily(Font(Res.font.dudu_font))
    Text(
        text = "Score : $score",
        modifier = Modifier.padding(start = 16.dp),
        color = Color(0xFF474534),
        fontSize = fontSize,
        fontFamily = duduFont
    )
}

/**
 * Affiche la vie sur l'écran de jeu
 * @param player Vie du joueur à afficher
 */
@Composable
fun ShowChrono(time: Float, fontSize: TextUnit = 44.sp) {
    val duduFont = FontFamily(Font(Res.font.dudu_font))
    Text(
        text = "Temps : ${time.toInt()}s",
        modifier = Modifier.padding(start = 16.dp),
        color = Color(0xFF474534),
        fontSize = fontSize,
        fontFamily = duduFont
    )
}

@Composable
fun ShowLife(life: Int, heartSize: Dp = 72.dp) {
    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(3) { index ->
            val isFullHeart = index < life
            var rotate: Float = 4f
            if (index == 1) rotate = -8f
            else if (index == 2) rotate = 6f
            val iconRes = if (isFullHeart) Res.drawable.player_heart else Res.drawable.player_heart_empty
            val iconDesc = if (isFullHeart) "Coeur plein" else "Coeur vide"
            Image(
                painter = painterResource(iconRes),
                contentDescription = iconDesc,
                modifier = Modifier
                    .size(heartSize)
                    .rotate(rotate)
            )
        }
    }
}

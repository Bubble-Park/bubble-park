package fr.iutlens.mmi.demo.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.dino_font
import fr.iutlens.mmi.demo.dudu_font
import fr.iutlens.mmi.demo.trou
import fr.iutlens.mmi.demo.ui.ShowLife
import org.jetbrains.compose.resources.Font
import androidx.compose.ui.text.font.FontFamily
import org.jetbrains.compose.resources.painterResource

@Composable
fun GameOverScreen(score: Int, onReplay: () -> Unit, onQuit: () -> Unit) {
    val dinoFont = FontFamily(Font(Res.font.dino_font))
    val duduFont = FontFamily(Font(Res.font.dudu_font))

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenW = maxWidth.value
        val screenH = maxHeight.value

        val contentWidthFraction = 0.64f
        val playFontSize = (screenH * 0.19f).sp
        val quitFontSize = (screenH * 0.17f).sp
        val dynamicStrokeWidth = screenH * 0.03f
        val scoreFontSize = (screenH * 0.13f).sp
        val playPaddingStart = (screenW * 0.06f).dp
        val playPaddingTop = (screenH * 0.08f).dp
        val quitPaddingStart = (screenW * 0.03f).dp

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxHeight()
                .fillMaxWidth(contentWidthFraction)
                .padding(top = (screenH * 0.09f).dp, start = (screenW * 0.06f).dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(Res.drawable.trou),
                    contentDescription = null,
                    modifier = Modifier.fillMaxHeight(0.85f).aspectRatio(1f)
                )
                Box(modifier = Modifier.padding(end = (screenW * 0.02f).dp)) {
                    ShowLife(0, heartSize = (screenH * 0.12f).dp)
                }
            }

            // Score
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.25f)
                    .padding(horizontal = (screenW * 0.04f).dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Score",
                    fontFamily = duduFont,
                    color = Color(0xFF474534),
                    fontSize = scoreFontSize
                )
                Text(
                    text = "$score",
                    fontFamily = duduFont,
                    color = Color(0xFF474534),
                    fontSize = scoreFontSize
                )
            }

            Spacer(modifier = Modifier.weight(0.25f))
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .fillMaxHeight(0.75f)
                .width((screenW * 0.36f).dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(-(screenH * 0.02f).dp)
        ) {
            MenuButton(
                onClick = onReplay,
                text = "JOUER",
                fontFamily = dinoFont,
                fillColor = Color(0xFF474534),
                outlineColor = Color.White,
                fontSize = playFontSize,
                strokeWidth = dynamicStrokeWidth,
                modifier = Modifier
                    .rotate(-4f)
                    .padding(start = playPaddingStart, top = playPaddingTop)
            )
            MenuButton(
                onClick = onQuit,
                text = "QUITTER",
                fontFamily = dinoFont,
                fillColor = Color.White,
                outlineColor = Color(0xFF474534),
                fontSize = quitFontSize,
                strokeWidth = dynamicStrokeWidth,
                modifier = Modifier
                    .rotate(-7f)
                    .padding(start = quitPaddingStart)
            )
            VolumeButton(
                modifier = Modifier
                    .size((screenH * 0.25f).dp)
                    .padding(start = quitPaddingStart)
            )
        }
    }
}

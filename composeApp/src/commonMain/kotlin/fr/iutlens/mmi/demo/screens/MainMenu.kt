package fr.iutlens.mmi.demo.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import org.jetbrains.compose.resources.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.iutlens.mmi.demo.App
import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.dino_font
import fr.iutlens.mmi.demo.game.sprite.Sprite
import fr.iutlens.mmi.demo.logo
import fr.iutlens.mmi.demo.menu_background
import fr.iutlens.mmi.demo.menu_content_fond
import fr.iutlens.mmi.demo.menu_nuages
import fr.iutlens.mmi.demo.menu_premier_plan
import fr.iutlens.mmi.demo.menu_premier_plan_up
import fr.iutlens.mmi.demo.menu_second_plan
import fr.iutlens.mmi.demo.menu_volcan
import fr.iutlens.mmi.demo.utils.SpriteSheet
import org.jetbrains.compose.resources.painterResource

@Composable
fun MainMenu(onPlayClick: () -> Unit) {

    val dinoFont = FontFamily(
        Font(Res.font.dino_font)
    )

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Images de fond - Layer pour les animations
        Image (
            painter = painterResource(Res.drawable.menu_background),
            contentDescription = "Fond du menu",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Image (
            painter = painterResource(Res.drawable.menu_nuages),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        )
        Image (
            painter = painterResource(Res.drawable.menu_second_plan),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )
        Image (
            painter = painterResource(Res.drawable.menu_premier_plan_up),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )

        // Logo
        Image (
            painter = painterResource(Res.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(-150.dp, 0.dp)
        )

        // Images de contenu
        Image (
            painter = painterResource(Res.drawable.menu_content_fond),
            contentDescription = null,
            alignment = Alignment.BottomEnd,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .fillMaxHeight(.75f)
                .width(300.dp)
        )

        // Contenu du menu
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .fillMaxHeight(0.75f)
                .width(300.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(-(10.dp))
        ) {
            // Jouer
            MenuButton(
                onClick = onPlayClick,
                text = "JOUER",
                fontFamily = dinoFont,
                fillColor = Color(0xFF474534),
                outlineColor = Color.White,
                fontSize = 70.sp,
                strokeWidth = 10f,
                modifier = Modifier
                    .rotate(-4f)
                    .padding(start = 60.dp, top = 40.dp)
            )
            // Crédits
            MenuButton(
                onClick = onPlayClick,
                text = "CREDITS",
                fontFamily = dinoFont,
                fillColor = Color.White,
                outlineColor = Color(0xFF474534),
                fontSize = 65.sp,
                strokeWidth = 10f,
                modifier = Modifier
                    .rotate(-7f)
                    .padding(start = 34.dp)
            )
        }
        
        // Version
        Text(
            text = "v1.0.0",
            color = Color.White,
            fontFamily = dinoFont,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
                .alpha(0.7f)
        )
    }
}

@Composable
fun OutlineText(
    text: String,
    fontFamily: FontFamily = FontFamily.Default,
    modifier: Modifier = Modifier,
    fillColor: Color = Color.White,
    outlineColor: Color = Color.Black,
    fontSize: TextUnit = 48.sp,
    strokeWidth: Float = 4f
) {
    val outineFontSize: TextUnit = (fontSize.value + 2).sp
    Box(modifier) {
        // Outline
        Text(
            text = text,
            fontSize = outineFontSize,
            color = outlineColor,
            fontFamily = fontFamily,
            style = TextStyle(
                drawStyle = Stroke(
                    width = strokeWidth,
                    join = StrokeJoin.Round
                )
            )
        )
        // Remplissage
        Text(
            text = text,
            fontSize = fontSize,
            color = fillColor,
            fontFamily = fontFamily
        )
    }
}

@Composable
fun MenuButton(
    onClick: () -> Unit,
    text: String,
    fontFamily: FontFamily = FontFamily.Default,
    modifier: Modifier = Modifier,
    fillColor: Color = Color.White,
    outlineColor: Color = Color.Black,
    fontSize: TextUnit = 48.sp,
    strokeWidth: Float = 4f
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
        elevation = ButtonDefaults.elevation(0.dp)
    ) {
        OutlineText(
            text = text,
            fontFamily = fontFamily,
            fillColor = fillColor,
            outlineColor = outlineColor,
            fontSize = fontSize,
            strokeWidth = strokeWidth,
            modifier = modifier
        )
    }
}
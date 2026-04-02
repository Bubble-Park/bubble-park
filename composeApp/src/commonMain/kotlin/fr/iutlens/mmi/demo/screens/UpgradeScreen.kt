package fr.iutlens.mmi.demo.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.background
import fr.iutlens.mmi.demo.damage_border
import fr.iutlens.mmi.demo.dino_font
import fr.iutlens.mmi.demo.dudu_font
import fr.iutlens.mmi.demo.game.upgrade.Upgrade
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

@Composable
fun UpgradeScreen(
    choices: List<Upgrade>,
    onUpgradeSelected: (Upgrade) -> Unit
) {
    val dinoFont = FontFamily(Font(Res.font.dino_font))
    val duduFont = FontFamily(Font(Res.font.dudu_font))

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenW = maxWidth.value
        val screenH = maxHeight.value

        Image(
            painter = painterResource(Res.drawable.background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Image(
            painter = painterResource(Res.drawable.damage_border),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.5f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = (screenW * 0.04f).dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "BOSS VAINCU !",
                fontFamily = dinoFont,
                color = Color(0xFFCC2200),
                fontSize = (screenH * 0.13f).sp,
                modifier = Modifier.rotate(-3f)
            )

            Spacer(modifier = Modifier.height((screenH * 0.04f).dp))

            Text(
                text = "Choisissez une amélioration",
                fontFamily = duduFont,
                color = Color(0xFF474534),
                fontSize = (screenH * 0.07f).sp
            )

            Spacer(modifier = Modifier.height((screenH * 0.06f).dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy((screenW * 0.03f).dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                choices.forEach { upgrade ->
                    UpgradeCard(
                        upgrade = upgrade,
                        dinoFont = dinoFont,
                        duduFont = duduFont,
                        screenW = screenW,
                        screenH = screenH,
                        onClick = { onUpgradeSelected(upgrade) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun UpgradeCard(
    upgrade: Upgrade,
    dinoFont: FontFamily,
    duduFont: FontFamily,
    screenW: Float,
    screenH: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cornerRadius = (screenH * 0.04f).dp
    val borderWidth = (screenH * 0.007f).dp

    Box(
        modifier = modifier
            .border(
                width = borderWidth,
                color = Color(0xFF474534),
                shape = RoundedCornerShape(cornerRadius)
            )
            .background(
                color = Color(0xFFFFF8E7).copy(alpha = 0.92f),
                shape = RoundedCornerShape(cornerRadius)
            )
            .clickable(onClick = onClick)
            .padding(
                horizontal = (screenW * 0.02f).dp,
                vertical = (screenH * 0.05f).dp
            ),
        contentAlignment = Alignment.Center
    ) {
        val image = upgrade.imageRes
        if (image != null) {
            Image(
                painter = painterResource(image),
                contentDescription = upgrade.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.75f)
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = upgrade.name,
                    fontFamily = dinoFont,
                    color = Color(0xFF474534),
                    fontSize = (screenH * 0.10f).sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height((screenH * 0.025f).dp))
                Text(
                    text = upgrade.description,
                    fontFamily = duduFont,
                    color = Color(0xFF474534).copy(alpha = 0.75f),
                    fontSize = (screenH * 0.055f).sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

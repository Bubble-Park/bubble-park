package fr.iutlens.mmi.demo.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.iutlens.mmi.demo.App
import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.game.sprite.Sprite
import fr.iutlens.mmi.demo.menu_background
import fr.iutlens.mmi.demo.menu_nuages
import fr.iutlens.mmi.demo.menu_premier_plan
import fr.iutlens.mmi.demo.menu_premier_plan_up
import fr.iutlens.mmi.demo.menu_second_plan
import fr.iutlens.mmi.demo.menu_volcan
import fr.iutlens.mmi.demo.utils.SpriteSheet
import org.jetbrains.compose.resources.painterResource

@Composable
fun MainMenu(onPlayClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
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

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "BUBBLE PARK",
                color = Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.h1
            )
            
            Spacer(modifier = Modifier.height(64.dp))
            
            Button(
                onClick = onPlayClick,
                modifier = Modifier
                    .width(200.dp)
                    .height(56.dp)
            ) {
                Text(text = "JOUER", fontSize = 20.sp)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { /* TODO: Paramètres */ },
                modifier = Modifier
                    .width(200.dp)
                    .height(56.dp)
            ) {
                Text(text = "OPTIONS", fontSize = 20.sp)
            }
        }
        
        Text(
            text = "v1.0.0",
            color = Color.Gray,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}
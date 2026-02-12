package fr.iutlens.mmi.demo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.unit.dp
import fr.iutlens.mmi.demo.BubblePark
import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.bubblechtein_sprites
import fr.iutlens.mmi.demo.game.GameView
import fr.iutlens.mmi.demo.sprites_bubblepark_map_v1
import fr.iutlens.mmi.demo.ui.Controllers
import fr.iutlens.mmi.demo.utils.SpriteSheet

import androidx.compose.foundation.focusable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.IntSize
import fr.iutlens.mmi.demo.JoystickPosition
import fr.iutlens.mmi.demo.plateformes_spritesheet

@Composable
fun GameScreen(onExit: () -> Unit) {
    SpriteSheet.load(Res.drawable.plateformes_spritesheet, 4, 1)
    SpriteSheet.load(Res.drawable.bubblechtein_sprites, 10, 3, filterQuality = FilterQuality.High)

    val gameData = remember { BubblePark() }
    
    // Gestion du Clavier
    val focusRequester = remember { FocusRequester() }
    var keyState by remember { mutableStateOf(setOf<Key>()) }

    fun updateJoystickFromKeys(keys: Set<Key>) {
        var dx = 0f
        var dy = 0f
        if (Key.DirectionRight in keys) dx += 1f
        if (Key.DirectionLeft in keys) dx -= 1f
        if (Key.DirectionDown in keys) dy += 1f
        if (Key.DirectionUp in keys) dy -= 1f
        
        if (keys.isEmpty()) {
             gameData.game.joystickPosition = null
        } else {
             gameData.game.joystickPosition = JoystickPosition(
                Offset(dx + 1, dy + 1), 
                IntSize(2, 2)
            )
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { event ->
                // Boutons
                if (event.key == Key.A) {
                    if (event.type == KeyEventType.KeyDown && !gameData.game.actionButtonA) {
                         gameData.shoot()
                    }
                    gameData.game.actionButtonA = (event.type == KeyEventType.KeyDown)
                    return@onKeyEvent true
                }
                if (event.key == Key.Z) {
                    gameData.game.actionButtonB = (event.type == KeyEventType.KeyDown)
                    return@onKeyEvent true
                }
                
                if (event.key in listOf(Key.DirectionUp, Key.DirectionDown, Key.DirectionLeft, Key.DirectionRight)) {
                    val newKeys = keyState.toMutableSet()
                    if (event.type == KeyEventType.KeyDown) newKeys.add(event.key)
                    else newKeys.remove(event.key)
                    
                    keyState = newKeys
                    updateJoystickFromKeys(newKeys)
                    return@onKeyEvent true
                }
                false
            }
    ) {
        GameView(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.DarkGray),
            gameData = gameData
        )

        Controllers(
            modifier = Modifier.fillMaxSize(),
            onJoystickChange = { gameData.game.joystickPosition = it },
            onActionA = { pressed ->
                if (pressed && !gameData.game.actionButtonA) gameData.shoot()
                gameData.game.actionButtonA = pressed
            },
            onActionB = { pressed -> gameData.game.actionButtonB = pressed }
        )
    }
    
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

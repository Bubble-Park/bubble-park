package fr.iutlens.mmi.demo

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import fr.iutlens.mmi.demo.App
import fr.iutlens.mmi.demo.game.currentGame

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        focusable = true,
        title = "KMPTest",
        onKeyEvent = { event ->
            //println(event)
            if(event.type == KeyEventType.KeyDown){
                val direction =
                    when (event.key) {
                        Key.DirectionRight, Key.D -> Offset(1f,0f)
                        Key.DirectionLeft, Key.Q -> Offset(-1f,0f)
                        Key.DirectionUp, Key.Z -> Offset(0f,-1f)
                        Key.DirectionDown, Key.S -> Offset(0f,1f)
                        else-> null
                    }
                if (direction != null){
                    currentPadAction?.let { it(direction) }
                    currentGame?.joystickPosition = JoystickPosition(
                        Offset(direction.x + 1, direction.y + 1),
                        IntSize(2, 2)
                    )
                    true
                } else {
                    currentGame?.joystickPosition = null
                }
                false
            } else false
        }
    ) {
        App()
    }
}
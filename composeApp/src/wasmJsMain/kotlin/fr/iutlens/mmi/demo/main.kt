package fr.iutlens.mmi.demo

import androidx.compose.foundation.focusable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeViewport
import fr.iutlens.mmi.demo.App
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val handleKeyDown: (Event) -> Unit = { event ->
        (event as KeyboardEvent).let {
            val direction = when (it.key) {
                "ArrowLeft","q" -> Offset(-1f,0f)
                "ArrowRight","d" -> Offset(1f,0f)
                "ArrowUp","z" ->  Offset(0f,-1f)
                "ArrowDown","s" -> Offset(0f,1f)
                else -> null
            }
            if (direction != null) {
                currentPadAction?.let { it(direction) }
            }
        }
    }
    window.addEventListener("keydown", handleKeyDown)
    ComposeViewport(document.body!!) {
        App(Modifier.focusable(true).onKeyEvent { event ->
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
                    true
                }
                false
            } else false
        })
    }
}
package fr.iutlens.mmi.demo

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntSize
import kotlin.math.atan2
import kotlin.math.sqrt

class JoystickPosition(offset: Offset, size: IntSize){
    val x by lazy{(2*offset.x / size.width - 1f).coerceIn(-1f..1f)}
    val y by lazy{(2*offset.y / size.height - 1f).coerceIn(-1f..1f)}
    val norm  by lazy{sqrt(x*x.toDouble() + y*y.toDouble())}
    val angle by lazy{atan2(y.toDouble(),x.toDouble())}
    val isCentered by lazy { x == 0f && y == 0f }

    companion object {
        val centered = JoystickPosition(Offset(0.5f, 0.5f), IntSize(1, 1))
    }
}

@Composable
fun Joystick(
    modifier: Modifier,
    onChange: (JoystickPosition?) -> Unit
) {
    var knobOffset by remember { mutableStateOf(Offset.Zero) }

    Canvas(
        modifier = modifier
            .aspectRatio(1f)
            .pointerInput(onChange) {
                awaitPointerEventScope {
                    while (true) {
                        val down = awaitPointerEvent().changes.firstOrNull { it.pressed } ?: continue
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val maxRadius = size.width / 2f * 0.55f

                        fun updateKnob(pos: Offset) {
                            val delta = pos - center
                            val dist = sqrt(delta.x * delta.x + delta.y * delta.y)
                            knobOffset = if (dist > maxRadius) delta / dist * maxRadius else delta
                            onChange(JoystickPosition(pos, size))
                        }

                        updateKnob(down.position)

                        var pressed = true
                        while (pressed) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull()
                            if (change == null || !change.pressed) {
                                pressed = false
                            } else {
                                updateKnob(change.position)
                            }
                        }

                        knobOffset = Offset.Zero
                        onChange(null)
                    }
                }
            }
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val baseRadius = size.width / 2f
        val knobRadius = baseRadius * 0.38f

        // Base
        drawCircle(
            color = Color(0x55000000),
            radius = baseRadius,
            center = center
        )
        drawCircle(
            color = Color(0x66FFFFFF),
            radius = baseRadius,
            center = center,
            style = Stroke(width = 4f)
        )

        // Knob
        drawCircle(
            color = Color(0xCCFFFFFF),
            radius = knobRadius,
            center = center + knobOffset
        )
        drawCircle(
            color = Color(0x88000000),
            radius = knobRadius,
            center = center + knobOffset,
            style = Stroke(width = 3f)
        )
    }
}

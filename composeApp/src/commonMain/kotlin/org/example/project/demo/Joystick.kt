package fr.iutlens.mmi.demo

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntSize
import kmptest.composeapp.generated.resources.Res
import kmptest.composeapp.generated.resources.pad
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import kotlin.math.atan2
import kotlin.math.sqrt

class JoystickPosition(offset: Offset, size: IntSize){
    val x by lazy{(2*offset.x / size.width - 1f).coerceIn(-1f..1f)}
    val y by lazy{(2*offset.y / size.height - 1f).coerceIn(-1f..1f)}
    val norm  by lazy{sqrt(x*x.toDouble() + y*y.toDouble())}
    val angle by lazy{atan2(y.toDouble(),x.toDouble())}
    val isCentered by lazy { x == 0f && y == 0f }

    companion object {
        val centered =  JoystickPosition(Offset(0.5f,0.5f), IntSize(1,1))
    }
}

@Composable
fun Joystick(modifier: Modifier,
             image : DrawableResource = Res.drawable.pad,
             onChange: (JoystickPosition)->Unit){

    Image( painterResource(resource = image), null,modifier = modifier.aspectRatio(1f)
        .pointerInput(key1 = onChange) {
            detectTapGestures(
                onPress = {
                    onChange(JoystickPosition(it,size))
                },
                onTap = {
                    onChange(JoystickPosition.centered)
                }

            )
        }
        .pointerInput(key1 = onChange){
            detectDragGestures(onDragStart = {
                onChange(JoystickPosition(it,size))
            }, onDragEnd = {
                onChange(JoystickPosition.centered)
            }, onDragCancel = {
                onChange(JoystickPosition.centered)
            }

            ) { change, dragAmount ->
                onChange(JoystickPosition(change.position,size))
            }
        })

}
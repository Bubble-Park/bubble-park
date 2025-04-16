package fr.iutlens.mmi.demo

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.abs

@Composable
fun Pad(modifier: Modifier, image: DrawableResource = Res.drawable.pad, action: (Offset) -> Unit){

    fun getDirection(p: Float): Float{
        if (p<-0.1) return -1f
        if (p>0.1) return 1f
        return 0f
    }

    Image( painterResource(resource = image), null,modifier = modifier.aspectRatio(1f)
        .pointerInput(key1 = action){
            val (sizeX,sizeY) = size
            detectTapGestures {(x,y)->
              //  println("$x, $y")
                val dx = x/sizeX-0.5f
                val dy = y/sizeY-0.5f
                action(
                    if (abs(dx)>abs(dy)){ Offset(getDirection(dx),0f) } else Offset(0f,getDirection(dy))
                )
            }
        })
}



@Composable
@Preview
fun PadPreview(){
    Pad(modifier = Modifier.width(200.dp)){_ -> }
}
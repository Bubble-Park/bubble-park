package fr.iutlens.mmi.demo.utils


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kmptest.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.imageResource

/**
 * SpriteSheet représente une feuille de sprites, c'est à dire une image découpée
 * en plusieurs cases, que l'on peut utiliser comme autant d'images distinctes en y
 * accédant par un numéro
 *
 * @property bitmap image à découper
 * @property sizeX nombre de colonnes
 * @property sizeY nombre de lignes
 * @constructor Create empty Sprite sheet
 */
class SpriteSheet(
    val bitmap: ImageBitmap,
    val sizeX: Int, val sizeY: Int,
    val padding: Int) {

    private val _rawSpriteWidth =  bitmap.width/sizeX
    private val _rawSpriteHeight = bitmap.height/sizeY
    /**
     * SpriteWidth : largeur d'un sprite
     */
    val spriteWidth = _rawSpriteWidth-padding*2

    /**
     * SpriteHeight : hauteur d'un sprite
     */
    val spriteHeight = _rawSpriteHeight-padding*2
    val size = IntSize(spriteWidth,spriteHeight)

    fun left(ndx : Int) = (ndx%sizeX)* _rawSpriteWidth+padding
    fun top(ndx: Int) = (ndx/sizeX)* _rawSpriteHeight+padding
    fun offset(ndx: Int) = IntOffset(left(ndx),top(ndx))

    fun paint(drawScope: DrawScope, ndx : Int, x : Int, y : Int){
      //  if (spriteSheet==null) throw NoSuchElementException("No SpriteSheet for this image resource. Use SpriteSheet.load(resource)")
        drawScope.drawImage(bitmap, offset(ndx), size, IntOffset(x,y),
            alpha = 1f,
            filterQuality = FilterQuality.None)
    }

    companion object {
         val map =  mutableStateMapOf<DrawableResource, SpriteSheet>()

        val values get() = map.values.toTypedArray()

         fun isLoaded(vararg res : DrawableResource) = res.all { it in map  && map[it]!!.sizeX>0}

        @Composable
        fun load(drawableResource: DrawableResource, sizeX: Int, sizeY: Int, padding : Int = 0) {
            val bitmap = imageResource(drawableResource)
            map[drawableResource] = SpriteSheet(bitmap, sizeX, sizeY,padding)
        }

        fun load(drawableResource: DrawableResource, image : ImageBitmap, sizeX: Int, sizeY: Int, padding : Int = 0) {
            //if (map.containsKey(drawableResource)) return
            map[drawableResource] = SpriteSheet(image, sizeX, sizeY,padding)
        }

        operator fun get(drawableResource: DrawableResource): SpriteSheet = map[drawableResource]
                ?: throw NoSuchElementException("No SpriteSheet for this image resource. Use SpriteSheet.load(resource)")

    }
}

@Composable
fun OnceSpriteLoaded(vararg res: DrawableResource, content : @Composable ()-> Unit){
    if (SpriteSheet.isLoaded(*res)){
        content()
    }
}



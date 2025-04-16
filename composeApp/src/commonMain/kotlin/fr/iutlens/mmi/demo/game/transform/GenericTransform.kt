package fr.iutlens.mmi.demo.game.transform


import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Matrix

import fr.iutlens.mmi.demo.game.sprite.Sprite
import fr.iutlens.mmi.demo.game.sprite.TiledArea
import kotlin.math.max
import kotlin.math.min


interface Constraint {
    fun maxScaleH(length: Float): Float
    fun maxScaleV(length: Float): Float
    fun offsetH(length: Float): Float
    fun offsetV(length: Float): Float


    class Fill(var sprite: Sprite) : Constraint {
        override fun maxScaleH(length: Float) = length/sprite.boundingBox.width
        override fun offsetH(length: Float) = -sprite.boundingBox.center.x

        override fun maxScaleV(length: Float) = length/ sprite.boundingBox.height
        override fun offsetV(length: Float) = -sprite.boundingBox.center.y
    }

    class Focus(val tiledArea: TiledArea, var sprite: Sprite, var minTiles: Int) : Constraint {
        override fun maxScaleH(length: Float) = length / tiledArea.w / minTiles
        override fun offsetH(length: Float) = -sprite.boundingBox.center.x

        override fun maxScaleV(length: Float) = length / tiledArea.h / minTiles
        override fun offsetV(length: Float) = -sprite.boundingBox.center.y
    }

    class Center(var sprite: Sprite) : Constraint {
        override fun maxScaleH(length: Float) = 0f
        override fun offsetH(length: Float) = -sprite.boundingBox.center.x
        override fun maxScaleV(length: Float) = 0f
        override fun offsetV(length: Float) = -sprite.boundingBox.center.y
    }
}

class GenericTransform( var hConstraint: Constraint,
                        var vConstraint: Constraint = hConstraint,
                        var keepInside: Sprite? = null) :
    CameraTransform {

    private val transform = Matrix()
    private val reverse = Matrix()

    override fun getPoint(offset: Offset) = reverse.map(offset)

    override fun getMatrix(size: Size): Matrix {

        val hs = hConstraint.maxScaleH(size.width)
        val vs = vConstraint.maxScaleV(size.height)
        var scale = when{
            hs==0f -> vs
            vs==0f -> hs
            else -> min(hs,vs )
        }

        var hOffset = hConstraint.offsetH(size.width)
        var vOffset = vConstraint.offsetV(size.height)

        keepInside?.boundingBox?.let {
            scale = scale.coerceAtLeast(size.width/it.width)
                        .coerceAtLeast(size.height/it.height)
            hOffset = hOffset.coerceIn(-it.right + size.width/scale/2 .. -it.left - size.width/scale/2)
            vOffset = vOffset.coerceIn(-it.bottom + size.height/scale/2 .. -it.top - size.height/scale/2)

        }


        transform.reset()
        transform.translate(size.width/2, size.height/2)
        transform.scale(scale, scale)
        transform.translate(hOffset, vOffset)
        reverse.setFrom(transform)
        reverse.invert()

        return  transform
    }

}

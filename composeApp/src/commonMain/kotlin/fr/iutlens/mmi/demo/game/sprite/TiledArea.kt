package fr.iutlens.mmi.demo.game.sprite

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.DrawScope
import fr.iutlens.mmi.demo.utils.SpriteSheet
import org.jetbrains.compose.resources.DrawableResource

/**
 * Une tiled area représente une grille de sprite définie à partir d'une feuille de sprite
 * et d'un tableau à deux dimensions (data) de numéros de sprite. Dans chaque case de la grille, le
 * sprite avec le numéro indiqué est dessiné.
 * Un objet de la classe TiledArea peut être utilisé comme un Sprite (on peut donc le dessiner)
 * et comme une TileMap (on peut accéder au contenu de chaque case, aux dimensions etc...)
 *
 * @property sprite feuille de sprite
 * @property data indices dans la feuille
 * @constructor Crée une grille de sprite à partir de la feuille de sprite (spriteSheet) et d'un
 * tableau des numéros de sprites (data)
 */
class TiledArea(var res : DrawableResource,  val  tileMap: TileMap) : Sprite {

    val sprite get() = SpriteSheet[res]
    /**
     * W largeur d'une case, en pixels
     */
    val w  get() = sprite.spriteWidth

    /**
     * H hauteur d'une case, en pixels
     */
    val h  get() = sprite.spriteHeight

    override fun paint(drawScope: DrawScope, elapsed: Long) {
        tileMap.foreach{x,y, value ->
                sprite.paint(
                    drawScope,
                    value,
                    (x * w) / tileMap.tileSizeX,
                    (y * h) / tileMap.tileSizeY
                )
        }

    }

    override val boundingBox get() = Rect(0f,0f,
        (w*tileMap.xMax / tileMap.tileSizeX).toFloat(),
        (h*tileMap.yMax /tileMap.tileSizeY).toFloat())
    override fun update() {
    }
}

/**
 * Retourne une TiledArea construite sur la feuille de sprite et un tableau
 *
 * @param data
 */
fun DrawableResource.tiledArea(data: TileMap) = TiledArea(this,data)
package fr.iutlens.mmi.demo.game.sprite

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.IntSize
import fr.iutlens.mmi.demo.utils.SpriteSheet
import kotlin.random.Random
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
class TiledArea(var res : DrawableResource, val tileMap: TileMap, val scaledTiles: Map<Int, ClosedFloatingPointRange<Float>> = emptyMap()) : Sprite {

    val sprite get() = SpriteSheet[res]
    /**
     * W largeur d'une case, en pixels
     */
    val w  get() = sprite.spriteWidth-3

    /**
     * H hauteur d'une case, en pixels
     */
    val h  get() = sprite.spriteHeight-3

    private var birthElapsed = -1L

    companion object {
        const val TILE_STAGGER_MS = 25L
        const val TILE_POP_DURATION_MS = 350L
    }

    fun spawnEndMs(): Long = (tileMap.xMax + tileMap.yMax) * TILE_STAGGER_MS + TILE_POP_DURATION_MS

    override fun paint(drawScope: DrawScope, elapsed: Long) {
        if (birthElapsed < 0L) birthElapsed = elapsed
        val localElapsed = elapsed - birthElapsed

        tileMap.foreach { x, y, value ->
            val px = (x * w) / tileMap.tileSizeX
            val py = (y * h) / tileMap.tileSizeY
            val delay = (x + y) * TILE_STAGGER_MS
            val animT = ((localElapsed - delay).toFloat() / TILE_POP_DURATION_MS).coerceIn(0f, 1f)
            if (animT <= 0f) return@foreach

            val drawTile: DrawScope.() -> Unit = {
                val scaleRange = scaledTiles[value]
                if (scaleRange != null) {
                    val tileRng = Random(x.toLong() * 1000L + y.toLong())
                    val s = scaleRange.start + tileRng.nextFloat() * (scaleRange.endInclusive - scaleRange.start)
                    val scaledW = (sprite.spriteWidth * s).toInt()
                    val scaledH = (sprite.spriteHeight * s).toInt()
                    val scaledX = px + sprite.spriteWidth / 2 - scaledW / 2
                    val scaledY = py + sprite.spriteHeight - scaledH
                    sprite.paint(this, value, scaledX, scaledY, IntSize(scaledW, scaledH))
                } else {
                    sprite.paint(this, value, px, py)
                }
            }

            if (animT >= 1f) {
                drawScope.drawTile()
            } else {
                val popScale = spawnScale(1f - animT)
                val cx = px + sprite.spriteWidth / 2f
                val cy = py + sprite.spriteHeight / 2f
                drawScope.withTransform({ scale(popScale, popScale, pivot = Offset(cx, cy)) }) {
                    drawTile()
                }
            }
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
package fr.iutlens.mmi.demo.game.sprite


import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import fr.iutlens.mmi.demo.utils.SpriteSheet
import org.jetbrains.compose.resources.DrawableResource


/**
 * Représente un sprite défini par une feuille de sprite et une numéro de sprite (ndx) et une position (x,y)
 * Le comportement du sprite peut être défini via la propriété action
 *
 * @property spriteSheet feuille de sprite utilisée pour dessiner ce sprite
 * @property x position en x (en pixel dans les coordonnées de référence)
 * @property y position en y (en pixel dans les coordonnées de référence)
 * @property ndx numéro du sprite dans la feuille
 * @property action action à réaliser entre deux images
 * @constructor Crée un sprite à partir de la feuille (spriteSheet), la position (x,y) et le numéro
 * de l'image dans la feuille. On peut préciser en plus une action à réaliser entre deux images pour
 * animer le sprite
 */
open class BasicSprite(var res : DrawableResource,
                       var x: Float, var y: Float,
                       var ndx : Int = 0,
                       var action: (BasicSprite.()->Unit)? = null) : Sprite {

    val spriteSheet get() =  SpriteSheet[res]

    // taille du sprite en pixels, divisée par deux (pour le centrage)
    private val w2 get() = spriteSheet.spriteWidth / 2
    private val h2 get() = spriteSheet.spriteHeight / 2

    override fun paint(drawScope: DrawScope, elapsed: Long) =
        drawScope.withTransform({translate(x,y)}){
            spriteSheet.paint(this, ndx, -w2, -h2)
        }


//rectangle occuppé par le sprite
    override val boundingBox get() = Rect(x - w2, y - h2, x + w2, y + h2)
    override fun update() {action?.invoke(this)}
}

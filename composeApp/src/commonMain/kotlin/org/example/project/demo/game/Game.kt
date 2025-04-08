package fr.iutlens.mmi.demo.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import fr.iutlens.mmi.demo.JoystickPosition
import fr.iutlens.mmi.demo.game.sprite.Sprite
import fr.iutlens.mmi.demo.game.sprite.SpriteList
import fr.iutlens.mmi.demo.game.transform.CameraTransform
import kotlinx.coroutines.delay
import kotlin.time.TimeSource

/**
 * Game regroupe les éléments constitutifs d'un jeu
 *
 * @property background Le sprite à afficher en le fond
 * @property spriteList Les sprites à afficher (dans l'ordre de la liste)
 * @property transform La transformation (changement de coordonnées, déplacement) à appliquer
 * @property onDragStart Action à réaliser quand commence un drag and drop. Icompatible avec onTap
 * @property onDragMove Action à réaliser quand on bouge pendant le drag and drop. Incompatible avec onTap
 * @property onTap Action à réaliser quand on clique. Incompatible avec onDrag
 * @constructor Créé un jeu définit par sprite de fond (background), une liste de sprite à afficher
 * par dessus (spriteList) et un point de vue (transform)
 * Il est possible de préciser en plus les interactions (onDrag/onTap)
 */
class Game(val background : Sprite,
           val spriteList : SpriteList<*>,
           val transform: CameraTransform,
           var onDragStart: (Game.(Offset) -> Unit)? = null,
           var onDragMove:  (Game.(Offset) -> Unit)? = null,
           var onTap: (Game.(Offset)-> Unit)? = null
        ) {
    var padAction: ((Offset) -> Unit)? = null
    var joystickPosition: JoystickPosition? = null

    val timeSource = TimeSource.Monotonic

    /**
     * Start Instant du début du jeu, utiliser pour calculer le temps écoulé
     */
    val start = timeSource.markNow()

    /**
     * Elapsed Mesure le temps écoulé entre début du jeu et la dernière demande d'affichage
     */
    var elapsed by mutableLongStateOf(0L)

    /**
     * Nombre de milliseconde souhaité entre deux images
     */
    var animationDelayMs: Int? = null

    /**
     * Update : action à réaliser entre deux images
     */
    var update: ((Game)-> Unit)? = null

    /**
     * Invalidate demande une nouvelle image, en général parce que les données du jeu ont changé
     */
    fun invalidate() {
        elapsed = (timeSource.markNow() - start).inWholeMilliseconds
    }

    /**
     * View
     * Composant affichant le jeu décrit dans cette classe
     * Si des actions sont prévues pour des clics ou le drag and drop, elles sont aussi configurées
     * Si un rafraîchissement automatique est prévu (update et animationDelayMS non nuls), il est planifié
     *
     * @param modifier
     */
    @Composable
    fun View(modifier: Modifier) {
        var m = modifier
        onTap?.let { _onTap ->
            m = m.pointerInput(key1 = _onTap) {
                detectTapGestures {
                    _onTap(transform.getPoint(it))
                    invalidate()
                }
            }
        }
        if (onDragMove!= null) {
            m = m.pointerInput(key1 = onDragStart to onDragMove){
                detectDragGestures(onDragStart = {
                    onDragStart?.invoke(this@Game,transform.getPoint(it))
                }) { change, dragAmount ->
                    onDragMove?.invoke(this@Game,transform.getPoint(change.position))
                }
            }
        }

        // gestion des évènements
        Canvas(modifier = m) {
            // Dessin proprement dit. On précise la transformation à appliquer avant
            this@Canvas.withTransform({ transform(transform.getMatrix(size)) }) {
                background.paint(this, elapsed)
                spriteList.paint(this, elapsed)
            }
        }
        // Gestion du rafraîssement automatique si update et animationDelay sont défnis
        update?.let{myUpdate->
            animationDelayMs?.let {delay ->
                LaunchedEffect(elapsed){
                    //Calcul du temps avant d'afficher la prochaine image, et pause si nécessaire)
                    val current = (timeSource.markNow()-start).inWholeMilliseconds
                    val next = elapsed+ delay
                    if (next>current) delay(next-current)
                    myUpdate(this@Game)
                }
            }
        }
    }
}


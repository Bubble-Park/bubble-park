package fr.iutlens.mmi.demo.game.sprite

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.drawscope.DrawScope

/**
 * Sprite représente un élément graphique animable (un sprite)
 */
interface Sprite {
    /**
     * Paint dessine le sprite dans le contexte donné
     * @param drawScope contexte graphique
     * @param elapsed temps écoulé depuis le début du jeu
     */
    fun paint(drawScope: DrawScope, elapsed: Long = 0L)

    /**
     * Bounding box calcule la boîte la plus petite contenant le sprite
     */
    val boundingBox: Rect

    /**
     * Update demande au sprite de réaliser son action (par exemple bouger)
     */
    fun update()
}
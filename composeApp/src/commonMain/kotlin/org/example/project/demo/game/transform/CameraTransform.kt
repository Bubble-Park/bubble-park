package fr.iutlens.mmi.demo.game.transform

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Matrix

/**
 * CameraTransform représente un placement de caméra (centrage, zoom etc...)
 */
interface CameraTransform {
    /**
     * Obtient la matrice de transformation correspondant à une zone à remplir
     *
     * @param size taille de la zone à remplir
     * @return la matrice de transformation calculée
     */
    fun getMatrix(size: Size): Matrix

    /**
     * Obtient les coordonnées dans le repère d'origine d'un point à l'écran
     *
     * @param offset coordonnées à l'écran
     * @return coordonnées dans le repère original
     */
    fun getPoint(offset: Offset): Offset

}
package fr.iutlens.mmi.demo.game.sprite

import kotlin.math.PI

/**
 * Calcule une rotation en square wave entre +intensity et -intensity.
 *
 * @param phase    Accumulateur de phase, incrémenté chaque frame par [speed]
 * @param intensity Amplitude max de la rotation en degrés
 * @return Angle de rotation en degrés
 */
fun squareWaveRotation(phase: Float, intensity: Float): Float {
    if (phase == 0f) return 0f
    val cycle = 2 * PI.toFloat()
    return if (phase % cycle < PI.toFloat()) intensity else -intensity
}

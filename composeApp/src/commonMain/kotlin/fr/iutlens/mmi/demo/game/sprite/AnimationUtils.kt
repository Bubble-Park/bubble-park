package fr.iutlens.mmi.demo.game.sprite

import kotlin.math.PI

/**
 * Calcule une rotation en square wave entre +intensity et -intensity.
 * @param phase    Accumulateur de phase, incrémenté chaque frame par la vitesse
 * @param intensity Amplitude max de la rotation en degrés
 * @return Angle de rotation en degrés
 */
fun squareWaveRotation(phase: Float, intensity: Float): Float {
    if (phase == 0f) return 0f
    val cycle = 2 * PI.toFloat()
    return if (phase % cycle < PI.toFloat()) intensity else -intensity
}

/**
 * Scale up léger au moment du hit, qui redescend progressivement à 1f.
 * @param stunRatio Ratio de stun restant, de 1f (début) à 0f (fin)
 */
fun hitScale(stunRatio: Float): Float {
    if (stunRatio > 40f) return 1f
    return 1f + 0.25f
}

/**
 * Rotation ample en square wave, indépendante de la marche.
 * @param phase    Valeur quelconque utilisée comme accumulateur de phase
 * @param intensity Amplitude en degrés (plus grande que squareWaveRotation)
 */
fun hitRotation(phase: Float, intensity: Float): Float {
    val cycle = 2 * PI.toFloat()
    return if (phase % cycle < PI.toFloat()) intensity else -intensity
}

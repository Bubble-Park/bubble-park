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
fun hitScale(stunRatio: Float): Float = 1f + 0.25f * stunRatio

/**
 * Scale d'apparition : 0f au début du spawn, 1f à la fin de la phase d'animation.
 * @param ratio 1f au 1er tick de l'animation, 0f au dernier
 */
fun spawnScale(ratio: Float): Float = 1f - ratio

/**
 * Rotation d'apparition : -10° au début, 0° à la fin de la phase d'animation.
 * @param ratio 1f au 1er tick de l'animation, 0f au dernier
 */
fun spawnRotation(ratio: Float): Float = -10f * ratio

/**
 * Rotation ample en square wave, indépendante de la marche.
 * @param phase    Valeur quelconque utilisée comme accumulateur de phase
 * @param intensity Amplitude en degrés (plus grande que squareWaveRotation)
 */
fun hitRotation(phase: Float, intensity: Float): Float {
    val cycle = 2 * PI.toFloat()
    return if (phase % cycle < PI.toFloat()) intensity else -intensity
}

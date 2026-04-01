package fr.iutlens.mmi.demo.utils

import androidx.compose.runtime.Composable
import kotlin.math.pow
import kotlin.random.Random

object GameSound {

    const val HIT_1 = "hit_1.wav"
    const val HIT_2 = "hit_2.wav"
    const val HIT_3 = "hit_3.wav"
    const val DOWN = "down.wav"
    const val BONUS = "bonus.wav"
    const val POINT_FINAL = "point_final.wav"
    const val POINT_COMBO = "point_combo.wav"

    const val WALK = "walk.mp3"
    const val JUMP = "jump.mp3"
    const val JUMP_VOICE = "jump_voice.mp3"
    private const val BULLE_1 = "bulle_1.mp3"
    private const val BULLE_2 = "bulle_2.mp3"
    private const val BULLE_3 = "bulle_3.mp3"

    // 1 chance sur JUMP_VOICE_CHANCE de jouer la voix en plus du son de saut
    const val JUMP_VOICE_CHANCE = 10

    private val BULLE_SEQUENCE = arrayOf(BULLE_1, BULLE_2, BULLE_3)
    private var bulleIndex = 0

    @Composable
    fun loadAll() {
        Music.loadSound(HIT_1)
        Music.loadSound(HIT_2)
        Music.loadSound(HIT_3)
        Music.loadSound(DOWN)
        Music.loadSound(BONUS)
        Music.loadSound(POINT_FINAL)
        Music.loadSound(POINT_COMBO)
        Music.loadSound(WALK)
        Music.loadSound(JUMP)
        Music.loadSound(JUMP_VOICE)
        Music.loadSound(BULLE_1)
        Music.loadSound(BULLE_2)
        Music.loadSound(BULLE_3)
    }

    /**
     * Joue le son de hit correspondant aux PV du joueur avant le coup.
     * @param lifeBefore vie du joueur avant le dégât (3, 2 ou 1)
     */
    fun playHit(lifeBefore: Int) {
        val sound = when (lifeBefore) {
            3 -> HIT_1
            2 -> HIT_2
            else -> HIT_3
        }
        Music.playSound(sound)
    }

    /** Joue le prochain son de bulle dans le cycle 1→2→3→1→... */
    fun playBubble() {
        Music.playSound(BULLE_SEQUENCE[bulleIndex], leftVolume = 0.3f, rightVolume = 0.3f)
        bulleIndex = (bulleIndex + 1) % BULLE_SEQUENCE.size
    }

    /** Joue le son de saut, avec 1 chance sur JUMP_VOICE_CHANCE de jouer la voix en plus. */
    fun playJump() {
        Music.playSound(JUMP, leftVolume = 0.4f, rightVolume = 0.4f)
        if (Random.nextInt(JUMP_VOICE_CHANCE) == 0) {
            Music.playSound(JUMP_VOICE, leftVolume = 0.5f, rightVolume = 0.5f)
        }
    }

    /** Joue le son de marche (à appeler manuellement en boucle depuis le Player). */
    fun playWalk() = Music.playSound(WALK, leftVolume = 0.1f, rightVolume = 0.1f)

    /** Stoppe immédiatement le son de marche. */
    fun stopWalk() = Music.stopSound(WALK)

    fun playBonus() = Music.playSound(BONUS)
    fun playDown() = Music.playSound(DOWN)
    fun playPointFinal() = Music.playSound(POINT_FINAL)
    // Pitch shift maximum sur le son de capture (en semitones)
    const val COMBO_PITCH_MAX_SEMITONES = 5

    fun playPointCombo(comboMultiplier: Int = 1) {
        val semitones = comboMultiplier.coerceAtMost(COMBO_PITCH_MAX_SEMITONES).toFloat()
        val rate = 2.0.pow(semitones / 12.0).toFloat()
        Music.playSound(POINT_COMBO, rate = rate)
    }
}

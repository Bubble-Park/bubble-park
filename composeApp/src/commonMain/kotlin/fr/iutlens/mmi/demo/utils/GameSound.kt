package fr.iutlens.mmi.demo.utils

import androidx.compose.runtime.Composable

object GameSound {

    const val HIT_1 = "hit_1.wav"
    const val HIT_2 = "hit_2.wav"
    const val HIT_3 = "hit_3.wav"
    const val DOWN = "down.wav"
    const val BONUS = "bonus.wav"
    const val POINT_FINAL = "point_final.wav"
    const val POINT_COMBO = "point_combo.wav"
    // musique_angoissante.wav est une musique de fond → utiliser Music.invoke() avec MusicPlayer

    @Composable
    fun loadAll() {
        Music.loadSound(HIT_1)
        Music.loadSound(HIT_2)
        Music.loadSound(HIT_3)
        Music.loadSound(DOWN)
        Music.loadSound(BONUS)
        Music.loadSound(POINT_FINAL)
        Music.loadSound(POINT_COMBO)
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

    fun playBonus() = Music.playSound(BONUS)
    fun playDown() = Music.playSound(DOWN)
    fun playPointFinal() = Music.playSound(POINT_FINAL)
    fun playPointCombo() = Music.playSound(POINT_COMBO)
}

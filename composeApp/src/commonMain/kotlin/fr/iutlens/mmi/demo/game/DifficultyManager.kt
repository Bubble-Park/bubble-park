package fr.iutlens.mmi.demo.game

import kotlin.math.ceil
import kotlin.math.roundToInt

object DifficultyConfig {
    const val INIT_DIFFICULTY: Float      = 1.0f
    const val INIT_MAX_DINO: Int          = 5
    const val INIT_SPAWN_DELAY: Float     = 3f
    const val DIFF_INCREMENT: Float       = 0.2f

    const val LOCAL_DIFF_INCREMENT: Float = 0.2f
    const val MAX_LOCAL_INCREMENT: Int    = 3
    const val LOCAL_DIFF_INTERVAL: Float  = 10f

    val TOTAL_LEVEL_TIME: Float = LOCAL_DIFF_INTERVAL * (MAX_LOCAL_INCREMENT + 1).toFloat()

    const val RATIO_WANDER: Float = 0.25f
    const val RATIO_FLEE: Float   = 0.45f
    const val RATIO_CHASE = 0.30f
}

data class LevelDifficulty(
    val level: Int,
    val difficulty: Float,
    val maxDino: Int,
    val spawnDelay: Float,
    val maxWander: Int,
    val maxFlee: Int,
    val maxChase: Int
)

object DifficultyManager {

    /**
     * Calcule les paramètres de base du niveau (1-indexé).
     * @param level Index du niveau
     */
    fun getLevelDifficulty(level: Int): LevelDifficulty {
        val difficulty = DifficultyConfig.INIT_DIFFICULTY +
                         (level - 1) * DifficultyConfig.DIFF_INCREMENT
        val maxDino    = ceil(DifficultyConfig.INIT_MAX_DINO * difficulty).toInt()
        val spawnDelay = DifficultyConfig.INIT_SPAWN_DELAY / difficulty

        val maxWander  = (maxDino * DifficultyConfig.RATIO_WANDER).roundToInt()
        val maxFlee    = (maxDino * DifficultyConfig.RATIO_FLEE).roundToInt()
        val maxChase   = maxDino - maxWander - maxFlee

        return LevelDifficulty(level, difficulty, maxDino, spawnDelay,
                               maxWander, maxFlee, maxChase)
    }

    /**
     * Retourne la diff_locale courante à [elapsedSeconds] secondes dans le niveau.
     * Stateless : le caller fournit le temps écoulé.
     * Cap = baseDiff + LOCAL_DIFF_INCREMENT * MAX_LOCAL_INCREMENT
     */
    fun updateLocalDifficulty(baseDiff: Float, elapsedSeconds: Float): Float {
        val steps       = (elapsedSeconds / DifficultyConfig.LOCAL_DIFF_INTERVAL).toInt()
        val cappedSteps = minOf(steps, DifficultyConfig.MAX_LOCAL_INCREMENT)
        val cap         = baseDiff + DifficultyConfig.LOCAL_DIFF_INCREMENT * DifficultyConfig.MAX_LOCAL_INCREMENT
        return minOf(baseDiff + cappedSteps * DifficultyConfig.LOCAL_DIFF_INCREMENT, cap)
    }

    /**
     * Retourne (currentSpawnDelay, currentMaxDino) pour un [diffLocale] donné.
     * Utilisé pendant le niveau pour adapter le spawn à la difficulté locale.
     */
    fun getLiveValues(diffLocale: Float): Pair<Float, Int> {
        val delay   = DifficultyConfig.INIT_SPAWN_DELAY / diffLocale
        val maxDino = ceil(DifficultyConfig.INIT_MAX_DINO * diffLocale).toInt()
        return Pair(delay, maxDino)
    }
}

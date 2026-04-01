package fr.iutlens.mmi.demo.game

data class BossConfig(
    val hitCount: Int,
    val speed: Float,
    val spawnCount: Int,
    val spawnIntervalMs: Long
)

object BossDifficultyConfig {
    private const val BASE_HIT_COUNT = 6
    private const val HIT_COUNT_PER_BOSS = 3

    private const val BASE_SPEED = 5f
    private const val SPEED_PER_BOSS = 1.2f

    private const val BASE_SPAWN_COUNT = 1
    private const val SPAWN_COUNT_PER_BOSS = 1

    private const val BASE_SPAWN_INTERVAL_S = 6f
    private const val SPAWN_INTERVAL_STEP_S = 0.67f
    private const val MIN_SPAWN_INTERVAL_S = 2f

    /** levelIndex est 0-basé. Boss levels : 4, 9, 14, 19... (= niveaux 5, 10, 15, 20...) */
    fun isBossLevel(levelIndex: Int) = (levelIndex + 1) % 5 == 0

    fun getConfig(levelIndex: Int): BossConfig {
        val n = ((levelIndex + 1) / 5 - 1)/2
        return BossConfig(
            hitCount = BASE_HIT_COUNT + n * HIT_COUNT_PER_BOSS,
            speed = BASE_SPEED + n * SPEED_PER_BOSS,
            spawnCount = BASE_SPAWN_COUNT + n * SPAWN_COUNT_PER_BOSS,
            spawnIntervalMs = ((BASE_SPAWN_INTERVAL_S - n * SPAWN_INTERVAL_STEP_S)
                .coerceAtLeast(MIN_SPAWN_INTERVAL_S) * 1000f).toLong()
        )
    }
}

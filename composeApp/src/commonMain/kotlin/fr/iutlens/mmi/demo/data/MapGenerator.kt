package fr.iutlens.mmi.demo.data

import fr.iutlens.mmi.demo.game.DifficultyManager
import kotlin.random.Random

object MapGenerator {
    const val MAP_WIDTH = 30
    const val MAP_HEIGHT = 23
    const val MAP_CODE = ".abef^?#ghcd)%*"

    private val PLATFORM_ROW_TOPS = intArrayOf(4, 10, 16)

    private const val MIN_MARGIN = 2
    private const val MIN_SEG_W = 6
    private const val MIN_HOLE_W = 4
    private const val MAX_HOLE_W = 8
    private const val MIN_ZONE_W = 16  // 2×MIN_SEG_W + MIN_HOLE_W : permet 1 trou même au max
    private const val MAX_NAV = 2      // décalage max de zoneStart entre rangées consécutives

    fun generate(levelIndex: Int, seed: Long = levelIndex.toLong()): String {
        val difficulty = DifficultyManager.getLevelDifficulty(levelIndex + 1).difficulty
        val grid = Array(MAP_HEIGHT) { CharArray(MAP_WIDTH) { '.' } }

        for (x in 0 until MAP_WIDTH) {
            grid[MAP_HEIGHT - 2][x] = '*'
            grid[MAP_HEIGHT - 1][x] = '#'
        }

        var prevZoneStart: Int? = null
        for ((idx, topRow) in PLATFORM_ROW_TOPS.withIndex()) {
            val rowRng = Random(seed * 10L + idx)
            val (segments, zoneStart) = computeRow(difficulty, rowRng, prevZoneStart)
            for (range in segments) placePlatformSegment(grid, topRow, range)
            prevZoneStart = zoneStart
        }

        return grid.joinToString("\n") { it.concatToString() }
    }

    /**
     * Approche soustractive : zone pleine → trous percés.
     * La zone rétrécit avec la difficulté et flotte aléatoirement (contrainte de navigation).
     *
     * Niveau 1  : ..[==========================]..  0 trou, zone pleine
     * Niveau 6  : .....[================].....       1 trou, zone réduite
     * Niveau 11+: .......[======]  [======].....     1 trou, zone min
     */
    private fun computeRow(
        difficulty: Float,
        rng: Random,
        prevZoneStart: Int?
    ): Pair<List<IntRange>, Int> {
        val diffNorm = ((difficulty - 1f) / 2f).coerceIn(0f, 1f)
        val fullZoneW = MAP_WIDTH - 2 * MIN_MARGIN  // 26

        val holeCount = when {
            diffNorm < 0.10f -> 0
            diffNorm < 0.50f -> 1
            else -> rng.nextInt(1, 3)
        }

        // Zone rétrécit de fullZoneW (26) à MIN_ZONE_W (16) avec la difficulté
        val zoneWidth = makeEven(fullZoneW - ((fullZoneW - MIN_ZONE_W) * diffNorm).toInt())
            .coerceAtLeast(MIN_ZONE_W)

        // Centre de la zone, décalé aléatoirement dans l'espace disponible
        val baseCenterStart = makeEven((MAP_WIDTH - zoneWidth) / 2)
        val maxShift = makeEven(baseCenterStart - MIN_MARGIN)
        val halfSteps = maxShift / 2
        val shift = if (halfSteps > 0) (rng.nextInt(2 * halfSteps + 1) - halfSteps) * 2 else 0
        val maxZoneStart = MAP_WIDTH - MIN_MARGIN - zoneWidth
        val unconstrained = (baseCenterStart + shift).coerceIn(MIN_MARGIN, maxZoneStart)

        // Contrainte de navigation : zoneStart à ±MAX_NAV de la rangée précédente
        val zoneStart = if (prevZoneStart != null) {
            unconstrained.coerceIn(
                (prevZoneStart - MAX_NAV).coerceAtLeast(MIN_MARGIN),
                (prevZoneStart + MAX_NAV).coerceAtMost(maxZoneStart)
            )
        } else unconstrained

        val zoneEnd = zoneStart + zoneWidth

        // Largeur max des trous croît avec la difficulté
        val maxHoleW = makeEven((MIN_HOLE_W + (MAX_HOLE_W - MIN_HOLE_W) * diffNorm).toInt())
            .coerceAtLeast(MIN_HOLE_W)

        val segments = mutableListOf(zoneStart until zoneEnd)

        repeat(holeCount) {
            val punchable = segments.filter { it.count() >= 2 * MIN_SEG_W + MIN_HOLE_W }
            if (punchable.isEmpty()) return@repeat

            val seg = punchable[rng.nextInt(punchable.size)]

            val holeSteps = (maxHoleW - MIN_HOLE_W) / 2 + 1
            val holeW = MIN_HOLE_W + rng.nextInt(holeSteps) * 2

            val hsMin = seg.first + MIN_SEG_W
            val hsMax = seg.last + 1 - MIN_SEG_W - holeW
            if (hsMin > hsMax) return@repeat

            val steps = (hsMax - hsMin) / 2 + 1
            val hs = hsMin + rng.nextInt(steps) * 2
            val he = hs + holeW

            val idx = segments.indexOf(seg)
            segments.removeAt(idx)
            segments.add(idx, seg.first until hs)
            segments.add(idx + 1, he until seg.last + 1)
        }

        return Pair(segments, zoneStart)
    }

    private fun placePlatformSegment(grid: Array<CharArray>, topRow: Int, range: IntRange) {
        val start = range.first
        val end = range.last + 1

        grid[topRow][start] = 'e'
        grid[topRow][start + 1] = 'f'
        var x = start + 2
        while (x < end - 2) {
            grid[topRow][x] = '^'
            grid[topRow][x + 1] = '?'
            x += 2
        }
        grid[topRow][end - 2] = 'a'
        grid[topRow][end - 1] = 'b'

        grid[topRow + 1][start] = 'g'
        grid[topRow + 1][start + 1] = 'h'
        x = start + 2
        while (x < end - 2) {
            grid[topRow + 1][x] = '%'
            grid[topRow + 1][x + 1] = ')'
            x += 2
        }
        grid[topRow + 1][end - 2] = 'c'
        grid[topRow + 1][end - 1] = 'd'
    }

    private fun makeEven(n: Int): Int = if (n % 2 == 0) n else n - 1
}

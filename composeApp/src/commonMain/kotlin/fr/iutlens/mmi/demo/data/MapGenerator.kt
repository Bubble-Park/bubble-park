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

    fun generate(levelIndex: Int, seed: Long = levelIndex.toLong()): String {
        val difficulty = DifficultyManager.getLevelDifficulty(levelIndex + 1).difficulty
        val grid = Array(MAP_HEIGHT) { CharArray(MAP_WIDTH) { '.' } }

        for (x in 0 until MAP_WIDTH) {
            grid[MAP_HEIGHT - 2][x] = '*'
            grid[MAP_HEIGHT - 1][x] = '#'
        }

        for ((idx, topRow) in PLATFORM_ROW_TOPS.withIndex()) {
            val rowRng = Random(seed * 10L + idx)
            val segments = computeRow(difficulty, rowRng)
            for (range in segments) placePlatformSegment(grid, topRow, range)
        }

        return grid.joinToString("\n") { String(it) }
    }

    private fun computeRow(difficulty: Float, rng: Random): List<IntRange> {
        val diffNorm = ((difficulty - 1f) / 2f).coerceIn(0f, 1f)

        val holeCount = when {
            diffNorm < 0.10f -> 0
            diffNorm < 0.50f -> 1
            else -> rng.nextInt(1, 3)
        }

        val usableStart = MIN_MARGIN           // 2
        val usableEnd = MAP_WIDTH - MIN_MARGIN // 28

        val maxHoleW = makeEven((MIN_HOLE_W + (MAX_HOLE_W - MIN_HOLE_W) * diffNorm).toInt())
            .coerceAtLeast(MIN_HOLE_W)

        val segments = mutableListOf(usableStart until usableEnd)

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

        return segments
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

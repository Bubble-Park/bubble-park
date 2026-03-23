package fr.iutlens.mmi.demo.data

import fr.iutlens.mmi.demo.game.DifficultyManager
import kotlin.math.floor
import kotlin.random.Random

object MapGenerator {
    const val MAP_WIDTH = 30
    const val MAP_HEIGHT = 23
    const val MAP_CODE = ".abef^?#ghcd)%*"

    private val PLATFORM_ROW_TOPS = intArrayOf(4, 10, 16)

    private const val MIN_MARGIN = 2
    private const val MIN_GAP = 2
    private const val MIN_SEG_W = 6
    private const val MAX_GAP = 8

    fun generate(levelIndex: Int, seed: Long = levelIndex.toLong()): String {
        val difficulty = DifficultyManager.getLevelDifficulty(levelIndex + 1).difficulty
        val grid = Array(MAP_HEIGHT) { CharArray(MAP_WIDTH) { '.' } }

        for (x in 0 until MAP_WIDTH) {
            grid[MAP_HEIGHT - 2][x] = '*'
            grid[MAP_HEIGHT - 1][x] = '#'
        }

        var prevLeftEdge: Int? = null
        for ((idx, topRow) in PLATFORM_ROW_TOPS.withIndex()) {
            val rowRng = Random(seed * 10L + idx)
            val segments = computeRow(difficulty, rowRng, prevLeftEdge)
            for (range in segments) placePlatformSegment(grid, topRow, range)
            prevLeftEdge = segments.firstOrNull()?.first
        }

        return grid.joinToString("\n") { String(it) }
    }

    private fun computeRow(
        difficulty: Float,
        rng: Random,
        prevLeftEdge: Int?
    ): List<IntRange> {
        val diffNorm = ((difficulty - 1f) / 2f).coerceIn(0f, 1f)

        val maxSegs = when {
            diffNorm < 0.10f -> 1
            diffNorm < 0.50f -> 2
            else -> 3
        }
        val segCount = rng.nextInt(1, maxSegs + 1)

        // Budget total de tuiles de plateforme (décroît avec la difficulté)
        val usableWidth = MAP_WIDTH - 2 * MIN_MARGIN  // 26
        val maxCoverage = makeEven((usableWidth * 0.92f).toInt())         // ~24
        val minCoverage = segCount * MIN_SEG_W + (segCount - 1) * MIN_GAP
        val coverage = makeEven(maxCoverage - ((maxCoverage - minCoverage) * diffNorm).toInt())
            .coerceAtLeast(minCoverage)

        // Largeurs des segments : variation aléatoire autour de baseW
        val segWidths = generateSegWidths(segCount, coverage, diffNorm, rng)

        // Calcul de l'espace disponible pour les marges et gaps
        val minTotalGaps = (segCount - 1) * MIN_GAP
        val totalPlatform = segWidths.sum()
        val spareSpace = MAP_WIDTH - totalPlatform - minTotalGaps  // espace pour marges + extra gaps

        // leftMargin : aléatoire dans [MIN_MARGIN, spareSpace - MIN_MARGIN]
        val maxLeft = (spareSpace - MIN_MARGIN).coerceAtLeast(MIN_MARGIN)
        val rawLeft = rng.nextInt(MIN_MARGIN, maxLeft + 1)
        val baseLeft = makeEven(rawLeft)

        // Contrainte de navigation : bord gauche à ±2 de la rangée précédente
        val leftMargin = if (prevLeftEdge != null) {
            baseLeft.coerceIn(
                (prevLeftEdge - 2).coerceAtLeast(MIN_MARGIN),
                (prevLeftEdge + 2).coerceAtMost(maxLeft)
            )
        } else {
            baseLeft
        }

        // Gaps internes : MIN_GAP + variation paire aléatoire, capés à MAX_GAP
        // On vérifie que tout rentre dans MAP_WIDTH avant de placer
        val gaps = buildGaps(segCount - 1, spareSpace - leftMargin - MIN_MARGIN, rng)

        // Construction des IntRanges
        val ranges = mutableListOf<IntRange>()
        var x = leftMargin
        for ((i, w) in segWidths.withIndex()) {
            ranges.add(x until x + w)
            x += w
            if (i < segCount - 1) x += gaps.getOrElse(i) { MIN_GAP }
        }
        return ranges
    }

    /**
     * Génère [count] largeurs de segments dont la somme = [totalCoverage],
     * avec une variation aléatoire non-uniforme. Les segments plus larges sont
     * favorisés à basse difficulté.
     */
    private fun generateSegWidths(
        count: Int,
        totalCoverage: Int,
        diffNorm: Float,
        rng: Random
    ): List<Int> {
        if (count == 1) return listOf(totalCoverage)

        val widths = MutableList(count) { MIN_SEG_W }
        var remaining = totalCoverage - count * MIN_SEG_W  // extra paires à distribuer

        // Biais : à diffNorm faible on donne plus d'extra au premier segment (plateforme principale plus large)
        while (remaining >= 2) {
            // Choisir un segment de façon biaisée
            val idx = if (diffNorm < 0.3f && rng.nextFloat() < 0.6f) 0
                      else rng.nextInt(count)
            widths[idx] += 2
            remaining -= 2
        }
        return widths
    }

    /**
     * Génère [count] gaps (entre segments) dont la somme ≤ [availableSpace],
     * chaque gap dans [MIN_GAP, MAX_GAP], valeurs paires.
     */
    private fun buildGaps(count: Int, availableSpace: Int, rng: Random): List<Int> {
        if (count == 0) return emptyList()
        val gaps = MutableList(count) { MIN_GAP }
        var spare = (availableSpace - count * MIN_GAP).coerceAtLeast(0)

        for (i in 0 until count) {
            if (spare <= 0) break
            val extra = rng.nextInt(0, (spare / count + 1).coerceAtLeast(1)) * 2
            val add = extra.coerceAtMost(MAX_GAP - MIN_GAP).coerceAtMost(spare)
            val addEven = makeEven(add)
            gaps[i] += addEven
            spare -= addEven
        }
        return gaps
    }

    private fun placePlatformSegment(grid: Array<CharArray>, topRow: Int, range: IntRange) {
        val start = range.first
        val end = range.last + 1

        // Ligne top (solide : codes 1–6)
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

        // Ligne bottom (décor : codes 8–13)
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

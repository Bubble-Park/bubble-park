// DEBUG — À SUPPRIMER après analyse. Ne pas committer.
package fr.iutlens.mmi.demo.utils

import kotlin.math.abs

fun PlatformGraph.fleeDiagnostic(
    aiTile: Pair<Int, Int>,
    distPlayer: Map<Pair<Int, Int>, Int>,
    playerTile: Pair<Int, Int>,
    k: Float = 0.3f,
    topN: Int = 5
): String {
    val (costs, parents) = dijkstra(aiTile, playerTile)

    data class Candidate(
        val tile: Pair<Int, Int>,
        val score: Float,
        val distP: Int?,
        val distManhattan: Int,
        val cost: Float,
        val path: List<PathStep>
    )

    val candidates = costs.entries
        .filter { it.key != aiTile }
        .map { (tile, cost) ->
            val pd = distPlayer[tile]
            val manhattan = abs(playerTile.first - tile.first) + abs(playerTile.second - tile.second)
            Candidate(tile, manhattan.toFloat() - k * cost, pd, manhattan, cost, reconstructPath(aiTile, tile, parents))
    }
        .sortedByDescending { it.score }

    val best = candidates.firstOrNull()
    val sb = StringBuilder()
    sb.appendLine("=== FLEE DIAGNOSTIC ===")
    sb.appendLine("AI: $aiTile  |  Player: $playerTile  |  distPlayer[AI]: ${distPlayer[aiTile] ?: "unreachable"}")
    sb.appendLine()
    sb.appendLine("Top $topN (score = manhattan - $k x cost) :")
    candidates.take(topN).forEachIndexed { n, c ->
        val summary = c.path.groupBy { it.action }.entries
            .joinToString(" ") { (a, s) -> "${a.name}x${s.size}" }
        sb.appendLine("  #${n + 1} ${c.tile}  score=${c.score}  manhattan=${c.distManhattan}  bfs=${c.distP ?: "?"}  cost=${c.cost}  [$summary]")
    }
    sb.appendLine()
    sb.appendLine("Chemin choisi (${best?.path?.size ?: 0} etapes) :")
    sb.appendLine(
        "  " + (best?.path?.joinToString(" -> ") {
            "${it.action.name[0]}(${it.tile.first},${it.tile.second})"
        } ?: "AUCUN")
    )
    sb.appendLine()
    sb.append(fleeDiagnosticAsciiMap(aiTile, playerTile, best?.path ?: emptyList()))
    return sb.toString()
}

private fun PlatformGraph.fleeDiagnosticAsciiMap(
    aiTile: Pair<Int, Int>,
    playerTile: Pair<Int, Int>,
    path: List<PathStep>
): String {
    val tm = tileArea.tileMap
    val sizeX = tm.geometry.sizeX
    val sizeY = tm.geometry.sizeY
    val pathTiles = path.map { it.tile }.toSet()
    val target = path.lastOrNull()?.tile
    val sb = StringBuilder()
    sb.appendLine("ASCII (P=player A=ai T=cible *=chemin x=standable #=solid .=vide)")
    for (j in 0 until sizeY) {
        for (i in 0 until sizeX) {
            val pos = i to j
            sb.append(
                when {
                    pos == playerTile -> 'P'
                    pos == aiTile     -> 'A'
                    pos == target     -> 'T'
                    pos in pathTiles  -> '*'
                    isStandable(i, j) -> 'x'
                    (tm.get(i, j) ?: 0) != 0 -> '#'
                    else -> '.'
                }
            )
        }
        sb.appendLine()
    }
    return sb.toString()
}

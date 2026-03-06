package fr.iutlens.mmi.demo.utils

import fr.iutlens.mmi.demo.game.sprite.TiledArea
import kotlin.math.abs

enum class MoveAction { WALK, JUMP, FALL }

data class Edge(
    val target: Pair<Int, Int>,
    val action: MoveAction,
    val cost: Float
)

data class PathStep(
    val tile: Pair<Int, Int>,
    val action: MoveAction,
    val dirX: Float
)

class PathPlan(steps: List<PathStep>) {
    private val steps = steps.toMutableList()
    private var index = 0

    val current: PathStep? get() = steps.getOrNull(index)
    val isDone: Boolean get() = index >= steps.size

    fun advance() { index++ }

    /** Retourne l'index et le step du prochain JUMP dans le plan (après l'index courant), ou null. */
    fun peekNextJump(): Pair<Int, PathStep>? {
        for (i in (index + 1) until steps.size) {
            if (steps[i].action == MoveAction.JUMP) return i to steps[i]
        }
        return null
    }

    /** Saute directement à l'index donné (pour anticiper un JUMP). */
    fun skipTo(targetIndex: Int) { index = targetIndex }
}

class PlatformGraph(
    val tileArea: TiledArea,
    val jumpHeight: Int = 6
) {
    private val tileMap = tileArea.tileMap
    private val sizeX = tileMap.geometry.sizeX
    private val sizeY = tileMap.geometry.sizeY

    private val standable = mutableSetOf<Pair<Int, Int>>()
    private val forward = mutableMapOf<Pair<Int, Int>, List<Edge>>()
    private val reverse = mutableMapOf<Pair<Int, Int>, List<Edge>>()

    init { precompute() }

    fun isStandable(i: Int, j: Int): Boolean = (i to j) in standable

    fun forwardEdges(pos: Pair<Int, Int>): List<Edge> = forward[pos] ?: emptyList()
    fun reverseEdges(pos: Pair<Int, Int>): List<Edge> = reverse[pos] ?: emptyList()

    fun getAction(from: Pair<Int, Int>, to: Pair<Int, Int>): MoveAction? =
        forward[from]?.find { it.target == to }?.action

    fun reverseNeighborFn(): (Pair<Int, Int>, (Pair<Int, Int>) -> Unit) -> Unit =
        { pos, visit -> reverseEdges(pos).forEach { visit(it.target) } }

    fun forwardNeighborFn(): (Pair<Int, Int>, (Pair<Int, Int>) -> Unit) -> Unit =
        { pos, visit -> forwardEdges(pos).forEach { visit(it.target) } }

    /**
     * Dijkstra
     * @param start : position de départ.
     * @param avoidTile : tile à pénaliser.
     * @param avoidCost : surcoût appliqué à avoidTile.
     * @return (costs, parents) pour la reconstruction de chemin.
     */
    fun dijkstra(
        start: Pair<Int, Int>,
        avoidTile: Pair<Int, Int>? = null,
        avoidCost: Float = 20f
    ): Pair<Map<Pair<Int, Int>, Float>, Map<Pair<Int, Int>, Pair<Int, Int>>> {
        val dist = mutableMapOf<Pair<Int, Int>, Float>()
        val parent = mutableMapOf<Pair<Int, Int>, Pair<Int, Int>>()
        val visited = mutableSetOf<Pair<Int, Int>>()

        dist[start] = 0f

        while (true) {
            val current = dist.entries
                .filter { it.key !in visited }
                .minByOrNull { it.value }
                ?.key ?: break

            visited.add(current)

            for (edge in forwardEdges(current)) {
                if (edge.target in visited) continue
                val extraCost = if (edge.target == avoidTile) avoidCost else 0f
                val newDist = dist[current]!! + edge.cost + extraCost
                if (newDist < (dist[edge.target] ?: Float.MAX_VALUE)) {
                    dist[edge.target] = newDist
                    parent[edge.target] = current
                }
            }
        }

        return dist to parent
    }

    /**
     * Reconstruit le chemin de from à to via la map parents issue de dijkstra
     * @param from : position de départ.
     * @param to : position d'arrivée.
     * @param parents : map des parents.
     * @return une liste de PathStep
     */
    fun reconstructPath(
        from: Pair<Int, Int>,
        to: Pair<Int, Int>,
        parents: Map<Pair<Int, Int>, Pair<Int, Int>>
    ): List<PathStep> {
        val path = mutableListOf<PathStep>()
        var current = to

        while (current != from) {
            val prev = parents[current] ?: return emptyList()
            val action = getAction(prev, current) ?: MoveAction.WALK
            val dirX = when {
                current.first < prev.first -> -1f
                current.first > prev.first -> 1f
                else -> 0f
            }
            path.add(PathStep(current, action, dirX))
            current = prev
        }

        path.reverse()
        return path
    }

    /**
     * Chemin complet de `from` vers `to`, avec pénalité optionnelle sur `avoidTile`.
     */
    fun findPath(
        from: Pair<Int, Int>,
        to: Pair<Int, Int>,
        avoidTile: Pair<Int, Int>? = null
    ): List<PathStep> {
        val (_, parents) = dijkstra(from, avoidTile)
        return reconstructPath(from, to, parents)
    }

    /**
     * Calcule en un seul Dijkstra la meilleure destination de fuite ET le chemin pour y aller.
     * Score de chaque tile = distPlayer[tile] - k * cost_from_me
     * playerTile est pénalisée pour éviter les chemins qui la traversent.
     */
    fun findFleePathTo(
        from: Pair<Int, Int>,
        playerTile: Pair<Int, Int>,
        k: Float = 0.3f
    ): List<PathStep> {
        val (costs, parents) = dijkstra(from, playerTile)

        var bestTile: Pair<Int, Int>? = null
        var bestScore = Float.NEGATIVE_INFINITY

        for ((tile, cost) in costs) {
            if (tile == from) continue
            val playerDist = (abs(playerTile.first - tile.first) + abs(playerTile.second - tile.second)).toFloat()
            val score = playerDist - k * cost
            if (score > bestScore) {
                bestScore = score
                bestTile = tile
            }
        }

        return if (bestTile != null) reconstructPath(from, bestTile, parents) else emptyList()
    }

    private fun precompute() {
        findStandableTiles()
        computeForwardEdges()
        computeReverseEdges()
    }

    private fun isTileSolid(i: Int, j: Int): Boolean {
        if (i !in 0 until sizeX || j !in 0 until sizeY) return false
        val code = tileMap.get(i, j) ?: 0
        return code in 1..3
    }

    private fun isTileAir(i: Int, j: Int): Boolean {
        if (i !in 0 until sizeX) return false
        if (j < 0) return true
        if (j >= sizeY) return false
        val code = tileMap.get(i, j) ?: 0
        return code == 0
    }

    private fun isTileBlocking(i: Int, j: Int): Boolean {
        if (i !in 0 until sizeX) return true
        if (j < 0) return false
        if (j >= sizeY) return true
        val code = tileMap.get(i, j) ?: 0
        return code != 0
    }

    private fun findStandableTiles() {
        for (i in 0 until sizeX) {
            for (j in 0 until sizeY) {
                if (isTileAir(i, j) && (j + 1 >= sizeY || isTileSolid(i, j + 1))) {
                    standable.add(i to j)
                }
            }
        }
    }

    private fun computeForwardEdges() {
        for (pos in standable) {
            val (i, j) = pos
            val edges = mutableListOf<Edge>()

            // WALK : gauche/droite — le plus coûteux, déplacement horizontal lent
            if (isStandable(i - 1, j)) edges.add(Edge(i - 1 to j, MoveAction.WALK, 2.0f))
            if (isStandable(i + 1, j)) edges.add(Edge(i + 1 to j, MoveAction.WALK, 2.0f))

            // FALL : marcher dans le vide — très avantageux, chute rapide
            for (dir in listOf(-1, 1)) {
                val ni = i + dir
                if (ni !in 0 until sizeX) continue
                if (isStandable(ni, j)) continue
                if (!isTileAir(ni, j)) continue

                val landing = findLanding(ni, j + 1)
                if (landing != null) {
                    val dy = (landing - j).coerceAtLeast(1).toFloat()
                    edges.add(Edge(ni to landing, MoveAction.FALL, dy * 0.3f))
                }
            }

            // JUMP : vers le haut — la physique utilise checkPlatform=false en montée,
            // donc les plateformes solides (codes 1-3) ne bloquent pas la trajectoire ascendante.
            // Seuls les "vrais murs" (codes hors 0-3) bloqueraient, inexistants dans la map actuelle.
            for (dy in 1..jumpHeight) {
                val tj = j - dy
                if (!isStandable(i, tj)) continue
                edges.add(Edge(i to tj, MoveAction.JUMP, dy * 0.5f))
            }

            forward[pos] = edges
        }
    }

    private fun findLanding(col: Int, startRow: Int): Int? {
        for (j in startRow until sizeY) {
            if (isStandable(col, j)) return j
            if (isTileBlocking(col, j)) return null
        }
        return null
    }

    private fun computeReverseEdges() {
        val rev = mutableMapOf<Pair<Int, Int>, MutableList<Edge>>()
        for ((from, edges) in forward) {
            for (edge in edges) {
                rev.getOrPut(edge.target) { mutableListOf() }
                    .add(Edge(from, edge.action, edge.cost))
            }
        }
        reverse.putAll(rev)
    }
}

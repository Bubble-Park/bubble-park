package fr.iutlens.mmi.demo.utils

import fr.iutlens.mmi.demo.game.sprite.TiledArea

enum class MoveAction { WALK, JUMP, FALL }

data class Edge(val target: Pair<Int, Int>, val action: MoveAction)

class PlatformGraph(
    val tileArea: TiledArea,
    val jumpHeight: Int = 6,
    val horizontalReach: Int = 4
) {
    private val tileMap = tileArea.tileMap
    private val sizeX = tileMap.geometry.sizeX
    private val sizeY = tileMap.geometry.sizeY

    // Tableau des cases standable
    private val standable = mutableSetOf<Pair<Int, Int>>()
    private val forward = mutableMapOf<Pair<Int, Int>, List<Edge>>()
    private val reverse = mutableMapOf<Pair<Int, Int>, List<Edge>>()

    init { precompute() }

    fun isStandable(i: Int, j: Int): Boolean = (i to j) in standable

    fun forwardEdges(pos: Pair<Int, Int>): List<Edge> = forward[pos] ?: emptyList()
    fun reverseEdges(pos: Pair<Int, Int>): List<Edge> = reverse[pos] ?: emptyList()

    /** Retourne l'action à effectuer pour aller de from à to */
    fun getAction(from: Pair<Int, Int>, to: Pair<Int, Int>): MoveAction? =
        forward[from]?.find { it.target == to }?.action

    /** Fonction voisinage inversée */
    fun reverseNeighborFn(): (Pair<Int, Int>, (Pair<Int, Int>) -> Unit) -> Unit =
        { pos, visit -> reverseEdges(pos).forEach { visit(it.target) } }

    /** Fonction voisinage directe */
    fun forwardNeighborFn(): (Pair<Int, Int>, (Pair<Int, Int>) -> Unit) -> Unit =
        { pos, visit -> forwardEdges(pos).forEach { visit(it.target) } }

    /**
     * Charge les prérequis pour les calculs initiaux
     */
    private fun precompute() {
        findStandableTiles()
        computeForwardEdges()
        computeReverseEdges()
    }

    /**
     * Vérifie si la tile à la position (i, j) est solide
     * @param i position sur l'axe X
     * @param j position sur l'axe Y
     * @return true si la tile est solide
     */
    private fun isTileSolid(i: Int, j: Int): Boolean {
        if (i !in 0 until sizeX || j !in 0 until sizeY) return false
        val code = tileMap.get(i, j) ?: 0
        return code in 1..3
    }

    /**
     * Vérifie si la tile à la position (i, j) est vide
     * @param i position sur l'axe X
     * @param j position sur l'axe Y
     * @return true si la tile est vide
     */
    private fun isTileAir(i: Int, j: Int): Boolean {
        if (i !in 0 until sizeX) return false
        if (j < 0) return true
        if (j >= sizeY) return false
        val code = tileMap.get(i, j) ?: 0
        return code == 0
    }

    /**
     * Vérifie si la tile à la position (i, j) est un obstacle
     * @param i position sur l'axe X
     * @param j position sur l'axe Y
     * @return true si la tile est un obstacle
     */
    private fun isTileWall(i: Int, j: Int): Boolean {
        if (i !in 0 until sizeX || j < 0 || j >= sizeY) return true
        val code = tileMap.get(i, j) ?: 0
        return code > 3
    }

    /**
     * Vérifie si la tile est une limite du niveau
     * @param i position sur l'axe X
     * @param j position sur l'axe Y
     * @return true si la tile est une limite du niveau
     */
    private fun isTileBlocking(i: Int, j: Int): Boolean {
        if (i !in 0 until sizeX) return true
        if (j < 0) return false
        if (j >= sizeY) return true
        val code = tileMap.get(i, j) ?: 0
        return code != 0
    }

    /** Trouve les tiles standables
     * Une tile standable = tile vide + tile solide juste en dessous (-1)
     * Rempli le tableau standable
     */
    private fun findStandableTiles() {
        for (i in 0 until sizeX) {
            for (j in 0 until sizeY) {
                if (isTileAir(i, j) && (j + 1 >= sizeY || isTileSolid(i, j + 1))) {
                    standable.add(i to j)
                }
            }
        }
    }

    /**
     * Construit le tableau des forward edges
     */
    private fun computeForwardEdges() {
        for (pos in standable) {
            val (i, j) = pos
            val edges = mutableListOf<Edge>()

            // WALK : gauche/droite
            if (isStandable(i - 1, j)) edges.add(Edge(i - 1 to j, MoveAction.WALK))
            if (isStandable(i + 1, j)) edges.add(Edge(i + 1 to j, MoveAction.WALK))

            // FALL : marcher dans le vide et tomber
            for (dir in listOf(-1, 1)) {
                val ni = i + dir
                if (ni !in 0 until sizeX) continue
                if (isStandable(ni, j)) continue // WALK
                if (!isTileAir(ni, j)) continue

                val landing = findLanding(ni, j + 1)
                if (landing != null) {
                    edges.add(Edge(ni to landing, MoveAction.FALL))
                }
            }

            // JUMP : toutes les tiles standable dans l'enveloppe de saut
            for (dy in -jumpHeight..jumpHeight) {
                val maxDx = if (dy <= 0) {
                    horizontalReach
                } else {
                    horizontalReach + dy / 2
                }

                for (dx in -maxDx..maxDx) {
                    if (dx == 0 && dy == 0) continue
                    if (dx in -1..1 && dy == 0) continue  // WALK
                    val ti = i + dx
                    val tj = j + dy
                    if (!isStandable(ti, tj)) continue
                    if (!isJumpPathClear(i, j, ti, tj)) continue

                    edges.add(Edge(ti to tj, MoveAction.JUMP))
                }
            }

            forward[pos] = edges
        }
    }

    /**
     * Trouve la première tile standable en tombant depuis (col, startRow)
     * @param col position sur l'axe X
     * @param startRow position sur l'axe Y
     * @return la première tile standable en tombant depuis (col, startRow)
     */
    private fun findLanding(col: Int, startRow: Int): Int? {
        for (j in startRow until sizeY) {
            if (isStandable(col, j)) return j
            if (isTileBlocking(col, j)) return null
        }
        return null
    }

    /**
     * Vérifie que l'arc de saut n'est pas bloqué par un mur ou une plateforme
     * @param fromI position X de départ
     * @param fromJ position Y de départ
     * @param toI position X d'arrivée
     * @param toJ position Y d'arrivée
     * @return true si l'arc de saut n'est pas bloqué
     */
    private fun isJumpPathClear(fromI: Int, fromJ: Int, toI: Int, toJ: Int): Boolean {
        val topJ = minOf(fromJ, toJ) - 1
        val minI = minOf(fromI, toI)
        val maxI = maxOf(fromI, toI)

        // Vérifier la colonne de départ vers le haut
        for (j in topJ until fromJ) {
            if (j >= 0 && isTileBlocking(fromI, j)) return false
        }
        // Vérifier la colonne d'arrivée vers le haut
        for (j in topJ until toJ) {
            if (j >= 0 && isTileBlocking(toI, j)) return false
        }
        // Vérifier les colonnes intermédiaires
        for (i in minI..maxI) {
            if (topJ >= 0 && isTileBlocking(i, topJ)) return false
        }
        return true
    }

    /** Construit le graphe inversé depuis le graphe direct */
    private fun computeReverseEdges() {
        val rev = mutableMapOf<Pair<Int, Int>, MutableList<Edge>>()
        for ((from, edges) in forward) {
            for (edge in edges) {
                rev.getOrPut(edge.target) { mutableListOf() }
                    .add(Edge(from, edge.action))
            }
        }
        reverse.putAll(rev)
    }
}

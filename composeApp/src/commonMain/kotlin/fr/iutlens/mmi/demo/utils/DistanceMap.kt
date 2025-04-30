package fr.iutlens.mmi.demo.utils

import fr.iutlens.mmi.demo.game.sprite.Sprite
import fr.iutlens.mmi.demo.game.sprite.TileMap
import fr.iutlens.mmi.demo.game.sprite.TiledArea
import fr.iutlens.mmi.demo.game.sprite.contains
import kotlin.math.floor

/**
 * Distance map
 * table des distances à la cible pour chaque position atteignable
 * @property map
 * @property target
 * @property neighbor
 * @constructor Create empty Distance map
 */
class DistanceMap(val map : TiledArea, var target : Sprite,
                  val neighbor : (pos : Pair<Int,Int>, visit : (Pair<Int,Int>)-> Unit)-> Unit) {
    private val distance = mutableMapOf<Pair<Int,Int>,Int>()

    // Accès aux distance

    operator fun get(pos : Pair<Int,Int>) : Int? = distance[pos]
    operator fun get(i : Int, j : Int) : Int? = distance[i to j]

    operator fun contains(pos : Pair<Int,Int>) = pos in distance

    /**
     * Next
     * Trouve parmis les cases voisines celle qui est le plus proche de la cible et la retourne
     * @param pos
     * @return null si cible pas atteignable ou déjà atteinte
     */
    fun next(pos : Pair<Int,Int>) : Pair<Int,Int>? {
        val list = mutableListOf<Pair<Pair<Int,Int>,Int>>()
        neighbor(pos){n ->
            get(n)?.let {  list.add(n to it) } }
        val best =   list.minByOrNull { it.second } ?: return null
        val current = get(pos)
        if (current != null && best.second>current) return null
        return best.first
    }

    private fun getTargetIJ() : Pair<Int,Int>{
        val x = target.boundingBox.center.x
        val y = target.boundingBox.center.y
        val i = floor( x/map.w).toInt()
        val j = floor(( y/map.h)).toInt()
        return i to j
    }

    /**
     * Update
     * Recalcule la table des distances
     */
    fun update(){
        distance.clear()
        val start = getTargetIJ()
        var current = mutableListOf(start)
        var d = 0
        distance[start] = d
        while (current.isNotEmpty()){
            d++
            val next = mutableListOf<Pair<Int,Int>>()
            for(pos in current){
                neighbor(pos){ n ->
                    if (n !in distance){
                        distance[n] = d
                        next.add(n)
                    }
                }
            }
            current = next
        }
    }

    init {
        update()
    }
}

enum class Direction(val vec : Pair<Int,Int>){
    N(0 to -1),
    S(0 to 1),
    E(1 to 0),
    W(-1 to 0)
}

/**
 * Neighbor
 * retourne une fonction qui parcours les voisins valides (par défaut : tous)
 * @param valid
 * @receiver
 */
fun TileMap.neighbor(valid : TileMap.(Int,Int) -> Boolean = { _,_ ->true}) =
{pos : Pair<Int,Int>, visit : (Pair<Int,Int>)-> Unit  ->
    for(dir in Direction.entries){
        val i = pos.first + dir.vec.first
        val j = pos.second +  dir.vec.second
        if ((i to j) in this.geometry && this.valid(i,j)){
            visit(i to j)
        }
    }
}

/**
 * Distance map
 * Construit la table des distance, avec comme paramètre la cible et une fonction indiquant
 * si une case est valide
 * @param target
 * @param valid
 * @receiver
 */
fun TiledArea.distanceMap(target : Sprite, valid : TileMap.(Int,Int) -> Boolean) =
    DistanceMap(this, target, this.tileMap.neighbor(valid))

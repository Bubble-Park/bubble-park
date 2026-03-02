package fr.iutlens.mmi.demo.utils

import fr.iutlens.mmi.demo.game.sprite.Sprite
import fr.iutlens.mmi.demo.game.sprite.TileMap
import fr.iutlens.mmi.demo.game.sprite.TiledArea
import fr.iutlens.mmi.demo.game.sprite.contains
import kotlin.math.floor

/**
 * distance map
 * table des distances à la cible pour chaque position atteignable
 */
class DistanceMap(val map : TiledArea, var target : Sprite,
                  val neighbor : (pos : Pair<Int,Int>, visit : (Pair<Int,Int>)-> Unit)-> Unit) {
    private val distance = mutableMapOf<Pair<Int,Int>,Int>()
    private var lastTargetPos: Pair<Int, Int>? = null

    operator fun get(pos : Pair<Int,Int>) : Int? = distance[pos]
    operator fun get(i : Int, j : Int) : Int? = distance[i to j]

    operator fun contains(pos : Pair<Int,Int>) = pos in distance

    /**
     * next
     * trouve parmi les cases voisines celle qui est le plus proche de la cible et la retourne
     */
    fun next(pos : Pair<Int,Int>) : Pair<Int,Int>? {
        var bestPos: Pair<Int, Int>? = null
        var bestDist = Int.MAX_VALUE
        
        neighbor(pos) { n ->
            val d = get(n)
            if (d != null && d < bestDist) {
                bestDist = d
                bestPos = n
            }
        }
        
        if (bestPos == null) return null
        
        val currentDist = get(pos)
        if (currentDist != null && bestDist > currentDist) return null
        
        return bestPos
    }
    
    /**
     * nextFlee
     * trouve parmi les cases voisines celle qui est le plus loin de la cible et la retourne
     */
    fun nextFlee(pos : Pair<Int,Int>) : Pair<Int,Int>? {
        var bestPos: Pair<Int, Int>? = null
        var bestDist = -1
        
        neighbor(pos) { n ->
            val d = get(n)
            if (d != null && d > bestDist) {
                bestDist = d
                bestPos = n
            }
        }
        
        if (bestPos == null) return null
        
        val currentDist = get(pos)
        if (currentDist != null && bestDist < currentDist) return null
        
        return bestPos
    }

    private fun getTargetIJ() : Pair<Int,Int>{
        val x = target.boundingBox.center.x
        val y = target.boundingBox.bottom - 1f
        val i = floor( x/map.w).toInt()
        val j = floor( y/map.h).toInt()
        return i to j
    }

    /**
     * update
     * recalcule la table des distances
     */
    fun update(force: Boolean = false){
        val start = getTargetIJ()
        
        if (!force && start == lastTargetPos) {
            return
        }
        lastTargetPos = start

        distance.clear()
        
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
        update(force = true)
    }
}

enum class Direction(val vec : Pair<Int,Int>){
    N(0 to -1),
    S(0 to 1),
    E(1 to 0),
    W(-1 to 0)
}

/**
 * neighbor
 * retourne une fonction qui parcours les voisins valides
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
 * distance map
 * construit la table des distance
 */
fun TiledArea.distanceMap(target : Sprite, valid : TileMap.(Int,Int) -> Boolean) =
    DistanceMap(this, target, this.tileMap.neighbor(valid))

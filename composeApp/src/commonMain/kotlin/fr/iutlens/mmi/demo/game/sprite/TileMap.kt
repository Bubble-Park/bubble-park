package fr.iutlens.mmi.demo.game.sprite

import kotlin.math.floor
import kotlin.math.max

interface TileGeometry {
    fun getIndex(x : Int, y: Int) : Int? //tile corner at x,y
    fun getCoord(ndx : Int) : Pair<Int,Int> // tile corner

    fun getIndex(x : Float, y: Float) : Int? // point at x,y
    val size : Int

    val sizeX : Int // starts at 0,0
    val sizeY : Int // size in map unit. for rectangular tiles, it's the tiles number in each direction

    val tileSizeX : Int // tile size in map unit
    val tileSizeY : Int

    val xMax : Int
    val yMax : Int
}

fun TileGeometry.forEachTile(action : (ndx: Int, x: Int, y: Int) -> Unit) {
    for(ndx in 0..<size)  getCoord(ndx).apply { action(ndx,first,second) }
}

operator fun TileGeometry.contains(coord: Pair<Int, Int>) = getIndex(coord.first,coord.second) != null

open class RectangleGeometry(override  val sizeX : Int, override val sizeY: Int) : TileGeometry {
    override fun getIndex(x: Int, y: Int) = if (x !in 0..<sizeX ||y  !in 0..<sizeY ) null else x+y*sizeX
    override fun getIndex(x: Float, y: Float): Int? = getIndex(floor(x).toInt(), floor(y).toInt())
    override fun getCoord(ndx: Int): Pair<Int, Int> = ndx.mod(sizeX) to (ndx/sizeX)
    override val size = sizeY*sizeX
    override val tileSizeX = 1
    override val tileSizeY = 1
    override val xMax = sizeX
    override val yMax = sizeY
}

/***
 *    _   _   _
 *   /0\_/2\_/4\
 *   \0/1\_/3\_/
 *   / \_/ \_/ \
 *   \1/ \_/ \_/
 *   / \_/ \_/ \
 *   \2/ \_/ \_/
 *   / \_/ \_/ \
 *   \3/ \_/ \_/
 *   / \_/ \_/ \
 *   \4/ \_/ \_/
 *   / \_/ \_/ \
 *   \5/ \_/ \_/
 *
 *   /\/\
 *   \/\/
 */


open class HexagonGeometry(override val sizeX : Int, override val sizeY: Int) : TileGeometry {
    override val size = sizeY*sizeX
    override val xMax = sizeX*2+1
    override val yMax = 3*sizeY+1

    override fun getIndex(x: Int, y: Int) : Int? {
        val i = x/2
        if (i !in 0..<sizeX) return null
        val j = (y - (i%2)*3)/6
        if (j !in 0..<sizeY) return null
        return x+y*sizeX
    }

    override fun getIndex(x: Float, y: Float): Int? {
        val xt = x * tileSizeX
        var i = floor(xt).toInt()
        if (i !in 0..<xMax) return null

        val yt = y * tileSizeY
        var j = floor(yt).toInt()


        if (j%3 == 0){
            if ((j/3)%2 == i%2){
                if (xt+yt-i-j > 1) ++j else --j
            } else {
                if (xt-i < yt-j) ++j else --j
            }
        }
        if (j !in 0..<yMax) return null


        val x  = (i-((j/3)%2)) /2
        val y = (j+2)/3-1

        return if (x !in 0..<sizeX ||y  !in 0..<sizeY ) null else x+y*sizeX
    }

    override fun getCoord(ndx: Int) : Pair<Int,Int> {
        val i =  ndx.mod(sizeX)
        val j = (ndx/sizeX)
        val x =  i*2 +j%2
        val y = j*3

        return x to y
    }
    override val tileSizeX = 2
    override val tileSizeY = 4
}

open class TileMap(val geometry: TileGeometry, val data : Array<Int>) : TileGeometry by geometry {
    operator fun get(x: Int, y: Int): Int? = geometry.getIndex(x,y)?.let { data[it]  }
    operator fun get(x: Float, y: Float): Int? = geometry.getIndex(x,y)?.let { data[it]  }

    operator fun set(x: Int, y: Int, value:Int) {geometry.getIndex(x,y)?.let { data[it] = value }}
    operator fun set(x: Float, y: Float, value:Int) {geometry.getIndex(x,y)?.let { data[it] = value }}

    fun foreach(action : (x : Int, y: Int, value : Int)-> Unit){
        geometry.forEachTile { ndx, x, y -> action(x,y,data[ndx]) }
    }
}

/**
 * Construit une TileMap en découpant une chaîne en lignes.
 * La valeur de chaque case est la position du caractère correspondant dans la
 * chaîne fournie
 *
 * @param code liste des caractères utilisés pour coder la TileMap
 */
fun String.toTileMap(code: String) : TileMap {
    val dataChar = this.split('\n').filter { it.isNotEmpty() }
    val lines = dataChar.size
    val columns = dataChar[0].length
    val geometry = RectangleGeometry(columns,lines)
    val data = Array(geometry.size){
        val (x,y) = geometry.getCoord(it)
        return@Array code.indexOf(dataChar[y][x])
    }

    return TileMap(geometry,data)
}

fun String.toHexTileMap(code : String) : TileMap {
    val tileMap = toTileMap(code)
    println(""+tileMap.sizeX +'x'+ tileMap.sizeY)
    return TileMap(HexagonGeometry(tileMap.sizeX,tileMap.sizeY),tileMap.data)
}

fun RectangleGeometry.transpose() = object : RectangleGeometry(sizeY,sizeX) {
    override fun getCoord(ndx: Int) = this@transpose.getCoord(ndx).apply { second to first }
    override fun getIndex(x: Int, y: Int) = this@transpose.getIndex(y,x)
}

/**
 * Transpose inverse les lignes et les colonnes de la carte
 *
 */
fun TileMap.transpose() = if (geometry is RectangleGeometry) TileMap(geometry.transpose(),data) else throw IllegalArgumentException("Can only transpose rectangle geometry")


fun TileMap.compose(dx: Int, dy: Int, other: TileMap, default: Int = 0) : TileMap {
    val newX = max(other.geometry.sizeX+dx,geometry.sizeX)
    val newY = max(other.geometry.sizeY+dy,geometry.sizeY)
    val result = if ( geometry.sizeX==newX && geometry.sizeY== newY) this else {
        val newGeometry = RectangleGeometry(newX,newY)
        val data = Array<Int>(newGeometry.size){
            val coord = newGeometry.getCoord(it)
            this[coord.first,coord.second] ?: default
        }
        TileMap(newGeometry,data)
    }
    for (i in 0 ..< other.geometry.sizeX)
        for(j in 0 ..< other.geometry.sizeY)
            result[i+dx,j+dy] = other[i,j] ?: default
    return result
}

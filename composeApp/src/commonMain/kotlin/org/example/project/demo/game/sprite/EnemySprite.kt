package fr.iutlens.mmi.demo.game.sprite

import fr.iutlens.mmi.demo.utils.DistanceMap
import fr.iutlens.mmi.demo.utils.SpriteSheet
import org.jetbrains.compose.resources.DrawableResource
import kotlin.math.floor


class EnemySprite( res: DrawableResource,
                  x : Float,
                  y : Float,
                  val distanceMap: DistanceMap,
                  var speed: Float =0.1f
) : BasicSprite(res,x,y) {

    private var target : Pair<Int,Int>? = null
    var dx = 0f
    var dy = 0f
    var progress = 0f


    override fun update() {
        if (target == null){ // Si pas de cible atteignable, on en cherche une
            val i = floor( x/distanceMap.map.w).toInt()
            val j = floor( y/distanceMap.map.h).toInt()
            target = distanceMap.next(i to j)?.also { // calcule la direction vers la prochaine case
                dx = (it.first-i)*distanceMap.map.w*speed
                dy = (it.second-j)*distanceMap.map.h*speed
            }
        }
        if (target != null){ // on se déplace
            x += dx
            y += dy
            progress += speed
            if (progress>=1f) { // si on a atteint la prochaine case, il faudra recalculer la direction
                progress = 0f
                target = null
            }
        }
    }
}
package fr.iutlens.mmi.demo.components.bonus

import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.player_heart
import fr.iutlens.mmi.demo.components.Player

class LifeBonus(x: Float, y: Float, private val player: Player) :
    Bonus(Res.drawable.player_heart, x, y, scale = 0.6f) {

    override fun onCollect() = player.heal()
}

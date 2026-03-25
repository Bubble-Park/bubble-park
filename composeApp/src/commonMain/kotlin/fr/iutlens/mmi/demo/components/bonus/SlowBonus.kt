package fr.iutlens.mmi.demo.components.bonus

import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.slow_bonus
import fr.iutlens.mmi.demo.game.SlowEffect

class SlowBonus(x: Float, y: Float) : Bonus(Res.drawable.slow_bonus, x, y, scale = 0.25f) {

    override fun onCollect() = SlowEffect.activate()
}

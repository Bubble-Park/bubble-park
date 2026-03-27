package fr.iutlens.mmi.demo.components.bonus

import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.fastammo_bonus
import fr.iutlens.mmi.demo.game.FastAmmoEffect

class FastAmmoBonus(x: Float, y: Float) : Bonus(Res.drawable.fastammo_bonus, x, y, scale = 0.25f) {

    override fun onCollect() = FastAmmoEffect.activate()
}

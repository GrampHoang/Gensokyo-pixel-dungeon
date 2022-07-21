/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2022 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Random;

public class ReisenHand extends MeleeWeapon {

	{
		image = ItemSpriteSheet.GLOVES;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1.3f;

		tier = 1;
		DLY = 0.4f; //2.5x speed
		
		bones = false;
	}

	@Override
	public int max(int lvl) {
		return  Math.round((tier+2)) +
				lvl*Math.round(0.5f*(tier+1));
	}

    @Override
	public int proc(Char attacker, Char defender, int damage) {
        if (true){
        // if (Random.Int(0, (this.level() + 5)) < (this.level()+1)){
			Ballistica trajectory = new Ballistica(attacker.pos, defender.pos, Ballistica.STOP_TARGET);
			//trim it to just be the part that goes past them
			trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size() - 1), Ballistica.PROJECTILE);
			//knock them back along that ballistica
			WandOfBlastWave.throwChar(defender, trajectory, 1, false, true, this.getClass());
            Buff.affect(defender, Paralysis.class, 0.4f);
		}

		return super.proc(attacker, defender, damage);
	}

    public String statsInfo(){
        int chance = Math.round((buffedLvl() + 1)*100/(buffedLvl() + 5));
		return Messages.get(this, "stats_desc", chance) + "_%_.";
	}
}

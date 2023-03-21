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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class YoumuBlade3 extends YoumuBlade2{
	{
		image = ItemSpriteSheet.GHOSTBLADE;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 2f;
		ACC = 999;
		tier = 4;
	}
	
	@Override
	public String status() {
		return null;
	}

	@Override
	public String evolve_desc() {
		return "";
	}

	@Override
	public void kill(KindOfWeapon blade){}

	@Override
	public int max(int lvl) {
		return  Math.round(3f*(tier+1)) +			//base 18, from 25
				lvl*(tier);						//4 per level, from 5
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		//Check damage augment then add STR
        int real_damage = augment.damageFactor(Random.NormalIntRange( min(), max()));
		if (attacker instanceof Hero){
            int exStr = Dungeon.hero.STR() - STRReq();
            if (exStr > 0) {
                real_damage += Random.IntRange(0, exStr);
            }
        }
		int dmg = defender.drRoll();
		dmg += defender.drRoll();
		dmg += defender.drRoll();
		dmg += defender.drRoll();
		dmg += defender.drRoll();
		dmg = (dmg + defender.drRoll())/3;
		//average of 6 drRoll*2
        return super.proc(attacker, defender, real_damage+dmg);
	}
}

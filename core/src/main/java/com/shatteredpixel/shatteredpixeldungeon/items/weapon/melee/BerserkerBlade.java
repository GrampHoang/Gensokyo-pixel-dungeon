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

import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class BerserkerBlade extends MeleeWeapon {

	{
		image = ItemSpriteSheet.BERSERKERBLADE;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1f;

		tier = 2;
		DLY = 1f;
    }

	@Override
	public int max(int lvl) {
		return  Math.round(5f*(tier+1)) + //15 base
				lvl*Math.round(1f*(tier+1)); // no change scale
	}

    @Override
	public int damageRoll(Char owner) {
        int damage1 = augment.damageFactor(Random.NormalIntRange( min(), max()));
        float damage = damage1 * (5/4 - (owner.HP/owner.HT)/2);
        if (owner instanceof Hero){
            int exStr = Dungeon.hero.STR() - STRReq();
            if (exStr > 0) {
                damage += Random.IntRange(0, exStr);
            }
        }
        return Math.round(damage);
	}
}

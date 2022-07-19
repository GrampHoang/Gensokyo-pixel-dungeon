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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Ofuda_handheld extends MissileWeapon {

	{
		image = ItemSpriteSheet.OFUDA;
		hitSound = Assets.Sounds.CHARGEUP;
		hitSoundPitch = 0.9f;
		
		bones = false;
		
		tier = 1;
		baseUses = 1;
		sticky = false;
	}
	
	@Override
	public int min(int lvl) {
		return 4;
    }
	
	@Override
	public int max(int lvl) {
		return 6;
	}
	
    @Override
	public int min() {
		return 4;
    }
	
	@Override
	public int max() {
		return 6;
	}

    @Override
	public int value() {
		return 0;
	}

    @Override
	protected void onThrow( int cell ) {
		Char enemy = Actor.findChar( cell );
		if (enemy == null || enemy == curUser) {
				parent = null;
				super.onThrow( cell );
		} else {
			if (!curUser.shoot( enemy, this )) {
				decrementDurability();
				rangedMiss( cell );
			} else {
				decrementDurability();
				rangedHit( enemy, cell );

			}
		}
	}
}

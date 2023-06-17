/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2022 Evan Debenham
 *
 * Gensokyo Pixel Dungeon
 * Copyright (C) 2022-2023 GrampHoang
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

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Crab;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Slime;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Goo;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;


public class WoodStick extends MeleeWeapon {

	{
		image = ItemSpriteSheet.WOOD_STICK;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1f;

		tier = 2;
		DLY = 1f;
    }

	@Override
	public int max(int lvl) {
		return  Math.round(4f*(tier+1)) + //12 base
				lvl*Math.round(1f*(tier+1)); // no change scale
	}

    @Override
	public int proc(Char attacker, Char defender, int damage) {
        if (defender.properties().contains(Char.Property.FAIRY) || defender instanceof Crab || defender instanceof Slime || defender instanceof Goo){
            damage += 5;
        }
        return super.proc(attacker, defender, damage);
	}
}

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
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class GhostAnchor extends MeleeWeapon {

	{
		image = ItemSpriteSheet.ANCHOR;
		hitSound = Assets.Sounds.HIT_STRONG;
		hitSoundPitch = 0.5f;

		tier = 2;
		DLY = 1f; 
		RCH = 99;    //extra reach
	}

	@Override
	public int max(int lvl) {
		return  Math.round(5f*(tier+1)) +    //15 base,
				lvl*Math.round(2f*(tier+1)/3); //+2 per levl
	}

    @Override
	public int proc(Char attacker, Char defender, int damage) {
        float time = Dungeon.level.distance(attacker.pos, defender.pos) - 1;
        if(time > 0){
            time = time * (1f/speedMultiplier(attacker));
        }
        attacker.spend_modified(time);
		return super.proc(attacker, defender, damage);
	}
}

/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2021 Evan Debenham
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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import java.util.ArrayList;

public class HisouBladeAwakened extends MeleeWeapon {

	{
		image = ItemSpriteSheet.HISOUBLADE_FIRE;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 0.5f;

		tier = 5;
		DLY = 0.5f;
		RCH = 2;
	}

	@Override
	public int max(int lvl) {
		return  3*(tier+1) +
				lvl/3*(tier+1);
	}

    @Override
	public int proc(Char attacker, Char defender, int damage) {
		Buff.affect(defender, Burning.class).reignite(defender);
		return super.proc(attacker, defender, damage);
	}


}


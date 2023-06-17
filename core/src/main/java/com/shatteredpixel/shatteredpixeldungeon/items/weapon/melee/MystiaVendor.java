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

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;


public class MystiaVendor extends MeleeWeapon {

	{
		image = ItemSpriteSheet.MYSTIA_VENDOR;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 0.2f;

		tier = 2;
		DLY = 2f;

		TIME_TO_EQUIP = 3f;
    }

	//For hero movespeed check Hero.speed()
    
	@Override
    protected float time2equip( Hero hero ) {
		return 3;
	}

    @Override
	public int min(int lvl) {
		return  Math.round((tier+2)) + //4 base
				lvl*2; // 2 per level
	}

	@Override
	public int max(int lvl) {
		return  Math.round(5f*(tier+3)) + //25 base
				lvl*Math.round(1.5f*(tier+2)); // tier 3 scale *1.5
	}

    @Override
	public int defenseFactor( Char owner ) {
		return 2+1*buffedLvl();     //2 extra defence, plus 1 per level;
	}
	
	public String statsInfo(){
		if (isIdentified()){
			return Messages.get(this, "stats_desc", 2+1*buffedLvl());
		} else {
			return Messages.get(this, "typical_stats_desc", 2);
		}
	}
}

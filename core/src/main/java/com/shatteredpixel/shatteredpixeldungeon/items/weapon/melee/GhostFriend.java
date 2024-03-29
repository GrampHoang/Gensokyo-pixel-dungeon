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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class GhostFriend extends MeleeWeapon {

	// TODO: CHANGE NAME

	{
		image = ItemSpriteSheet.GHOST_FRIEND;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 2f;
		ACC = 1.2f;
		tier = 4;
	}

    @Override
	public int max(int lvl) {
		return  Math.round(3f*(tier+1)) +			//base 15
				lvl*(tier-1);						//3 per level
	}

    @Override
	public int damageRoll(Char owner) {
        return 0;
	}

    @Override
	public int proc(Char attacker, Char defender, int damage) {
		//Check damage augment then add strenght
        int real_damage = augment.damageFactor(Random.NormalIntRange( min(), max()));
		if (attacker instanceof Hero){
            int exStr = Dungeon.hero.STR() - STRReq();
            if (exStr > 0) {
                real_damage += Random.IntRange(0, exStr);
            }
        }
		defender.damage(real_damage, attacker);
        return super.proc(attacker, defender, 0);
	}
}

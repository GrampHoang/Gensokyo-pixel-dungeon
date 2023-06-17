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
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Random;

public class Knife extends MeleeWeapon {

	{
		image = ItemSpriteSheet.KNIFE;
		hitSound = Assets.Sounds.HIT_STAB;
		hitSoundPitch = 1.2f;

		tier = 1;
		DLY = 0.8f; //1.25x speed
	}

	@Override
	public int max(int lvl) {
		return  4*(tier+1) - 1 +    //  7 base
				lvl*(tier+1);   //scaling unchanged
	}

    @Override
	public int damageRoll(Char owner) {
		if (owner instanceof Hero) {
			Hero hero = (Hero) owner;
			Char enemy = hero.enemy();
			if (Dungeon.hero.belongings.weapon() instanceof Knife) {
                if ((buffedLvl() + 1) > Random.IntRange(0, (buffedLvl() + 5) )){
                    Buff.affect(enemy, Bleeding.class).set(Math.round((max()+1)/4));
                }
			}
		}
		return super.damageRoll(owner);
	}

    public String statsInfo(){
        int dmg = Math.round((max()+1)/4);
        int lvl = buffedLvl() + 1;
        int chance = Math.round(lvl*100 / (lvl + 4));
		return Messages.get(this, "stats_desc", dmg, chance) + "_%_.";
	}

}

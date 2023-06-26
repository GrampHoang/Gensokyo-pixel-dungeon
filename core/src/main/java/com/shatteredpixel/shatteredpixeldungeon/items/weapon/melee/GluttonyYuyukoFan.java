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

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.watabou.utils.Random;

public class GluttonyYuyukoFan extends GluttonyFan {

    // 2 More base damage
    // Better food scaling
    // Special effect in Hero.java, die()
    // Can only be acquired from Yuyuko quest

	{
		image = ItemSpriteSheet.GLUTTONYFAN;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1.3f;

		tier = 3;
		DLY = 1f;
    }

	@Override
	public int max(int lvl) {
		return  Statistics.foodEaten/2 +
                Math.round(4f*(tier+1)) + 2 +
				lvl*Math.round(1f*(tier+1));
	}

    @Override
	public int damageRoll(Char owner) {
        int damage1 = augment.damageFactor(Random.NormalIntRange( min(), max()));
        float damage = damage1;
        if (owner instanceof Hero){
            int exStr = Dungeon.hero.STR() - STRReq();
            if (exStr > 0) {
                damage1 += Random.IntRange(0, exStr);
            }

            float curhunger = Dungeon.hero.buff(Hunger.class).getHunger();
            damage = damage1 * (5/4 - ((curhunger-150)/301)/2);
        }
        return Math.round(damage);
	}
}

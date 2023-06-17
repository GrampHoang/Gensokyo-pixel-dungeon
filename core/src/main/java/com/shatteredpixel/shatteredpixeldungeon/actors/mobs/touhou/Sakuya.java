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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.effects.Chains;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SakuyaSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class Sakuya extends Mob {

	{
		spriteClass = SakuyaSprite.class;

		HP = HT = 30;
		defenseSkill = 10;

		EXP = 7;
		maxLvl = 14;
        baseSpeed = 1.3f;

		loot = Generator.Category.ARMOR;
		lootChance = 0.2f; //by default, see lootChance()

		if (isLunatic()){
			immunities.add(Sleep.class);
			immunities.add(MagicalSleep.class);
		};
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(3, 8);
	}

    @Override
	public float attackDelay() {
		return super.attackDelay()*0.5f;
	}

	@Override
	public float speed() {
		if (enemySeen){
			return super.speed();
		}else{
			return super.speed()*3f;
		}
	}

	@Override
	public int attackSkill( Char target ) {
		return 12;
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 5);
	}

	@Override
	public float lootChance() {
		//each drop makes future drops 1/2 as likely
		// so loot chance looks like: 1/5, 1/10, 1/20, 1/40, etc.
		return super.lootChance() * (float)Math.pow(1/2f, Dungeon.LimitedDrops.GUARD_ARM.count);
	}

	@Override
	public Item createLoot() {
		Dungeon.LimitedDrops.GUARD_ARM.count++;
		return super.createLoot();
	}
}

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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WriggleBug;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WriggleSprite;
import com.watabou.utils.Random;

public class Wriggle extends Mob {
	{
		spriteClass = WriggleSprite.class;
		HP = HT = 10;
		defenseSkill = 3;
		EXP = 2;
		maxLvl = 8;
        loot = Generator.Category.SEED;
		lootChance = 0.4f;
	}


	@Override
	public int damageRoll() {
		return Random.NormalIntRange(1, 6);
	}

	@Override
	public int attackSkill(Char target) {
		return 9;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 2);
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		damage = super.attackProc(enemy, damage);
		if (enemy instanceof Hero && Random.IntRange(0,2) == 1) {
			Buff.affect(enemy, Light.class, 3f);
			Buff.prolong(enemy, Blindness.class, 2f);
            Buff.prolong(enemy, Hex.class, 3f);
			return damage;
		}
		return damage;
	}
	
	@Override
	public void damage(int dmg, Object src) {
		if(surprisedBy(Dungeon.hero) && (!isLunatic())){
			dmg = Math.round(dmg*1.5f);
		}
		super.damage(dmg, src);
	}

    @Override
	protected boolean act() {
		return super.act();
	}

	@Override
	public void die(Object cause) {
		if(Random.Int(500) == 1){
			Dungeon.level.drop( new WriggleBug(), pos ).sprite.drop();
		}
		super.die(cause);
	}
}

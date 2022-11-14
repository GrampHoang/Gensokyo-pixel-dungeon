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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
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
	protected boolean act() {
		return super.act();
	}

	// public boolean jumpkick(Char kickVictim){
	// 	int leapPos = kickVictim.pos;
	// 	Ballistica b = new Ballistica(pos, leapPos, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID);
	// 	//check if leap pos is not obstructed by terrain
	// 	if (rooted || b.collisionPos != leapPos){
	// 		leapPos = -1;
	// 		return true;
	// 	}

	// 	final int endPos;
	// 	sprite.visible = Dungeon.level.heroFOV[pos] || Dungeon.level.heroFOV[leapPos] || Dungeon.level.heroFOV[endPos];
	// 	sprite.jump(pos, leapPos, new Callback() {
	// 		@Override
	// 		public void call() {

	// 			if (kickVictim != null && alignment != kickVictim.alignment){
	// 				Buff.prolong(kickVictim, Paralysis.class, 1.5f);
	// 				kickVictim.sprite.flash();
	// 				Sample.INSTANCE.play(Assets.Sounds.HIT);
	// 			}

	// 			if (endPos != leapPos){
	// 				Actor.addDelayed(new Pushing(MeilingBoss.this, leapPos, endPos), -1);
	// 			}

	// 			pos = endPos;
	// 			leapPos = -1;
	// 			sprite.idle();
	// 			Dungeon.level.occupyCell(Wriggle.this);
	// 			next();
	// 		}
	// 	});
	// 	return false;
	// 	return true;
	// }

	@Override
	public void die(Object cause) {
		super.die(cause);
	}
}

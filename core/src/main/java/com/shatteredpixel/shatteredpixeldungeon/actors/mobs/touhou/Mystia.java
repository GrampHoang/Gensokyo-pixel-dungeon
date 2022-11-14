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
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MystiaSprite;
import com.watabou.utils.Random;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class Mystia extends Mob {
	{
		spriteClass = MystiaSprite.class;
		HP = HT = 10;
		defenseSkill = 3;
		EXP = 2;
		maxLvl = 8;
		loot = Gold.class;
		lootChance = 0.5f;
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
	public float speed() {
		return super.speed()*0.9f;
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 2);
	}

    @Override
    public void damage(int dmg, Object src) {
        sing(20);
		if (Dungeon.isChallenged(Challenges.LUNATIC)){
			sing(20);	//roll again lmao
		}
        super.damage(dmg, src);
    }

	@Override
	public int attackProc(Char enemy, int damage) {
		damage = super.attackProc(enemy, damage);
		if (enemy instanceof Hero) {
            sing(10);
			if (Dungeon.isChallenged(Challenges.LUNATIC)){
				sing(10);	//roll again lmao., maybe higher chance? nah
			}
		}
		return damage;
	}

	//Higher roll = less chance
	public void sing(int roll){
		if (Random.IntRange(0, roll) == 1 ){
			for (Mob mob : Dungeon.level.mobs) {
				mob.beckon( pos );
				if (Dungeon.isChallenged(Challenges.LUNATIC)){
					Buff.affect(mob, Haste.class, 2f);
				}
			}

			if (Dungeon.level.heroFOV[pos]) {
				CellEmitter.center( pos ).start( Speck.factory( Speck.SCREAM ), 0.3f, 3 );
			}
			
			if (Dungeon.isChallenged(Challenges.LUNATIC) && enemy != null){
				Buff.affect(enemy, Blindness.class, 2f);
			}
		}
	}

    @Override
	protected boolean act() {
		return super.act();
	}

	@Override
	public void die(Object cause) {
		super.die(cause);
	}
}

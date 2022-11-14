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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.*;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.*;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.AquaBlast;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.LettySprite;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Letty extends Mob {
	{
		spriteClass = LettySprite.class;
		HP = HT = 30;
		defenseSkill = 15;
		EXP = 7;
		maxLvl = 15;
        loot = AquaBlast.class;
		lootChance = 0.75f;

		immunities.add(Freezing.class);
		immunities.add(Chill.class);
	}


	@Override
	public int damageRoll() {
		return Random.NormalIntRange(5, 15);
	}

	@Override
	public int attackSkill(Char target) {
		return 16;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 6);
	}

    // @Override
	// public float speed() {
	// 	return super.speed();
	// }

	// @Override
	// public int defenseProc( Char enemy, int damage ) {		
	// 	return super.defenseProc(enemy, damage);
	// }

    // @Override
	// protected boolean canAttack( Char enemy ) {
	// 	Ballistica attack = new Ballistica( pos, enemy.pos, Ballistica.WONT_STOP);
	// 	return !Dungeon.level.adjacent(pos, enemy.pos) && attack.collisionPos == enemy.pos;
	// }

	@Override
	public int attackProc(Char enemy, int damage) {
		damage = super.attackProc(enemy, damage);
		if (enemy instanceof Hero) {
			Buff.prolong(enemy, Chill.class, 0.5f);
			return damage;
		}
		return damage;
	}

    @Override
	protected boolean act() {
        for (int i : PathFinder.NEIGHBOURS9){
			//avoid items
			Heap heap = Dungeon.level.heaps.get( this.pos + i );
			if(Random.IntRange(0, 2) == 1 && heap == null){
				GameScene.add( Blob.seed( this.pos + i, 2, Freezing.class ) );
			}
		}
		return super.act();
	}

	@Override
	public void die(Object cause) {
		if(isLunatic()){
			CellEmitter.get(this.pos).burst(SmokeParticle.FACTORY, 4);
			PathFinder.buildDistanceMap( this.pos, BArray.not( Dungeon.level.solid, null ), 2 );
			for (int i = 0; i < PathFinder.distance.length; i++) {
				if (PathFinder.distance[i] < Integer.MAX_VALUE) {
					//avoid items and mobs, including hero
					Heap heap = Dungeon.level.heaps.get(i);
					Char ch = Actor.findChar(i);
					if(heap == null || ch != null){
						GameScene.add(Blob.seed(i, 5, Freezing.class));
					}
				}
			}
		}
		super.die(cause);
	}
}

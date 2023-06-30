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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CirnoSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Icecream;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.CirnoIcecream;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.watabou.utils.Random;

public class Cirno extends Mob {
	{
		spriteClass = CirnoSprite.class;
		HP = HT = 20;
		defenseSkill = 5;
		EXP = 4;
		maxLvl = 9;
        loot = Icecream.class;
		lootChance = 0.1666f;

		properties.add(Property.FAIRY);
	}


	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 2, 6 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 12;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(2, 4);
	}

	@Override
	protected boolean canAttack( Char enemy ) {
		boolean can = super.canAttack(enemy);
		if (can) return can;	// Champion buff take priority
		if (isLunatic()){
			Ballistica attack = new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE);
			return Dungeon.level.distance(this.pos, enemy.pos) < 3 && attack.collisionPos == enemy.pos;
		} else {
			return super.canAttack(enemy);
		}
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		Buff.affect(enemy, Chill.class, 2f);
		return super.attackProc(enemy, damage);
	}

    @Override
	protected boolean act() {
		return super.act();
	}

	@Override
	public void die(Object cause) {
        // if (this.flying || !Dungeon.level.pit[this.pos]) {
        //     PathFinder.buildDistanceMap( this.pos, BArray.not( Dungeon.level.solid, null ), 1 );
		// 	for (int i = 0; i < PathFinder.distance.length; i++) {
		// 		if (PathFinder.distance[i] < Integer.MAX_VALUE) {
		// 			//avoid items
		// 			Heap heap = Dungeon.level.heaps.get(i);
		// 			if(heap == null){
		// 				GameScene.add(Blob.seed(i, 2, Freezing.class));
		// 			}
		// 		}
		// 	}
        // }
			
		// if(isLunatic()){
		// 	int lastCirno = 0;
		// 	for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
		// 		if (mob instanceof Cirno){
		// 			lastCirno++;
		// 		}
		// 	}
		// 	if (lastCirno < 2){	//Because it will count the dying Cirno
		// 		// Spawn new Cirno if no other cirno present
		// 		Cirno newCirno = new Cirno();
		// 		MagicalSleep.affect(newCirno, MagicalSleep.class); 
		// 		newCirno.state = newCirno.SLEEPING;
		// 		newCirno.pos = Dungeon.level.randomRespawnCell( newCirno );
		// 		KomachiBlessing.setRandom(newCirno);
		// 		if (newCirno.pos != -1) {
		// 			GameScene.add(newCirno);
		// 		}
		// 	}
		// }

		if(Random.Int(500) == 1){
			Dungeon.level.drop( new CirnoIcecream(), pos ).sprite.drop();
		}
		super.die(cause);
	}
}

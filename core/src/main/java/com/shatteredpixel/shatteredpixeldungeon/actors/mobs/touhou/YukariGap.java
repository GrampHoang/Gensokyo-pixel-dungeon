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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.*;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dread;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Sleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.YukariSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class YukariGap extends Mob {

	{
		spriteClass = YukariSprite.class;

		HP = HT = 80;
		defenseSkill = 0;

		maxLvl = -2;

		properties.add(Property.MINIBOSS);
		properties.add(Property.INORGANIC);
		properties.add(Property.IMMOVABLE);

		state = PASSIVE;
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 6);
	}

	@Override
	public void beckon(int cell) {
		//do nothing
	}

	@Override
	public boolean reset() {
		return true;
	}

	private float SPAWNCD = 60;
	private float spawnCooldown = 0;

	// public boolean spawnRecorded = false;

	@Override
	protected boolean act() {
		// if (!spawnRecorded){
		// 	Statistics.spawnersAlive++;
		// 	spawnRecorded = true;
		// }

		spawnCooldown--;
		if (spawnCooldown <= 0){
			ArrayList<Integer> candidates = new ArrayList<>();
			for (int n : PathFinder.NEIGHBOURS8) {
				if (Dungeon.level.passable[pos+n] && Actor.findChar( pos+n ) == null) {
					candidates.add( pos+n );
				}
			}

			if (!candidates.isEmpty()) {
				Mob spawn;
                if(Random.IntRange(0, 1) == 1){
                    spawn = new YukariRan();
                } else {
                    spawn = new YukariChen();
                }

				spawn.pos = Random.element( candidates );
				spawn.state = spawn.HUNTING;

				GameScene.add( spawn, 1 );
				Dungeon.level.occupyCell(spawn);

				if (sprite.visible) {
					Actor.addDelayed(new Pushing(spawn, pos, spawn.pos), -1);
				}

				spawnCooldown += SPAWNCD;
			}
		}
		alerted = false;
		return super.act();
	}

	@Override
	public void damage(int dmg, Object src) {
		if (dmg >= 20){
			//takes 20/21/22/23/24/25/26/27/28/29/30 dmg
			// at   20/22/25/29/34/40/47/55/64/74/85 incoming dmg
			dmg = 19 + (int)(Math.sqrt(8*(dmg - 19) + 1) - 1)/2;
		}
		spawnCooldown -= dmg/4;
		super.damage(dmg, src);
	}

	@Override
	public int defenseProc(Char enemy, int damage) {
		return super.defenseProc(enemy, damage);
	}


	@Override
	public void die(Object cause) {
		// if (spawnRecorded){
		// 	Statistics.spawnersAlive--;
		// }
		GLog.h(Messages.get(this, "on_death"));
		super.die(cause);
	}

	public static final String SPAWN_COOLDOWN = "spawn_cooldown";
	public static final String SPAWN_RECORDED = "spawn_recorded";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(SPAWN_COOLDOWN, spawnCooldown);
		// bundle.put(SPAWN_RECORDED, spawnRecorded);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		spawnCooldown = bundle.getFloat(SPAWN_COOLDOWN);
		// spawnRecorded = bundle.getBoolean(SPAWN_RECORDED);
	}

	{
		immunities.add( Paralysis.class );
		immunities.add( Amok.class );
		immunities.add( Sleep.class );
		immunities.add( Dread.class );
		immunities.add( Terror.class );
		immunities.add( Vertigo.class );
		if(isLunatic()){
			immunities.add( CorrosiveGas.class );
			immunities.add( ToxicGas.class );
			immunities.add( Burning.class );
		}
	}

	public static class YukariChen extends Chen {
		{
			EXP = 0;
			maxLvl = 1;
			lootChance = 0f;
			state = HUNTING;
			
		}
	}
	public static class YukariRan extends Ran {
		{	
			EXP = 0;
			maxLvl = 1;
			lootChance = 0f;
			state = HUNTING;
		}
	}
}

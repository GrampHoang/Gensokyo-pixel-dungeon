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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dread;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Sleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ChenSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.YukariGapSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.YukariTalismanSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class YukariGap extends Mob {

	{
		spriteClass = YukariGapSprite.class;

		HP = HT = 30;
		defenseSkill = 0;

		maxLvl = -2;

		properties.add(Property.MINIBOSS);
		properties.add(Property.INORGANIC);
		properties.add(Property.IMMOVABLE);

		state = PASSIVE;
	}

	public static void spawnGap() {
		YukariGap gap = new YukariGap();
		int cell;
		do {
			cell = Random.Int( Dungeon.level.length() );
		} while (!Dungeon.level.passable[cell]
				|| !Dungeon.level.openSpace[cell]);
		gap.pos = cell;
		CellEmitter.get(cell).start(Speck.factory(Speck.LIGHT), 0.3f, 5);
		GameScene.add( gap, 0 );
		Dungeon.level.occupyCell( gap );
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
		return super.act();
	}

	@Override
	public void damage(int dmg, Object src) {
		if(src instanceof YukariChen || src instanceof YukariRan){
			super.damage(dmg, src);
		} //do nothing if damage src from some where else
	}

	@Override
	public int defenseProc(Char enemy, int damage) {
		return super.defenseProc(enemy, damage);
	}


	@Override
	public void die(Object cause) {
		for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
			if (mob instanceof YukariBoss && ((YukariBoss)mob).phase == 2) {
				mob.damage(mob.HP/3+2, YukariGap.class);
				spawnGap();
			}
		}
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

		@Override
		public void damage(int dmg, Object src) {
			return;
		}
		
		@Override
		protected boolean act() {
			if (paralysed > 0 || buff(Frost.class) != null || buff(Roots.class) != null) {
				spend( TICK );
				return true;
			}
			if(rolling == true){
				rolling = false;
				spend(TICK);
				return roll(enemy_pos);
			} else{
				// if(enemy == null){
				// 	enemy_pos = Dungeon.hero.pos;
				// }
				enemy_pos = ready(Dungeon.hero.pos);
				spend(TICK);
				rolling = true;
				return true;
			}
		}
		@Override
		protected int ready(int target){
			((ChenSprite)sprite).spinning();
			Ballistica b;
			do{
				b = new Ballistica(this.pos, target, Ballistica.STOP_SOLID);
				target -= 5;
			}while (b.collisionPos == this.pos);
			//Make sure she won't roll into pit
			while(Dungeon.level.pit[b.collisionPos] && b.collisionPos != this.pos){
				b.collisionPos = b.path.get(b.path.indexOf(b.collisionPos) - 1);
			}
			for (int p : b.subPath(0, Dungeon.level.distance(this.pos, b.collisionPos))){
				sprite.parent.add(new TargetedCell(p, 0xFF0000));
			}
			GLog.w("Chen is spining!");
			return b.collisionPos;
		}

		@Override
		protected boolean roll(int stopCell) {
			//push char
			Char cha = Actor.findChar(stopCell);
			int push_pos = this.pos;
			if (cha != null && cha != this){
				for (int i : PathFinder.NEIGHBOURS8){
					if (Actor.findChar(stopCell + i) == null && Dungeon.level.passable[stopCell + i]){
						push_pos = stopCell+i;
						break;
					}
				}
				Actor.addDelayed(new Pushing(cha, cha.pos, push_pos), 0);
				// ch.moveSprite(ch.pos, push_pos);
				cha.move(push_pos);
				Dungeon.level.occupyCell(cha);
				
			}
			//roll
			sprite.parent.add(new Beam.DeathRay(sprite.center(), DungeonTilemap.raisedTileCenterToWorld(stopCell)));
			Ballistica b = new Ballistica(this.pos, stopCell, Ballistica.STOP_SOLID);
			for (int p : b.subPath(0, Dungeon.level.distance(this.pos, stopCell))){
				Char ch = Actor.findChar(p);
				if (ch != null && !(ch instanceof Chen)){
					ch.damage(20 - ch.drRoll(), this);	//Affected by armor , "Physical roll"
					if (ch instanceof Hero) Statistics.qualifiedForBossChallengeBadge = false;
				}
			}

			for(int i : PathFinder.NEIGHBOURS8){
				CellEmitter.get( i + stopCell ).start( Speck.factory( Speck.ROCK ), 0.07f, 10 );
				Char ch = Actor.findChar(i + stopCell);
				if (ch != null && !(ch instanceof Chen)){
					ch.damage(12 - ch.drRoll(), this);
					if (ch instanceof Hero) Statistics.qualifiedForBossChallengeBadge = false;
				}
			}
			//move
			this.move( stopCell);
			this.moveSprite(this.pos, stopCell);
			return true;
		}
	}

	public static class YukariRan extends Ran {
		{	
			EXP = 0;
			maxLvl = 1;
			lootChance = 0f;
			state = HUNTING;
		}

		protected boolean rolling = false;
		protected int enemy_pos = this.pos; //just to be safe

		@Override
		public void damage(int dmg, Object src) {
			return;
		}

		@Override
		protected boolean act() {
			if(rolling == true){
				rolling = false;
				spend(TICK);
				return roll(enemy_pos);
			} else{
				// if(enemy == null){
				// 	enemy_pos = Dungeon.hero.pos;
				// }
				enemy_pos = ready(Dungeon.hero.pos);
				spend(TICK);
				rolling = true;
				return super.act();
			}
		}

		protected int ready(int target){
			// ((ChenSprite)sprite).spinning();
			// CellEmitter.center(this.pos).burst(RainbowParticle.BURST, 20);
			Ballistica b;
			do{
				b = new Ballistica(this.pos, target, Ballistica.STOP_SOLID);
				target -= 5;
			}while (b.collisionPos == this.pos);
			//Make sure she won't roll into pit
			while(Dungeon.level.pit[b.collisionPos] && b.collisionPos != this.pos){
				b.collisionPos = b.path.get(b.path.indexOf(b.collisionPos) - 1);
			}
			for (int p : b.subPath(0, Dungeon.level.distance(this.pos, b.collisionPos))){
				sprite.parent.add(new TargetedCell(p, 0xFF0000));
			}
			GLog.w("Chen is spining!");
			return b.collisionPos;
		}
		
		protected boolean roll(int stopCell) {
			//push char
			Char cha = Actor.findChar(stopCell);
			int push_pos = this.pos;
			if (cha != null && cha != this){
				for (int i : PathFinder.NEIGHBOURS8){
					if (Actor.findChar(stopCell + i) == null && Dungeon.level.passable[stopCell + i]){
						push_pos = stopCell+i;
						break;
					}
				}
				Actor.addDelayed(new Pushing(cha, cha.pos, push_pos), 0);
				// ch.moveSprite(ch.pos, push_pos);
				cha.move(push_pos);
				Dungeon.level.occupyCell(cha);
			}
			//roll
			sprite.parent.add(new Beam.DeathRay(sprite.center(), DungeonTilemap.raisedTileCenterToWorld(stopCell)));
			Ballistica b = new Ballistica(this.pos, stopCell, Ballistica.STOP_SOLID);
			for (int p : b.subPath(0, Dungeon.level.distance(this.pos, stopCell))){
				Char ch = Actor.findChar(p);
				if (ch != null && !(ch instanceof Chen)){
					ch.damage(10, this);
					if (ch instanceof Hero) Statistics.qualifiedForBossChallengeBadge = false;
				}
			}
			for(int i : PathFinder.NEIGHBOURS8){
				CellEmitter.get( i + stopCell ).start( Speck.factory( Speck.ROCK ), 0.07f, 10 );
				Char ch = Actor.findChar(i+ stopCell);
				if (ch != null && !(ch instanceof Chen)){
					ch.damage(6, this);
					if (ch instanceof Hero) Statistics.qualifiedForBossChallengeBadge = false;
				}
			}
			//move
			this.move( stopCell);
			this.moveSprite(this.pos, stopCell);
			return true;
		}

		private static final String ROLLING = "rolling";
		private static final String ENEMY_POS = "enemy_pos";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put( ENEMY_POS, enemy_pos );
			bundle.put( ROLLING, rolling );
		}
		
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			enemy_pos = bundle.getInt( ENEMY_POS );
			rolling = bundle.getBoolean( ROLLING );
		}
	}

	public static class YukariTalisman extends Mob {
		{
			spriteClass = YukariTalismanSprite.class;

			EXP = 0;
			maxLvl = 1;
			lootChance = 0f;
			state = HUNTING;
			
		}

		@Override
		public void damage(int dmg, Object src) {
			return;
		}
		
		public static void spawnMass(int count){
			for (int i = 0; i < count; i++){
				YukariTalisman tal = new YukariTalisman();
				int cell;
				do {
					cell = Random.Int( Dungeon.level.length() );
				} while ( Dungeon.level.distance(cell, 593) < 4 ||
					(!Dungeon.level.passable[cell] || !Dungeon.level.openSpace[cell]));
				tal.pos = cell;
				CellEmitter.get(cell).start(Speck.factory(Speck.LIGHT), 0.2f, 3);
				GameScene.add( tal, 1 );
				Dungeon.level.occupyCell( tal );
			}
		}

		@Override
		protected boolean act() {
			int target = 593;	//Middle map, just in case
			Ballistica b = new Ballistica(this.pos, target, Ballistica.STOP_TARGET);;
			for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
				if (mob instanceof YukariBoss) {
					mob.HP +=1;
					target = mob.pos;
					b = new Ballistica(this.pos, target, Ballistica.STOP_TARGET);
				}
			}
			sprite.parent.add(new Beam.DeathRay(sprite.center(), DungeonTilemap.raisedTileCenterToWorld(target)));

			for (int p : b.subPath(0, Dungeon.level.distance(this.pos, target))){
				Char ch = Actor.findChar(p);
				if (ch != null && !(ch instanceof YukariChen || ch instanceof YukariRan || ch instanceof YukariGap || ch instanceof YukariBoss)){
					ch.damage(10, this);
					if (ch instanceof Hero) Statistics.qualifiedForBossChallengeBadge = false;
				}
			}
			die(null);
			return true;
		}

		@Override
		public void die(Object cause) {
			destroy();
			sprite.killAndErase();
			Dungeon.level.mobs.remove(this);
		}
	}
}

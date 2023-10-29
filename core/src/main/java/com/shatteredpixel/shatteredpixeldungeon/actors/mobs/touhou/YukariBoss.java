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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou.YukariGap.YukariChen;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou.YukariGap.YukariRan;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.*;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.YukariSprite;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.watabou.noosa.Camera;
import com.watabou.utils.Bundle;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.noosa.particles.Emitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class YukariBoss extends Mob {

	{
		spriteClass = YukariSprite.class;

		HP = HT = Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 400 : 300;

		defenseSkill = 15;
        flying = false;
		EXP = 30;

		viewDistance = 12;

		properties.add(Property.BOSS);
	}

	//Yeah, 3 individual int cuz I'm lazy
	private int trinestpos1 = 1;
	private int trinestpos2 = 3;
	private int trinestpos3 = 3;

	public int phase = 1;

	private int TRINEST_CD = 12; //12
	private int trinest_cd = TRINEST_CD;
	private int YAKUMONEST_CD = 18; //18
	private int yakumonest_cd = YAKUMONEST_CD;
	private int CEILING_CD = 25;	//25
	private int ceiling_cd = CEILING_CD;

	private boolean lastStand = false;

	private int charging_skill = 0;	//0: free 1:trinest, 2:yakumonest_corner, 3:yakumonest_stars, 4:chen, 5:ceiling

	@Override
	public void notice() {
		super.notice();
		if (!BossHealthBar.isAssigned()) {
			BossHealthBar.assignBoss(this);
		}
	}

	@Override
	protected void onAdd() {
		//when he's removed and re-added to the fight, his time is always set to now.
		if (cooldown() > TICK) {
			timeToNow();
			spendToWhole();
		}
		super.onAdd();
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 15, 25 );
	}

	@Override
	public int attackSkill( Char target ) {
		return 20;
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 10);
	}

	@Override
	public void damage(int dmg, Object src) {
		if (!Dungeon.level.mobs.contains(this)){
			return;
		}
		
		if (src instanceof YukariGap.YukariChen || src instanceof YukariGap.YukariRan){
			return;
		}

		int beforeHitHP = HP;
		super.damage(dmg, src);
		dmg = beforeHitHP - HP;

		if (phase == 1 && HP > 0) {
			if (HP <= (Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 100 : 75)) {
				HP = (Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 400 : 300);
				// ScrollOfTeleportation.appear(this, YukariBossLevel.exit);
				phase = 2;
				sprite.idle();
				ScrollOfTeleportation.appear(this, 89);
				
				// Summon Chen, Ran and portals here
				spawnChenRan();
				
				YukariGap.spawnGap();
				YukariGap.spawnGap();
				YukariGap.spawnGap();
				YukariGap.spawnGap();

				Buff.append(Dungeon.hero, TalismanOfForesight.HeapAwareness.class, 999).pos = this.pos;
			}
		} else if (phase == 2 && HP >= 50) {
			
		} else if (phase == 2 && HP < 50) {
			Dungeon.hero.buff(TalismanOfForesight.HeapAwareness.class).detach();
			KomachiBlessing.setRange(this);
			phase = 3;
			sprite.centerEmitter().start( Speck.factory( Speck.SCREAM ), 0.4f, 2 );
			Sample.INSTANCE.play( Assets.Sounds.CHALLENGE );

			for (Mob m : getSubjects()){
				if (m != null && !(m instanceof YukariBoss)) m.die(null);
			}

			Char c =Actor.findChar(593);
			if (c!= null){
				c.die(this);
				if (c instanceof Hero){
					// TODO
					//Give an  achievement
				}
			}

			ScrollOfTeleportation.appear(this, 593); //Appear at the entrance
		}
	}
	
	private HashSet<Mob> getSubjects(){
		HashSet<Mob> subjects = new HashSet<>();
		for (Mob m : Dungeon.level.mobs){
			if (m.alignment == alignment){
				subjects.add(m);
			}
		}
		return subjects;
	}

	@Override
	public void die( Object cause ) {
		Dungeon.level.drop( new PotionOfExperience(), pos ).sprite.drop();
		Dungeon.level.unseal();
		GameScene.bossSlain();

		for (Mob m : getSubjects()){
			if (m != null && !(m instanceof YukariBoss)) m.die(null);
		}

		Statistics.bossScores[2] += 2000;
		super.die( cause );
	}

	// @Override
	// public int attackProc(Char hero, int damage) {
	// 	damage = super.attackProc(enemy, damage);
	// 	return damage;
	// }

	@Override
	protected boolean getCloser( int target ) {
			return super.getCloser( target );
	}

	@Override
	public void aggro(Char ch) {
		//cannot be aggroed to something it can't see
		if (ch == null || fieldOfView == null || fieldOfView[ch.pos]) {
			super.aggro(ch);
		}
	}

    @Override
    public boolean act() {
		if(phase == 1 || lastStand == true){
			if(!enemySeen && state != WANDERING) {
				enemy = Dungeon.hero;
				sprite.showLost();
				state = WANDERING;
				return true;
			}
			if (canUseAbility()){
				return useAbility();
			}
			if (canUseReady()){
				return useReady();
			}
			yakumonest_cd--;
			trinest_cd--;
			ceiling_cd--;
			if(Dungeon.level.distance(this.pos, Dungeon.hero.pos) > 2) ceiling_cd--;
		} else if (phase == 2){
			//Do nothing
		} else if (phase == 3 && HP < 300){
			properties.add(Property.IMMOVABLE);
			for(int i : PathFinder.NEIGHBOURS8){
				CellEmitter.get( i + this.pos ).start( Speck.factory( Speck.ROCK ), 0.07f, 10 );
				Char ch = Actor.findChar(i + this.pos);
				if (ch != null){
					ch.damage(10, this);
				}
			}
			YukariGap.YukariTalisman.spawnMass(9);
			Buff.affect(this, Roots.class, 1f);
			Buff.affect(Dungeon.hero, MindVision.class, 1f);
		} else if (phase == 3 && HP >= 300){
			spawnChenRan();
			KomachiBlessing.tryDetach(this);
			lastStand = true;
			properties.remove(Property.IMMOVABLE);
		}
		return super.act();
    }

	//SKILLLLLLLLL

	private void spawnChenRan(){
		YukariGap.YukariChen cheen = new YukariChen();
		cheen.pos = 603;
		cheen.state = HUNTING;
		GameScene.add( cheen, 1 );
		Dungeon.level.occupyCell( cheen );

		YukariGap.YukariRan raan = new YukariRan();
		raan.pos = 584;
		raan.state = HUNTING;
		GameScene.add( raan, 2 );
		Dungeon.level.occupyCell( raan );
	}

	public void toSafety(){
		int telepos;
		do {
			telepos = Random.Int( Dungeon.level.length() );
		} while (!Dungeon.level.passable[telepos] || !Dungeon.level.openSpace[telepos]);
		CellEmitter.center(this.pos).burst(RainbowParticle.BURST, 10);
		ScrollOfTeleportation.appear(this, telepos);
		Dungeon.level.occupyCell(this);
	}

	public boolean canUseReady(){
		if(yakumonest_cd < 1 && enemySeen && (Dungeon.level.distance(this.pos, Dungeon.hero.pos) < 3)){
			charging_skill = 2;
			return true;
		} else if(trinest_cd < 1 && enemySeen){
			charging_skill = 1;
			return true;
		} else if(ceiling_cd < 1 && (Dungeon.level.distance(this.pos, Dungeon.hero.pos) > 1)){
			charging_skill = 5;
			return true;
		}
		return false;
	}
	
	public boolean useReady(){
		Dungeon.hero.interrupt();
		if(charging_skill == 2 && enemySeen){
			spend(TICK);
			charging_skill = 0;
			yakumonest_cd = 999;
			return true;
		} else if(charging_skill == 1 && enemySeen){
			
			triNest(enemy);
			spend(TICK);
			return true;
		} else if(charging_skill == 5){
			gapCeiling(); //This is a skill with no need for ready
			spend(TICK);
			return true;
		}
		return false;
	}

	public boolean canUseAbility(){
		return charging_skill != 0;
	}
	
	public boolean useAbility(){
		Dungeon.hero.interrupt();
		if((charging_skill == 2 || charging_skill == 3) && enemySeen){
			spend(TICK);
			return true;
		} else if(charging_skill == 1 && enemySeen){
			triNest_shot();
			spend(TICK);
			return true;
		}
		spend(TICK);
		return false;
	}

	public void shootLaser(int target_pos){
		Ballistica laser = new Ballistica(this.pos, target_pos, Ballistica.STOP_SOLID);
		sprite.parent.add(new Beam.DeathRay(sprite.center(), DungeonTilemap.raisedTileCenterToWorld(laser.collisionPos)));
		for (int p : laser.subPath(0, Dungeon.level.distance(this.pos, laser.collisionPos))){
			if(this.HP*2 < this.HT) CellEmitter.get(p).burst(SmokeParticle.FACTORY, 10);
            Char ch = Actor.findChar(p);
			if(ch != null && !(ch instanceof YukariBoss)){
				ch.damage(Math.max(ch.HP/4, 10), this);
				Buff.prolong(ch, Blindness.class, 5f);
				if(this.HP*2 < this.HT){
					Buff.prolong(ch, Blindness.class, 9f);
					Buff.prolong(ch, Vertigo.class, 3f);
				}
			}
        }
	}

	public void triNest(Char enemy){
		trinest_cd = TRINEST_CD;
		Ballistica direct = new Ballistica(this.pos, enemy.pos, Ballistica.STOP_SOLID);
		trinestpos1 = direct.collisionPos;
		Ballistica four;
		Ballistica corners;
		int try_count = 0; //
		do{
			try_count++;
			int ran_four = Random.IntArray(PathFinder.NEIGHBOURS4) + enemy.pos;
			four = new Ballistica(this.pos, ran_four, Ballistica.STOP_SOLID);
			trinestpos2 = four.collisionPos;
		} while(trinestpos2 == trinestpos1 && try_count < 20 && trinestpos2 != this.pos);
		do{
			try_count++;
			int ran_corners = Random.IntArray(PathFinder.NEIGHBOURS4_CORNERS) + enemy.pos;
			corners = new Ballistica(this.pos, ran_corners, Ballistica.STOP_SOLID);
			trinestpos3 = corners.collisionPos;
		} while((trinestpos3 == trinestpos1 || trinestpos3 == trinestpos2) && trinestpos2 != this.pos && try_count < 50);


		for (int p : direct.subPath(0, Dungeon.level.distance(this.pos, trinestpos1))){
            sprite.parent.add(new TargetedCell(p, 0xFF0000));
        }
		for (int p : four.subPath(0, Dungeon.level.distance(this.pos, trinestpos2))){
            sprite.parent.add(new TargetedCell(p, 0xFF0000));
        }
		for (int p : corners.subPath(0, Dungeon.level.distance(this.pos, trinestpos3))){
            sprite.parent.add(new TargetedCell(p, 0xFF0000));
        }
		//Shoot 3 lasers
	}

	public void triNest_shot(){
		shootLaser(trinestpos1);
		shootLaser(trinestpos2);
		shootLaser(trinestpos3);
		toSafety();
		charging_skill = 0;
	}

	public void summonChen(){
		//Push then summon Chen
	}

	public void gapCeiling(){
		//punish staying in choke point/small area for range attack
		ceiling_cd = CEILING_CD;
		Buff.affect(Dungeon.hero, FallingRock.class);
		Buff.affect(Dungeon.hero, Blindness.class, 4f);
		charging_skill = 0;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( "trinestPOS1", trinestpos1 );
		bundle.put( "trinestPOS2", trinestpos2 );
		bundle.put( "trinestPOS3", trinestpos3 );

		bundle.put( "TRINESCD", trinest_cd );
		bundle.put( "YAKUMONESTSCD", yakumonest_cd );
		bundle.put( "CEILINGCD", ceiling_cd );

		bundle.put( "SKILLTYPE ", charging_skill );

		bundle.put( "PHASE", phase );
		bundle.put( "LASTSTAND", lastStand );
		
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		trinestpos1 = bundle.getInt( "trinestPOS1" );
		trinestpos2 = bundle.getInt( "trinestPOS2" );
		trinestpos3 = bundle.getInt( "trinestPOS3" );

		trinest_cd 		= bundle.getInt( "TRINESCD" );
		yakumonest_cd 	= bundle.getInt( "YAKUMONESTSCD" );
		ceiling_cd 		= bundle.getInt( "CEILINGCD" );

		charging_skill 	= bundle.getInt( "SKILLTYPE" );

		phase = bundle.getInt( "PHASE" );
		lastStand = bundle.getBoolean("LASTSTAND");

		BossHealthBar.assignBoss(this);
	}

	@Override
	public String description() {
		String descript = super.description();
		if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)){
			descript = descript + "\n\n_Badder Bosses:_\n" + Messages.get(this, "stronger_bosses");
		}
		return descript;
	}

	public static class FallingRock extends FlavourBuff {

		private int count = 0;
		List<Integer> rockPositions = new ArrayList<Integer>();
		private ArrayList<Emitter> rockEmitters = new ArrayList<>();

		@Override
		public boolean act() {
			count++;
			if (Dungeon.hero.buff(Paralysis.class) != null){
				count--;
			}
			else if (count == 7){
				detach();
			}
			else if (count % 2 == 1){
				for (int i : PathFinder.NEIGHBOURS8){
					if(Random.IntRange(0,2) == 1 && Dungeon.level.passable[target.pos+i]) rockPositions.add(target.pos+i);
				}
				rockPositions.add(target.pos);
				Dungeon.hero.interrupt();
				fx(true);
			} else {
				for (int i : rockPositions){
					CellEmitter.get( i ).start( Speck.factory( Speck.ROCK ), 0.07f, 10 );
					Char ch = Actor.findChar(i);
					if (ch != null && !(ch instanceof YukariBoss)){
						ch.damage(5, this);
						Buff.prolong( ch, Paralysis.class, Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 3 : 1 );
						if (ch == Dungeon.hero){
							Statistics.bossScores[2] -= 100;
						}
					}
				}
				Camera.main.shake( 2, 0.7f );
				Sample.INSTANCE.play(Assets.Sounds.ROCKS);
				fx(false);
			}
			// return super.act();
			spend(TICK);
			return true;
		}

		@Override
		public void fx(boolean on) {
			if (on && rockPositions != null){
				for (int i : this.rockPositions){
					Emitter e = CellEmitter.get(i);
					e.y -= DungeonTilemap.SIZE*0.2f;
					e.height *= 0.4f;
					e.pour(EarthParticle.FALLING, 0.1f);
					rockEmitters.add(e);
				}
			} else {
				for (Emitter e : rockEmitters){
					e.on = false;
				}
			}
		}

		private static final String COUNT = "turn_count";
		private static final String ROCKPOS = "rock_pos";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(COUNT, count);
			int[] bundleArr = new int[rockPositions.size()];
			for (int i = 0; i < rockPositions.size(); i++){
			bundleArr[i] = rockPositions.get(i);
			}
			bundle.put(ROCKPOS, bundleArr);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			count = bundle.getInt(COUNT);
			for (int i : bundle.getIntArray(ROCKPOS)){
				rockPositions.add(i);
			}
		}
	}
}
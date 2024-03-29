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
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Hakkero;
import com.shatteredpixel.shatteredpixeldungeon.items.KingsCrown;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Sheep;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.*;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.sprites.*;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.effects.MasterSparkBig;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.watabou.noosa.Camera;
import com.watabou.utils.Bundle;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.HashSet;

public class MarisaBoss extends Mob {

	{
		spriteClass = MarisaBossSprite.class;

		HP = HT = Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 500 : 375;

		defenseSkill = 20;

		EXP = 40;

		state = WANDERING;
		HUNTING = new Hunting();
		viewDistance = 20;

		properties.add(Property.BOSS);
		if(Dungeon.isChallenged(Challenges.STRONGER_BOSSES)) immunities.add(Fire.class);
		immunities.add(Paralysis.class);
		immunities.add( Slow.class );
	}

	
	private static int DASH_CD = 8;
	private int MS_CD = 16;
	private boolean charging_skill = false; 
	private int dash_cd = DASH_CD - 3;
	private int masterspark_cd = MS_CD - 3;
	private int phase = 1;

	private int count_before_tele = 0;

	private int summonpos1 = 541; 
	private int summonpos2 = 543; //left + right of entrance

	private int summonpos3 = 106;
	private int summonpos4 = 108; //left + right of exit

	private int summonpos5 = 304;
	private int summonpos6 = 306;	// left + right side of map


	@Override
	protected void onAdd() {
		//when he's removed and re-added to the fight, his time is always set to now.
		if (cooldown() > TICK) {
			timeToNow();
			spendToWhole();
		}
		super.onAdd();
	}

	public static class Rainbow{}
	
	protected void zap() {
		spend( TICK );
		
		if (hit( this, enemy, true )) {
			if (enemy == Dungeon.hero && Random.Int( 3 ) == 0) {
				Buff.prolong( enemy, Blindness.class, 2f );
				Sample.INSTANCE.play( Assets.Sounds.DEBUFF );
			}
			
			int dmg = Random.NormalIntRange( 5, 6 );
			enemy.damage( dmg, new Rainbow() );
			
			if (enemy == Dungeon.hero && !enemy.isAlive()) {
				Badges.validateDeathFromEnemyMagic();
				Dungeon.fail( getClass() );
				GLog.n( Messages.get(this, "bolt_kill") );
			}
		} else {
			enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
		}
	}
	
	public void onZapComplete() {
		zap();
		next();
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(20, 30);
	}

	@Override
	public int attackSkill(Char target) {
		return 99;
	}

	@Override
	public void damage(int dmg, Object src) {
		if (!Dungeon.level.mobs.contains(this)){
			return;
		}
		//Use hero for now, it will be janky if we use allies
		Ballistica attack = new Ballistica( pos, Dungeon.hero.pos, Ballistica.PROJECTILE);
		if (attack.collisionPos != Dungeon.hero.pos){	
			// getCloser(Dungeon.hero.pos);
			count_before_tele+=6;
		} else {
			count_before_tele = 1;
		}

		if (count_before_tele > 15){
			toSafety();
		}
		int hpBracket = HT / 3;
		int beforeHitHP = HP;
		super.damage(dmg, src);
		dmg = beforeHitHP - HP;
		if (phase == 1 && HP < HT*1/2){
			toSafety();
			callPachouli();
			phase++;
			this.sprite.centerEmitter().start( Speck.factory( Speck.SCREAM ), 0.3f, 3 );
			Sample.INSTANCE.play( Assets.Sounds.CHALLENGE);
			PotionOfHealing.heal(this);
			PotionOfHealing.cure(this);
			GLog.w("Help meeee! Patchouliiii");
		}
		if (phase == 2 && HP < HT*1/2){
			toSafety();
			callAlice();
			phase++;
			this.sprite.centerEmitter().start( Speck.factory( Speck.SCREAM ), 0.3f, 3 );
			Sample.INSTANCE.play( Assets.Sounds.CHALLENGE);
			GLog.w("Help meeee! Aliiiiiice");
		}
		
		// cannot be hit through multiple brackets at a time
		if ((beforeHitHP/hpBracket - HP/hpBracket) >= 2){
			HP = hpBracket * ((beforeHitHP/hpBracket)-1) + 1;
		}
		// if (beforeHitHP / hpBracket != HP / hpBracket) {
		// 	shootTheFloor();
		// }
	}

	public void toSafety(){
		int telepos;
		do {
			telepos = Random.Int( Dungeon.level.length() );
			// GLog.w(Integer.toString(telepos));
		} while (!Dungeon.level.passable[telepos]);
		CellEmitter.center(this.pos).burst(RainbowParticle.BURST, 10);
		ScrollOfTeleportation.appear(this, telepos);
		Dungeon.level.occupyCell(this);
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(2, 8);
	}

	@Override
	protected boolean canAttack( Char enemy ) {
		Ballistica attack = new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE);
		boolean canAtt = attack.collisionPos == enemy.pos;
		if (canAtt && enemy instanceof Hero && Dungeon.level.distance(this.pos, enemy.pos) > 2){
			Statistics.qualifiedForBossChallengeBadge = false;
		}
		return canAtt;
	}

	@Override
	protected boolean getCloser( int target ) {
		if (state == HUNTING) {
			return enemySeen && getFurther( target );
		} else {
			return super.getCloser( target );
		}
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
		return super.act();
	}
	@Override
	public void die( Object cause ) {
		super.die( cause );
		Dungeon.level.drop( new PotionOfExperience(), pos ).sprite.drop();
		Dungeon.level.drop( new KingsCrown(), pos ).sprite.drop();
		Dungeon.level.unseal();
		GameScene.bossSlain();
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		damage = super.attackProc(enemy, damage);
		if (enemy instanceof Hero) {
			// Buff.prolong(enemy, Blindness.class, 0.9f);
			return damage;
		}
		return damage;
	}

	@Override
	public void notice() {
		super.notice();
		if (!BossHealthBar.isAssigned()) {
			BossHealthBar.assignBoss(this);
		}
	}
	
	// public void shootTheFloor() {
	// 	damage = super.attackProc(enemy, damage);
	// 	if (hero instanceof Hero) {
	// 		// Buff.prolong(enemy, Blindness.class, 0.9f);
	// 		return damage;
	// 	}
	// 	return damage;
	// }

	private class Hunting extends Mob.Hunting{
		
		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {
			if (enemyInFOV && !isCharmedBy( enemy ) && canAttack( enemy )) {

				if (canUseReady()){
					return useReady();
				}
				if (canUseAbility()){
					return useAbility();
				}
				return doAttack( enemy );
				
			} else {
				
				if (enemyInFOV || charging_skill) {
					target = Dungeon.hero.pos;
				} else {
					sprite.showLost();
					charging_skill = false;
					state = WANDERING;
					return true;
				}
				
				//if not charmed, attempt to use an ability, even if the enemy can't be seen
				if (canUseAbility()){
					return useAbility();
				}
				if (canUseReady()){
					return useReady();
				}
				spend( TICK );
				return true;
				
			}
		}
	}


	//SKILLLLLLLLL

	public void callPachouli(){
		callHelp(summonpos1, Elemental.FireElemental.class);
		callHelp(summonpos2, Elemental.FrostElemental.class);
		callHelp(summonpos3, Warlock.class);
		callHelp(summonpos4, Shaman.RedShaman.class);
		callHelp(summonpos5, Shaman.BlueShaman.class);
		callHelp(summonpos6, Shaman.PurpleShaman.class);
	}

	public void callAlice(){
		callHelp(summonpos1, ShanghaiDoll.class);
		callHelp(summonpos2, AliceDoll.class);
		callHelp(summonpos3, AliceDoll.class);
		callHelp(summonpos4, AliceDoll.class);
		callHelp(summonpos5, AliceDoll.class);
		callHelp(summonpos6, HouraiDoll.class);
	}

	public boolean callHelp( int pos, Class<?extends Mob> type){
		if (Actor.findChar(pos) instanceof Sheep){
			Actor.findChar(pos).die(null);
		}

		if (Actor.findChar(pos) == null) {
			Mob m = Reflection.newInstance(type);
			m.pos = pos;
			m.maxLvl = -2;
			GameScene.add(m);
			Dungeon.level.occupyCell(m);
			m.state = m.HUNTING;
		} else {
			Char ch = Actor.findChar(pos);
			CellEmitter.center(pos).burst(BlastParticle.FACTORY, 20);
			ch.damage(Random.NormalIntRange(20, 40), target);
			if (!ch.isAlive() && ch == Dungeon.hero) {
				Dungeon.fail(MarisaBoss.class);
			}
		}
		return true;
	}

	public boolean canUseReady(){
		if ((masterspark_cd < 3 && masterspark_cd > 0)|| (dash_cd < 2 && dash_cd > 0)){
			return true;
		} else {
			return false;
		}
	}

	public boolean useReady(){
		spend(TICK);
		charging_skill = true;
		if (masterspark_cd == 2){
			dash_cd++;
			masterspark_cd--;
			return masterspark_ready();
		} else if (masterspark_cd == 1) {
			dash_cd++;
			masterspark_cd--;
			return masterspark_ready_2();
		} else if (dash_cd == 1){
			dash_cd--;
			return dash_ready();
		}
		return false;
	}

	public boolean canUseAbility(){
		if (masterspark_cd < 1 || dash_cd < 1){
			return true;
		} else {
			masterspark_cd--;
			dash_cd--;
			return false;
		}
	}
	
	public boolean useAbility(){
		spend(TICK);
		charging_skill = false;
		if (masterspark_cd < 1){
			masterspark_cd = MS_CD;
			if (dash_cd < 3){dash_cd = 3;} //prevent from dash at the same time
            return masterspark();
        } 
		else if (dash_cd < 1){
			dash_cd = DASH_CD;
			if (masterspark_cd < 2){
				masterspark_cd = 4;
				targetedCells_MS.clear();
			}
            return dash();
        }
        return false;
		
		// } else {
		// 	return sts_cd(this);
		// }
	}

    // private ArrayList<Integer> targetedCells = new ArrayList<>();
    private int stopCell = 69;

    private boolean dash_ready(){
		Dungeon.hero.interrupt();
		// targetedCells.clear();
		CellEmitter.center(this.pos).burst(RainbowParticle.BURST, 20);
        Ballistica b = new Ballistica(this.pos, Dungeon.hero.pos, Ballistica.STOP_SOLID);
        for (int p : b.subPath(0, Dungeon.level.distance(this.pos, b.collisionPos))){
            sprite.parent.add(new TargetedCell(p, 0xFF0000));
            // targetedCells.add(p);
        }
        stopCell = b.collisionPos;
		GLog.w(Messages.get(MarisaBoss.class, "todash"));
        return true;
    }

    private boolean dash() {
        HashSet<Char> affected = new HashSet<>();
        boolean terrainAffected = false;
		if (stopCell == 69) stopCell = Dungeon.hero.pos;
        sprite.parent.add(new Beam.DeathRay(sprite.center(), DungeonTilemap.raisedTileCenterToWorld(stopCell)));
		// Dungeon.level.occupyCell(this);

		Camera.main.shake( 1f, 1f );
		Sample.INSTANCE.play( Assets.Sounds.ROCKS );
		CellEmitter.get(stopCell).start(Speck.factory(Speck.ROCK), 0.07f, 10);
		for (int p : PathFinder.NEIGHBOURS8) {
			CellEmitter.get(stopCell + p).start(Speck.factory(Speck.ROCK), 0.07f, 10);
			Char ch = Actor.findChar(stopCell + p);
			if(ch != null && ch != this){
				Buff.affect(ch, Paralysis.class, 1.5f);
				if (ch == Dungeon.hero){
					Statistics.qualifiedForBossChallengeBadge = false;
					Statistics.bossScores[2] -= 200;
				}
			}
			
		}

		Ballistica b = new Ballistica(this.pos, stopCell, Ballistica.STOP_SOLID);
		for (int p : b.subPath(0, Dungeon.level.distance(this.pos, stopCell))){
			CellEmitter.get(p).start(Speck.factory(Speck.JET), 0.05f, 10);
			CellEmitter.get(p).start(RainbowParticle.BURST, 0.05f, 10);
            Char ch = Actor.findChar(p);
            if (ch != null && ch != this) {
                affected.add(ch);
            }
            if (Dungeon.level.flamable[p]) {
                Dungeon.level.destroy(p);
                GameScene.updateMap(p);
                terrainAffected = true;
            }
		}
        if (terrainAffected) {
            Dungeon.observe();
        }
        for (Char ch : affected) {
			Buff.affect(ch, Bleeding.class).set(5);
			if (ch.alignment != this.alignment){
				if (ch == Dungeon.hero){
					Statistics.qualifiedForBossChallengeBadge = false;
					Statistics.bossScores[2] -= 400;
				}
            	ch.damage(Random.NormalIntRange(10, 20), new Hakkero());
			} else {
				ch.damage(Random.NormalIntRange(5, 10), new Hakkero());
			}
            if (Dungeon.level.heroFOV[pos]) {
                ch.sprite.flash();
                CellEmitter.center(pos).burst(PurpleParticle.BURST, Random.IntRange(1, 2));
            }
            if (!ch.isAlive() && ch == Dungeon.hero) {
                Badges.validateDeathFromEnemyMagic();
                Dungeon.fail(getClass());
                GLog.n(Messages.get(Char.class, "kill", name()));
            }
        }

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
		this.move( stopCell);
        this.moveSprite(this.pos, stopCell);
		stopCell = 69;
        return true;
    }

	private ArrayList<Integer> targetedCells_MS = new ArrayList<>();
	private ArrayList<Integer> stopCells_MS = new ArrayList<>();


	private int ms_getphase(){
		switch(this.phase){
			case 1:
			default:
				return Ballistica.STOP_SOLID;
			case 2:
				return Ballistica.MASTERSPARK;
			case 3:
				return Ballistica.WONT_STOP;
		}
	}

    private boolean masterspark_ready(){
		Dungeon.hero.interrupt();
		this.sprite.add(CharSprite.State.CHARGING);
		for (int i : PathFinder.NEIGHBOURS8){
			if (!(Actor.findChar(this.pos+i) instanceof MarisaBoss)){
				Ballistica p = new Ballistica(this.pos, Dungeon.hero.pos + i, ms_getphase());
				stopCells_MS.add(p.collisionPos);
				for(int j : p.subPath(0, Dungeon.level.distance(this.pos, p.collisionPos + ( (this.phase == 2) ? 1 : 0)))){
					targetedCells_MS.add(j);
					sprite.parent.add(new TargetedCell(j, 0xFF0000));
				}
			}
		}
        GLog.w(Messages.get(MarisaBoss.class, "tohakkero"));
        return true;
    }

	private boolean masterspark_ready_2(){
		Dungeon.hero.interrupt();
		for (int p : targetedCells_MS){
            sprite.parent.add(new TargetedCell(p, 0xFF0000));
        }
		return true;
	}

	private boolean masterspark() {
		this.sprite.remove(CharSprite.State.CHARGING);
		Sample.INSTANCE.play(Assets.Sounds.MASTERSPARK, 1f, 1f);
        HashSet<Char> affected = new HashSet<>();
        boolean terrainAffected = false;

		Camera.main.shake( phase, 1f );
		Sample.INSTANCE.play( Assets.Sounds.ROCKS );
		Sample.INSTANCE.play( Assets.Sounds.BLAST );
		for (int p : stopCells_MS) {
			sprite.parent.add(new MasterSparkBig.BigMasterSpark(DungeonTilemap.raisedTileCenterToWorld( this.pos), DungeonTilemap.raisedTileCenterToWorld(p), 1.5f));
		}

        for (int p : targetedCells_MS) {
			CellEmitter.center(p).burst( RainbowParticle.BURST, Random.IntRange( phase, 4) );
            Char ch = Actor.findChar(p);
            if (ch != null && !(ch instanceof MarisaBoss)) {
                affected.add(ch);
            }
            if (Dungeon.level.flamable[p]) {
                Dungeon.level.destroy(p);
                GameScene.updateMap(p);
                terrainAffected = true;
            }
			if (p != this.pos && this.phase > 2){
				GameScene.add( Blob.seed( p, phase, Fire.class ) );
			}
			
        }
        if (terrainAffected) {
            Dungeon.observe();
        }
        for (Char ch : affected) {
			Buff.affect(ch, Blindness.class, 2f);
			Buff.affect(ch, Cripple.class, 2f);
			Buff.affect(ch, Vertigo.class, 2f);
			if (ch.alignment != this.alignment){
            	ch.damage(Random.NormalIntRange(20, 35), new Hakkero());
				if (ch == Dungeon.hero){
					Statistics.qualifiedForBossChallengeBadge = false;
					Statistics.bossScores[2] -= 400;
				}
			} else {
				ch.damage(Random.NormalIntRange(5, 10), new Hakkero());
			}
            if (Dungeon.level.heroFOV[pos]) {
                ch.sprite.flash();
                CellEmitter.center(pos).burst(PurpleParticle.BURST, Random.IntRange(1, 2));
            }
            if (!ch.isAlive() && ch == Dungeon.hero) {
                Badges.validateDeathFromEnemyMagic();
                Dungeon.fail(getClass());
                GLog.n(Messages.get(Char.class, "kill", name()));
            }
        }
		stopCells_MS.clear();
        targetedCells_MS.clear();
        return true;
    }


	private static final String DASH_COOLDOWN     = "dash_cd";
	private static final String MS_COOLDOWN     = "masterspark_cd";
	private static final String DASH_TARGETED_CELLS     = "dash_targeted_cells";
	private static final String MS_TARGETED_CELLS     = "masterspark_targeted_cells";
	private static final String MS_STOP_CELLS     = "masterspark_stop_cells";
	private static final String CHARING_SKILL     = "charging_skill";
	private static final String STOP_CELL     = "stop_Cell";
	private static final String MARISA_PHASE     = "marisa_phase";
	private static final String TELE_COUNT     = "telecount";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( CHARING_SKILL, charging_skill );
		bundle.put( DASH_COOLDOWN, dash_cd );
		bundle.put( MS_COOLDOWN, masterspark_cd );
		bundle.put( STOP_CELL, stopCell);
		bundle.put( MARISA_PHASE, phase);
		bundle.put( TELE_COUNT, count_before_tele);
		int[] bundleArr = new int[targetedCells_MS.size()];
		for (int i = 0; i < targetedCells_MS.size(); i++){
			bundleArr[i] = targetedCells_MS.get(i);
		}
		bundle.put(MS_TARGETED_CELLS, bundleArr);

		int[] bundleArr_2 = new int[stopCells_MS.size()];
		for (int i = 0; i < stopCells_MS.size(); i++){
			bundleArr_2[i] = stopCells_MS.get(i);
		}
		bundle.put(MS_STOP_CELLS, bundleArr_2);

		// int[] bundleArr_3 = new int[targetedCells.size()];
		// for (int i = 0; i < targetedCells.size(); i++){
		// 	bundleArr_3[i] = targetedCells.get(i);
		// }
		// bundle.put(DASH_TARGETED_CELLS, bundleArr_3);
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		BossHealthBar.assignBoss(this);
		charging_skill = bundle.getBoolean( CHARING_SKILL );
		dash_cd = bundle.getInt( DASH_COOLDOWN );
		masterspark_cd = bundle.getInt( MS_COOLDOWN );
		stopCell = bundle.getInt( STOP_CELL);
		phase = bundle.getInt(MARISA_PHASE);
		count_before_tele = bundle.getInt(TELE_COUNT);

		for (int i : bundle.getIntArray(MS_STOP_CELLS)){
			stopCells_MS.add(i);
		}

		for (int i : bundle.getIntArray(MS_TARGETED_CELLS)){
			targetedCells_MS.add(i);
		}

		// for (int i : bundle.getIntArray(DASH_TARGETED_CELLS)){
		// 	targetedCells.add(i);
		// }
	}

	@Override
	public String description() {
		String descript = super.description();
		if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)){
			descript = descript + "\n\n_Badder Bosses:_\n" + Messages.get(this, "stronger_bosses");
		}
		return descript;
	}
}
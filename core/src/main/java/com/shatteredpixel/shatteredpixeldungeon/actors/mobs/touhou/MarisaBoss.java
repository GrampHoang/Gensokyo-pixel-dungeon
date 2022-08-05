/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2021 Evan Debenham
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
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Hakkero;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.FrostBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.*;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.sprites.*;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MarisaBossSprite;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ConfusionGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.effects.MasterSparkBig;
import com.shatteredpixel.shatteredpixeldungeon.effects.MasterSpark;
import com.watabou.noosa.Camera;
import com.watabou.utils.Bundle;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class MarisaBoss extends Mob {

	{
		spriteClass = MarisaBossSprite.class;

		HP = HT = Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 325 : 250;

		defenseSkill = 30;

		EXP = 40;

		state = WANDERING;
		HUNTING = new Hunting();
		viewDistance = 20;

		properties.add(Property.BOSS);
		immunities.add(Paralysis.class);
		immunities.add( Slow.class );
	}

	
	private static int DASH_CD = 8;
	private int MS_CD = 16;
	private boolean charging_skill = false; 
	private int dash_cd = DASH_CD - 3;
	private int masterspark_cd = MS_CD - 3;
	
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
		
		int hpBracket = HT / 3;
		
		int beforeHitHP = HP;
		super.damage(dmg, src);
		dmg = beforeHitHP - HP;
		
		// cannot be hit through multiple brackets at a time
		if ((beforeHitHP/hpBracket - HP/hpBracket) >= 2){
			HP = hpBracket * ((beforeHitHP/hpBracket)-1) + 1;
		}
		// if (beforeHitHP / hpBracket != HP / hpBracket) {
		// 	shootTheFloor();
		// }
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(2, 8);
	}

	@Override
	protected boolean canAttack( Char enemy ) {
		Ballistica attack = new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE);
		return attack.collisionPos == enemy.pos;
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
		Dungeon.level.unseal();
		GameScene.bossSlain();
	}

	@Override
	public int attackProc(Char hero, int damage) {
		damage = super.attackProc(enemy, damage);
		if (hero instanceof Hero) {
			// Buff.prolong(enemy, Blindness.class, 0.9f);
			return damage;
		}
		return damage;
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
			GLog.w("%1d,     %2d",dash_cd, masterspark_cd);
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
            return dash();
        }
        return false;
		
		// } else {
		// 	return sts_cd(this);
		// }
	}

    private ArrayList<Integer> targetedCells = new ArrayList<>();
    private int stopCell;

    private boolean dash_ready(){
		targetedCells.clear();
		CellEmitter.center(this.pos).burst(RainbowParticle.BURST, 20);
        Ballistica b = new Ballistica(this.pos, Dungeon.hero.pos, Ballistica.MASTERSPARK);

        for (int p : b.subPath(0, Dungeon.level.distance(this.pos, b.collisionPos))){
            sprite.parent.add(new TargetedCell(p, 0xFF0000));
            targetedCells.add(p);
        }
        stopCell = b.collisionPos;
        GLog.w("Marisa is about to dash, get out of her way!");
        return true;
    }

    private boolean dash() {
        HashSet<Char> affected = new HashSet<>();
        boolean terrainAffected = false;

        sprite.parent.add(new Beam.DeathRay(sprite.center(), DungeonTilemap.raisedTileCenterToWorld(stopCell)));
        this.move( stopCell);
        this.moveSprite(this.pos, stopCell);

		Camera.main.shake( 1f, 1f );
		Sample.INSTANCE.play( Assets.Sounds.ROCKS );
		CellEmitter.get(stopCell).start(Speck.factory(Speck.ROCK), 0.07f, 10);
		for (int p : PathFinder.NEIGHBOURS8) {
			CellEmitter.get(stopCell + p).start(Speck.factory(Speck.ROCK), 0.07f, 10);
			Char ch = Actor.findChar(stopCell + p);
			if(ch != null){
				Buff.affect(ch, Paralysis.class, 1.5f);
			}
		}
        for (int p : targetedCells) {
			CellEmitter.get(p).start(Speck.factory(Speck.JET), 0.05f, 10);
			CellEmitter.get(p).start(RainbowParticle.BURST, 0.05f, 10);
            Char ch = Actor.findChar(p);
            if (ch != null && (ch.alignment != this.alignment)) {
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
            ch.damage(Random.NormalIntRange(10, 20), new Hakkero());
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
        return true;
    }

	private ArrayList<Integer> targetedCells_MS = new ArrayList<>();
	private ArrayList<Integer> stopCells_MS = new ArrayList<>();

    private boolean masterspark_ready(){
		this.sprite.add(CharSprite.State.CHARGING);
		for (int i : PathFinder.NEIGHBOURS8){
			if (!(Actor.findChar(this.pos+i) instanceof MarisaBoss)){
				Ballistica p = new Ballistica(this.pos, Dungeon.hero.pos + i, Ballistica.MASTERSPARK);
				stopCells_MS.add(p.collisionPos);
				for(int j : p.subPath(0, Dungeon.level.distance(this.pos, p.collisionPos))){
					targetedCells_MS.add(j);
					sprite.parent.add(new TargetedCell(j, 0xFF0000));
				}
			}
		}
        GLog.w("Marisa is charging her Hakkero, be careful!");
        return true;
    }

	private boolean masterspark_ready_2(){
		for (int p : targetedCells_MS){
            sprite.parent.add(new TargetedCell(p, 0xFF0000));
        }
		return true;
	}

	private boolean masterspark() {
		this.sprite.remove(CharSprite.State.CHARGING);
		Buff.affect(this, Paralysis.class, 4f);

        HashSet<Char> affected = new HashSet<>();
        boolean terrainAffected = false;

		Camera.main.shake( 3f, 1f );
		Sample.INSTANCE.play( Assets.Sounds.ROCKS );
		Sample.INSTANCE.play( Assets.Sounds.BLAST );
		for (int p : stopCells_MS) {
			sprite.parent.add(new MasterSparkBig.BigMasterSpark(DungeonTilemap.raisedTileCenterToWorld( this.pos), DungeonTilemap.raisedTileCenterToWorld(p), 1.5f));
		}

        for (int p : targetedCells_MS) {
			CellEmitter.center(p).burst( RainbowParticle.BURST, Random.IntRange( 2, 4) );
            Char ch = Actor.findChar(p);
            if (ch != null && (ch.alignment != this.alignment) && !(ch instanceof MarisaBoss)) {
                affected.add(ch);
            }
            if (Dungeon.level.flamable[p]) {
                Dungeon.level.destroy(p);
                GameScene.updateMap(p);
                terrainAffected = true;
            }
			if (p != this.pos){
				GameScene.add( Blob.seed( p, 2, Fire.class ) );
			}
			
        }
        if (terrainAffected) {
            Dungeon.observe();
        }
        for (Char ch : affected) {
			Buff.affect(ch, Blindness.class, 2f);
			Buff.affect(ch, Cripple.class, 2f);
			Buff.affect(ch, Vertigo.class, 2f);
            ch.damage(Random.NormalIntRange(20, 35), new Hakkero());
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


	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( CHARING_SKILL, charging_skill );
		bundle.put( DASH_COOLDOWN, dash_cd );
		bundle.put( MS_COOLDOWN, masterspark_cd );
		bundle.put( STOP_CELL, stopCell);
		
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

		int[] bundleArr_3 = new int[targetedCells.size()];
		for (int i = 0; i < targetedCells.size(); i++){
			bundleArr_3[i] = targetedCells.get(i);
		}
		bundle.put(DASH_TARGETED_CELLS, bundleArr_3);
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		charging_skill = bundle.getBoolean( CHARING_SKILL );
		dash_cd = bundle.getInt( DASH_COOLDOWN );
		masterspark_cd = bundle.getInt( MS_COOLDOWN );
		stopCell = bundle.getInt( STOP_CELL);
		for (int i : bundle.getIntArray(DASH_TARGETED_CELLS)){
			targetedCells_MS.add(i);
		}

		for (int i : bundle.getIntArray(MS_STOP_CELLS)){
			stopCells_MS.add(i);
		}

		for (int i : bundle.getIntArray(MS_TARGETED_CELLS)){
			targetedCells_MS.add(i);
		}

		for (int i : bundle.getIntArray(DASH_TARGETED_CELLS)){
			targetedCells.add(i);
		}
	}
}
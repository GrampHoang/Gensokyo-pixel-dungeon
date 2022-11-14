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
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
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

public class ReimuBoss extends Mob {

	{
		spriteClass = ReimuBossSprite.class;

		HP = HT = Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 32500 : 25000;

		defenseSkill = 30;

		EXP = 40;

		state = WANDERING;
		HUNTING = new Hunting();
		viewDistance = 20;

		properties.add(Property.BOSS);
		immunities.add(Paralysis.class);
		immunities.add( Slow.class );
	}

	private static int MARRED_CD = 9;
    private static int SPREAD_CD = 12;
    private static int ORBS_CD = 15;

    private static int marred_cd = MARRED_CD-2;
    private static int spread_cd = SPREAD_CD-2;
    private static int orbs_cd = ORBS_CD-2;
	
	private static boolean charging_skill = false;
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
		return enemySeen;
		// Ballistica attack = new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE);
		// return attack.collisionPos == enemy.pos;
	}

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
	public int attackProc(Char enemy, int damage) {
		damage = super.attackProc(enemy, damage);
		if (enemy instanceof Hero) {
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
			GLog.w("%1d,     %2d,           %3d",marred_cd, orbs_cd, spread_cd);
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
					aggro(enemy);
				} else {
					sprite.showLost();
					charging_skill = false;
					state = WANDERING;
					return true;
				}
				
				//if not charmed, attempt to use an ability, even if the enemy can't be seen
				if (canUseReady()){
					return useReady();
				}
				if (canUseAbility()){
					return useAbility();
				}
				
				spend( TICK );
				return true;
				
			}
		}
	}


	//SKILLLLLLLLL

	public boolean canUseReady(){
		if ((orbs_cd < 3 && orbs_cd > 0)){
			return true;
		} else {
			return false;
		}
	}

	public boolean useReady(){
		spend(TICK);
		charging_skill = true;
		if (orbs_cd == 2){
			orbs_cd--;
			marred_cd++;
			spread_cd++;
			return orbs_ready_1();
		} else if (orbs_cd == 1) {
			orbs_cd--;
			marred_cd++;
			spread_cd++;
			return orbs_ready_2();
		}
		return false;
	}

	public boolean canUseAbility(){
		if (orbs_cd < 1 || marred_cd < 1 || spread_cd < 1){
			return true;
		} else {
			orbs_cd--;
			marred_cd--;
            spread_cd--;
			return false;
		} 
	}
	
	public boolean useAbility(){
		spend(TICK);
		charging_skill = false;
		if (orbs_cd < 1){
			orbs_cd = ORBS_CD;
            return orbs();
        } 
		else if (marred_cd < 1){
			marred_cd = MARRED_CD;
            return marred();
        }
        else if (spread_cd < 1){
			spread_cd = SPREAD_CD;
            return spread();
        }
        return false;
		
		// } else {
		// 	return sts_cd(this);
		// }
	}

    private ArrayList<Integer> targetedCells = new ArrayList<>();

    
    public boolean marred(){
        Char enemy = this.enemy;
        WandOfBlastWave.BlastWave.blast(enemy.pos);
		CellEmitter.center(enemy.pos).burst(SnowParticle.FACTORY, 20);
		CellEmitter.center(enemy.pos).burst(WoolParticle.FACTORY, 20);
        Buff.affect(enemy, Roots.class, 1f);
        Buff.affect(enemy, Blindness.class, 4f);
        Buff.affect(enemy, Degrade.class, 4f);
        return true;
    }

    public boolean spread(){
		Char enemy = this.enemy;
		for (int j  : PathFinder.NEIGHBOURS8){
			if(Random.Int(3) > 1){
				WandOfBlastWave.BlastWave.blast(enemy.pos + j);
				Ballistica trajectory = new Ballistica(enemy.pos, pos+j, Ballistica.MAGIC_BOLT);
				int strength = 1 + Math.round(Random.Int(6) / 2f);
				WandOfBlastWave.throwChar(enemy, trajectory, strength, false, true, getClass());
			}
		}
        return true;
    }

    public boolean orbs_ready_1(){
        targetedCells.clear();
        for (int i : PathFinder.NEIGHBOURS8){
            int t = pos + i;
            sprite.parent.add(new TargetedCell(t, 0xFF0000));
            // targetedCells.add(t);
        }
        return true;
    }
    public boolean orbs_ready_2(){
        for (int i : PathFinder.NEIGHBOURS8){
			Ballistica b = new Ballistica(this.pos, this.pos+i, Ballistica.STOP_SOLID);
			for (int p : b.subPath(1, Dungeon.level.distance(this.pos, b.collisionPos))){
				sprite.parent.add(new TargetedCell(p, 0xFF0000));
				targetedCells.add(p);
			}
        }
        for (int i : PathFinder.NEIGHBOURS_HORSEMOVES){
            Ballistica b = new Ballistica(this.pos, this.pos+i, Ballistica.STOP_SOLID);
			for (int p : b.subPath(1, Dungeon.level.distance(this.pos, b.collisionPos))){
				sprite.parent.add(new TargetedCell(p, 0xFF0000));
				targetedCells.add(p);
			}
        }
        return true;
    }

    public boolean orbs(){
        Camera.main.shake( 2f, 0.5f );
        Sample.INSTANCE.play( Assets.Sounds.ROCKS );
        for (int i :targetedCells){
            CellEmitter.get(i).start(Speck.factory(Speck.ROCK), 0.07f, 10);
			CellEmitter.get(i).start(Speck.factory(Speck.STEAM), 0.07f, 10);
			CellEmitter.center(enemy.pos).burst(EarthParticle.FACTORY, 20);
			Char ch = Actor.findChar(i);
			if(ch != null){
				Buff.affect(ch, Paralysis.class, 4f);
                ch.damage(Random.IntRange(10,25), this);
	}
        }
        return true;
    }	

	private static final String MARRED_COOLDOWN     = "marred_cd";
	private static final String SPREAD_COOLDOWN     = "spread_cd";
	private static final String ORBS_COOLDOWN     = "orbs_cd";
	private static final String REIMU_TARGETED_CELLS     = "reimu_targeted_cells";
	private static final String CHARING_SKILL     = "charging_skill";
	// private static final String STOP_CELL     = "stop_Cell";


	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( CHARING_SKILL, charging_skill );
		bundle.put( MARRED_COOLDOWN, marred_cd );
		bundle.put( SPREAD_COOLDOWN, spread_cd );
		bundle.put( ORBS_COOLDOWN, orbs_cd);
		
		int[] bundleArr = new int[targetedCells.size()];
		for (int i = 0; i < targetedCells.size(); i++){
			bundleArr[i] = targetedCells.get(i);
		}
        bundle.put(REIMU_TARGETED_CELLS, bundleArr);
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		charging_skill = bundle.getBoolean( CHARING_SKILL );
		marred_cd = bundle.getInt( MARRED_COOLDOWN );
		spread_cd = bundle.getInt( SPREAD_COOLDOWN );
		orbs_cd = bundle.getInt( ORBS_COOLDOWN );
		// stopCell = bundle.getInt( STOP_CELL);

		for (int i : bundle.getIntArray(REIMU_TARGETED_CELLS)){
			targetedCells.add(i);
		}
	}
}
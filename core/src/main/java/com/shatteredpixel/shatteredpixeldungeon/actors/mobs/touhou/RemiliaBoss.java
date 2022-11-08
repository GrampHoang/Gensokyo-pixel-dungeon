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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Sheep;
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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave.BlastWave;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ExplosiveTrap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.TengusMask;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.*;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.sprites.*;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RemiliaSprite;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SmokeScreen;
import com.watabou.noosa.Camera;
import com.watabou.utils.Bundle;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class RemiliaBoss extends Mob {

	{
		spriteClass = RemiliaSprite.class;

		HP = HT = Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 250 : 200;

		defenseSkill = 15;
        flying = true;
		EXP = 20;

		state = WANDERING;
		HUNTING = new Hunting();
		viewDistance = 20;

		properties.add(Property.BOSS);
		properties.add(Property.DEMONIC);
		immunities.add(Poison.class);
		immunities.add(Fire.class);
		immunities.add(Drowsy.class);
	}
	private int middle_of_map = 8*17+8;
	private int debug_summon_pos = 1*17+8;
	
	private final int LEVATIN_CD = (Dungeon.isChallenged(Challenges.LUNATIC) ? 14 : 25);

	private int levatin_cd = LEVATIN_CD;
	// private int levatin_pos = 0;
	private int levatin_stop_pos = 0;
	private boolean levatin_throw = false;

	private ArrayList<Integer> levatinCells = new ArrayList<>();
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
		return Random.NormalIntRange(4, 10);
	}

	@Override
	public int attackSkill(Char target) {
		return 15;
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

		if(HP <= 0){
			return;
		}
		if (beforeHitHP / hpBracket != HP / hpBracket) {
			callSakuya(debug_summon_pos);
		}
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 5);
	}

	@Override
	public boolean act() {
		if(Dungeon.isChallenged(Challenges.STRONGER_BOSSES)){
			GameScene.add(Blob.seed(this.pos, 3, Fire.class));
		}
		return super.act();
	}
	
	private HashSet<Mob> getSubjects(){
		HashSet<Mob> subjects = new HashSet<>();
		for (Mob m : Dungeon.level.mobs){
			if (m.alignment == alignment && (m instanceof MaidSakuya)){
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

		if (Dungeon.hero.subClass == HeroSubClass.NONE) {
			Dungeon.level.drop( new TengusMask(), pos ).sprite.drop();
		}

		for (Mob m : getSubjects()){
			m.die(null);
		}

		Statistics.bossScores[1] += 2000;
		super.die( cause );
	}

	@Override
	public int attackProc(Char hero, int damage) {
		damage = super.attackProc(enemy, damage);
		if (hero instanceof Hero) {
			Buff.affect(enemy, Bleeding.class).set(2f);
			return damage;
		}
		if (this.HP < this. HT){
			this.HP++;
		}
		return damage;
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


	public void callSakuya(int summonPos) {
		//Make sure mid map is empty
		Char block = Actor.findChar(middle_of_map);
		if(block != null && !(block instanceof RemiliaBoss)){
			block.move(15*17+8);
			block.sprite.move( this.pos, 15*17+8 );
		}
		//Jump to mid map
		if (Dungeon.level.heroFOV[this.pos]) CellEmitter.get( this.pos ).burst( Speck.factory( Speck.WOOL ), 6 );
		GameScene.add( Blob.seed( this.pos, 5, SmokeScreen.class ) );

		if (Dungeon.level.heroFOV[this.pos]) CellEmitter.get( middle_of_map ).burst( Speck.factory( Speck.WOOL ), 6 );
		sprite.move( this.pos, middle_of_map );
		move( middle_of_map );
		if (Dungeon.level.heroFOV[debug_summon_pos]) CellEmitter.get( debug_summon_pos ).burst( Speck.factory( Speck.WOOL ), 6 );

		//Release Smoke and set levatin_cd to 6
		GameScene.add( Blob.seed( this.pos, 200, SmokeScreen.class ) );
		levatin_cd = 6;

		//Summon Sakuya and deliver cake
		if (Actor.findChar(summonPos) instanceof Sheep){
			Actor.findChar(summonPos).die(null);
		}

		if (Actor.findChar(summonPos) == null) {
			MaidSakuya m = new MaidSakuya();
			m.pos = summonPos;
			GameScene.add(m);
			Dungeon.level.occupyCell(m);
			m.deliverCake(this.pos);
		} else {
			Char ch = Actor.findChar(summonPos);
			CellEmitter.center(summonPos).burst(BlastParticle.FACTORY, 20);
			ch.damage(Random.NormalIntRange(20, 30), target);
			if (!ch.isAlive() && ch == Dungeon.hero) {
				Dungeon.fail(RemiliaBoss.class);
			}
		}

		spend(TICK);
	}

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
				
				if (enemyInFOV || levatin_throw) {
					target = Dungeon.hero.pos;
					aggro(enemy);
				} else {
					sprite.showLost();
					levatin_throw = false;
					//is this even helpful at all?
					levatin_cd += 2; 
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
				int oldPos = pos;
				if (getCloser(enemy.pos)){
					spend(1/speed());
					return moveSprite( oldPos,  pos );
				}
				spend( TICK );
				return true;
				
			}
		}
	}


	//SKILLLLLLLLL

	public boolean canUseReady(){
		if ((levatin_cd == 5 && this.HP < this.HT/3) || (levatin_cd == 3 && this.HP < this.HT/3*2) || (levatin_cd == 1)){ 
			Ballistica attack = new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE);
			if (attack.collisionPos != enemy.pos){
				//Make sure she can throw levatin
				return false;
			}
			levatin_throw = true;
			levatin_cd--;
			if (levatin_cd == 0){
				releaseSmoke();
				levatin_cd = LEVATIN_CD+1;
				MarisaBoss summoner = new MarisaBoss();
				summoner.callHelp(debug_summon_pos, MaidSakuya.class);
			}
			return true;
		} else {
			levatin_cd--;
			return false;
		}
	}
	
	public boolean useReady(){
		Dungeon.hero.interrupt();
		spend(TICK);
		
		Camera.main.shake( 0.5f, 0.25f );
		BlastWave.blast(this.pos);
		Ballistica b = new Ballistica(this.pos, Dungeon.hero.pos, Ballistica.MASTERSPARK);
		// levatin_pos = Dungeon.hero.pos; This should act like throwing projectile but not sure how to lol
		levatin_stop_pos = b.collisionPos;
		for (int p : b.subPath(1, Dungeon.level.distance(this.pos, b.collisionPos))){
			levatinCells.add(p);
            sprite.parent.add(new TargetedCell(p, 0xFF0000));
        }
		for (int i : PathFinder.NEIGHBOURS8){
			CellEmitter.floor(pos+i).burst(PitfallParticle.FACTORY4, 8);
			Char ch = Actor.findChar(this.pos + i);
			if (ch != null && !(ch instanceof RemiliaBoss)){
				Buff.affect(ch, Slow.class, 0.5f);
				ch.damage(4, this);
			}
		}
		return true;
	}

	public boolean canUseAbility(){
		if (levatin_throw == true){
			return true;
		} else {
			return false;
		} 
	}
	
	public boolean useAbility(){
		spend(TICK);
		if (levatin_throw == true){
			levatin_throw = false;
            return throwLevatin();
        } 
        return false;
	}

    public boolean throwLevatin(){
		sprite.parent.add(new Beam.DeathRay(sprite.center(), DungeonTilemap.raisedTileCenterToWorld(levatin_stop_pos)));
        for (int i :levatinCells){
			CellEmitter.get(i).start(Speck.factory(Speck.STEAM), 0.07f, 10);
			if (Dungeon.isChallenged(Challenges.LUNATIC)){
				GameScene.add(Blob.seed(i, 60, Fire.class));
			}else {
				Blob.seed(i, 10, Fire.class);
			}
			Char ch = Actor.findChar(i);
			if(ch != null && (!(ch instanceof RemiliaBoss))){
				Buff.affect(ch, Paralysis.class, 1f);
				this.spend(TICK);
                ch.damage(Random.IntRange(8,16), this);

				Actor.addDelayed(new Pushing(ch, ch.pos, levatin_stop_pos), 0);
				
				ch.pos = levatin_stop_pos;
				Dungeon.level.occupyCell(ch);
			}
        }
		CellEmitter.center(levatin_stop_pos).burst(BlastParticle.FACTORY, 30);
		PathFinder.buildDistanceMap( levatin_stop_pos, BArray.not( Dungeon.level.solid, null ), 1 );
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] < Integer.MAX_VALUE) {
				// if (Dungeon.level.pit[i])
				// 	GameScene.add(Blob.seed(i, 1, Fire.class));
				// else 
				if (Dungeon.isChallenged(Challenges.LUNATIC))
					GameScene.add(Blob.seed(i, 60, Fire.class));
				else{
					GameScene.add(Blob.seed(i, 10, Fire.class));
				}
				CellEmitter.get(i).burst(FlameParticle.FACTORY, 5);
				CellEmitter.get(i).burst(SmokeParticle.FACTORY, 4);
				Char ch = Actor.findChar(i);
					if (ch != null && !(ch instanceof RemiliaBoss)) {
						ch.damage(8, this);
					}
			}
		}

		levatinCells.clear();
        return true;
    }	

	public void releaseSmoke(){
		Buff.affect(this, Invisibility.class, 5f);
		Sample.INSTANCE.play( Assets.Sounds.GAS );
		int centerVolume = 1;
		for (int i : PathFinder.NEIGHBOURS8){
			if (!Dungeon.level.solid[this.pos+i]){
				GameScene.add( Blob.seed( this.pos+i, 1, SmokeScreen.class ) );
			} else {
				centerVolume += 1;
			}
		}

		GameScene.add( Blob.seed( this.pos, centerVolume, SmokeScreen.class ) );
	}

	private static final String LEVATIN_COOLDOWN     = "levatin_cd";
	private static final String LEVATIN_CELLS     = "levatin_cells";
	private static final String LEVATIN_STOP_POS     = "levatin_stop_pos";
	private static final String LEVATIN_THROW		= "levatin_throw";


	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( LEVATIN_COOLDOWN, levatin_cd );
		bundle.put( LEVATIN_STOP_POS, levatin_stop_pos );
		bundle.put( LEVATIN_THROW, levatin_throw );

		int[] bundleArr = new int[levatinCells.size()];
		for (int i = 0; i < levatinCells.size(); i++){
			bundleArr[i] = levatinCells.get(i);
		}
        bundle.put(LEVATIN_CELLS, bundleArr);
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		levatin_cd = bundle.getInt( LEVATIN_COOLDOWN );
		levatin_stop_pos = bundle.getInt( LEVATIN_STOP_POS );
		levatin_throw = bundle.getBoolean( LEVATIN_THROW );

		for (int i : bundle.getIntArray(LEVATIN_CELLS)){
			levatinCells.add(i);
		}
	}

	public static class MaidSakuya extends Sakuya {
		
		{
			
			state = WANDERING;
			//no loot or exp
			maxLvl = -5;
			
			//20/25 health to start
			viewDistance = 0;
			HT = 10;
			HP = 10;
			immunities.add(Fire.class);
		}

		@Override
		public float spawningWeight() {
			return 0;
		}

		private void deliverCake(int remPos){
			beckon(remPos);
		}

		@Override
		public boolean act() {
			for (int i : PathFinder.NEIGHBOURS9){
				Char find = Char.findChar(this.pos + i);
				if (find != null){
					if (find instanceof RemiliaBoss){
						if(Dungeon.isChallenged(Challenges.LUNATIC)){
							Buff.affect(find, Stamina.class, 5f);
						}
						int healAmount = Math.round(30*(1-(find.HP/find.HT)));
						if(Dungeon.isChallenged(Challenges.STRONGER_BOSSES)){
							healAmount = 30;
						}
						find.HP += healAmount;
						find.sprite.emitter().start( Speck.factory( Speck.HEALING ), 0.4f, 1 );
						find.sprite.showStatus( CharSprite.POSITIVE, Integer.toString( healAmount ) );
						die(RemiliaBoss.class);
					}
				}
			}
			return super.act();
		}

		@Override
		public void die( Object cause ) {
			super.die( cause );
			if (Dungeon.level.heroFOV[this.pos]) CellEmitter.get( this.pos ).burst( Speck.factory( Speck.WOOL ), 6 );
		}
		
		@Override
		public float speed() {
			float speed = (isLunatic() ? 2 : 1);
			if ( buff( Cripple.class ) != null ) speed /= 2f;
			if ( buff( Stamina.class ) != null) speed *= 1.5f;
			if ( buff( Adrenaline.class ) != null) speed *= 2f;
			if ( buff( Haste.class ) != null) speed *= 3f;
			if ( buff( Exterminating.class ) != null) speed *= 1.2f;
			if ( buff( Dread.class ) != null) speed *= 2f;
			return speed;
		}
	}
}
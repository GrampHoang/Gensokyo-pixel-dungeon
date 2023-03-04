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
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave.BlastWave;
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
import com.watabou.utils.PathFinderUtils;
import com.watabou.utils.Random;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class TenshiBoss extends Mob {

	{
		spriteClass = RemiliaSprite.class;

		HP = HT = Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 450 : 350;

		defenseSkill = 22;
		EXP = 20;

		state = WANDERING;
		HUNTING = new Hunting();
		viewDistance = 20;

		properties.add(Property.BOSS);
		immunities.add(Fire.class);
	}

	private int HP_BRACKET = 150;	//every 40 damage the trigger happen
	private int bracket_count = 1;

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
		return Random.NormalIntRange(30, 50);
	}

	@Override
	public int attackSkill(Char target) {
		return 26;
	}

	@Override
	public void damage(int dmg, Object src) {
		if (!Dungeon.level.mobs.contains(this)){
			return;
		}
		// if ( Blob.volumeAt(this.pos, SmokeScreen.class) > 0){
		// 	sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "smoke"));
		// 	return;
		// }
		// int hpBracket = HT / 3;
		
		int beforeHitHP = HP;
		super.damage(dmg, src);
		dmg = beforeHitHP - HP;

		if(HP <= 0){
			die(src);
			return;
		}

		if (HP < HT - HP_BRACKET * bracket_count){
			bracket_count++;
			HP = HT - HP_BRACKET * bracket_count;
		}
		// // cannot be hit through multiple brackets at a time
		// if ((beforeHitHP/hpBracket - HP/hpBracket) >= 2){
		// 	HP = hpBracket * ((beforeHitHP/hpBracket)-1) + 1;
		// }
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(2, 8);
	}

	@Override
	public boolean act() {
		// if(Dungeon.isChallenged(Challenges.STRONGER_BOSSES)){
		// 	GameScene.add(Blob.seed(this.pos, 3, Fire.class));
		// }
		return super.act();
	}

	@Override
	public void die( Object cause ) {
		Dungeon.level.drop( new PotionOfExperience(), pos ).sprite.drop();
		Dungeon.level.unseal();
		GameScene.bossSlain();
		Statistics.bossScores[1] += 1000;
		super.die( cause );
	}

	//Will not attack hero directly
	@Override
	protected boolean canAttack( Char enemy ) {
		return !(enemy instanceof Hero);
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
	public void notice() {
		super.notice();
		if (!BossHealthBar.isAssigned()) {
			BossHealthBar.assignBoss(this);
		}
	}

	private class Hunting extends Mob.Hunting{
		
		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {
			if (enemyInFOV && !isCharmedBy( enemy ) && canAttack( enemy )) {

				if (canUseAbility()){
					return useAbility();
				}
				if (canUseReady()){
					return useReady();
				}
				
				return doAttack( enemy );
				
			} else {
				
				// sprite.showLost();
				// state = WANDERING;
				
				//if not charmed, attempt to use an ability, even if the enemy can't be seen
				if (canUseAbility()){
					return useAbility();
				}	
				if (canUseReady()){
					return useReady();
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

	//Rush (if too far) and slash (distance = 3)
	private final int SLASH_CD = 5;
	private int slash_cd = SLASH_CD;
	private final int DASH_CD = 10;
	private int dash_cd = DASH_CD;
	private final int LASER_CD = 12;
	private int laser_cd = LASER_CD;
	private final int SHOOT_CD = 15;
	private int shoot_cd = SHOOT_CD;

	public boolean canUseReady(){
		GLog.w(Integer.toString(slash_cd));
		if (slash_cd < 2){
			slash_cd--;
			return true;
		} else{
			slash_cd--;
			dash_cd--;
			laser_cd--;
			shoot_cd--;
			return false;
		}
	}
	
	public boolean useReady(){
		Dungeon.hero.interrupt();
		spend(TICK);
		slash_ready();
		return true;
	}

	public boolean canUseAbility(){
		if (slash_cd < 1){
			return true;
		} else {
			return false;
		} 
	}
	
	public boolean useAbility(){
		Dungeon.hero.interrupt();
		spend(TICK);
		if (slash_cd < 1){
			slash_cd = SLASH_CD;
            return slash();
        } 
        return false;
	}

	private boolean dash(Char enemy) {
		Ballistica b = new Ballistica(this.pos, enemy.pos, Ballistica.STOP_CHARS);
		for (int p : b.subPath(0, Dungeon.level.distance(this.pos, enemy.pos)-3)){
			BlastWave.blast(p);
            Char ch = Actor.findChar(p);
            if (ch != null && ch != this) {
				Buff.affect(ch, Bleeding.class).set(5);
            	ch.damage(Random.NormalIntRange(10, 20), this);
			}
        }
		//new CollisionPos is 1 tile shorter
		b.collisionPos = b.path.get(b.path.indexOf(b.collisionPos) - 2);
		this.move( b.collisionPos);
        this.moveSprite(this.pos, b.collisionPos);
        return true;
    }

	private int[] slashEnds = {1,2,3};
	private int[] basic = {1,2,3};
	public boolean slash_ready(){
		GLog.w("Slash_Ready");
		if(Dungeon.level.distance(this.pos, Dungeon.hero.pos) > 3){
			dash(Dungeon.hero);
		}
		
		Ballistica b = new Ballistica(this.pos, Dungeon.hero.pos, Ballistica.STOP_CHARS);
		int colliPos = b.path.get(b.path.indexOf(b.collisionPos) - 1);
		slashEnds = PathFinderUtils.perpendicular(PathFinder.CIRCLE8, colliPos, Dungeon.hero.pos);
		for (int i : slashEnds){
			sprite.parent.add(new TargetedCell(i, 0xFF0000));
		}
		//Debugray
		sprite.parent.add(new Beam.DeathRay(this.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(colliPos)));
		return true;
	}

    public boolean slash(){
		GLog.w("Slash");
		if (Arrays.equals(slashEnds, basic)){
			GLog.w("No");
			return true;
		}
		for (int i : slashEnds){
			Ballistica slash = new Ballistica(this.pos, i, Ballistica.STOP_TARGET);
			sprite.parent.add(new Beam.DeathRay(this.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(i)));
			for (int p : slash.subPath(0, Dungeon.level.distance(this.pos, slash.collisionPos))){
				CellEmitter.get(p).burst(SmokeParticle.FACTORY, 10);
				GameScene.add(Blob.seed(this.pos, 6, Fire.class));
				Char ch = Actor.findChar(p);
				if(ch != null && ch != this){
					ch.damage(Math.max(ch.HP/4, 15), this);
					Buff.affect(ch, Burning.class).reignite(ch, 5f);
				}
			}
		}
		slashEnds = basic;
        return true;
    }	

	private static final String DASH     = "dash_cd";
	private static final String SLASH     = "slash_cd";
	// private static final String LEVATIN_STOP_POS     = "levatin_stop_pos";
	// private static final String LEVATIN_THROW		= "levatin_throw";
	private static final String BRA_COUNT		= "bracket";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( DASH, dash_cd );
		bundle.put( SLASH, slash_cd );
		// bundle.put( LEVATIN_THROW, levatin_throw );
		bundle.put( BRA_COUNT, bracket_count );
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		dash_cd = bundle.getInt( DASH );
		slash_cd = bundle.getInt( SLASH );
		bracket_count = bundle.getInt( BRA_COUNT );
		BossHealthBar.assignBoss(this);
	}

	@Override
	public String description() {
		String descript = super.description();
		if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)){
			descript = descript + "\n\n_Badder Bosses:\n" + Messages.get(this, "stronger_bosses");
		}
		return descript;
	}
}
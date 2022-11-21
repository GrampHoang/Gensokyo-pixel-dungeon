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
import com.shatteredpixel.shatteredpixeldungeon.levels.YukariBossLevel;
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
import com.shatteredpixel.shatteredpixeldungeon.sprites.YukariSprite;
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

public class YukariBoss extends Mob {

	{
		spriteClass = YukariSprite.class;

		HP = HT = Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 400 : 300;

		defenseSkill = 15;
        flying = true;
		EXP = 30;

		viewDistance = 12;

		properties.add(Property.BOSS);
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
		
		int hpBracket = HT / 4;
		
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
			summonGap();
		}
	}
	
	private HashSet<Mob> getSubjects(){
		HashSet<Mob> subjects = new HashSet<>();
		for (Mob m : Dungeon.level.mobs){
			if (m.alignment == alignment && (m instanceof Chen || m instanceof Ran)){
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
			m.die(null);
		}

		Statistics.bossScores[2] += 2000;
		super.die( cause );
	}

	@Override
	public int attackProc(Char hero, int damage) {
		damage = super.attackProc(enemy, damage);
		// if (hero instanceof Hero) {
		// 	Buff.affect(enemy, Bleeding.class).set(2f);
		// 	return damage;
		// }
		// if (this.HP < this. HT){
		// 	this.HP++;
		// }
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


	public void summonGap() {
        int tried = 0;
        // Lunatic mode spawn 2/3/4 gap, normal spawn 1/2/3
        int summon = (isLunatic() ? -1 : 0);
        // will try to spawn 1/1/2 gaps 
        while (tried < 50 || summon < (4 - this.HP*4/this.HT)){
            tried++;
            int sum_pos = YukariBossLevel.gapPositions[Random.IntRange(0, YukariBossLevel.gapPositions.length - 1)];
            Char ch = Actor.findChar(sum_pos);
            if(ch != null){
                if(ch instanceof Hero && ch.HP != 1){
                    //lose half max HP or reduce HP to 1
                    // if already 1 HP then ignore, else it would be a watse gap
                    Char hero = Dungeon.hero;
                    Dungeon.hero.damage(Math.min(hero.HP - 1, hero.HT/2), this);

                } else if(!(ch instanceof YukariGap || ch instanceof YukariBoss)){
                    //if a mob block spawning, kill it
                    ch.die(null);
                    YukariGap gap = new YukariGap();
                    gap.pos = sum_pos;
                    GameScene.add( gap, 1 );
                    Dungeon.level.occupyCell(gap);
                    summon++;
                }   //else if it's Yukari or the Gap, try again
            }
        }
		spend(TICK);
	}

    @Override
    public boolean act() {
        if (!isCharmedBy( enemy ) && canAttack( enemy )) {

            if (canUseReady()){
                return useReady();
            }
            if (canUseAbility()){
                return useAbility();
            }
            return doAttack( enemy );
            
        } else {
            
            if (enemySeen) {
                target = Dungeon.hero.pos;
                aggro(enemy);
            } else {
                sprite.showLost();
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

	//SKILLLLLLLLL

	public boolean canUseReady(){
		return false;
	}
	
	public boolean useReady(){
		Dungeon.hero.interrupt();
		spend(TICK);
		return true;
	}

	public boolean canUseAbility(){
		return false;
	}
	
	public boolean useAbility(){
		spend(TICK);
        return false;
	}


	// private static final String LEVATIN_COOLDOWN     = "levatin_cd";
	// private static final String LEVATIN_CELLS     = "levatin_cells";
	// private static final String LEVATIN_STOP_POS     = "levatin_stop_pos";
	// private static final String LEVATIN_THROW		= "levatin_throw";


	// @Override
	// public void storeInBundle(Bundle bundle) {
	// 	super.storeInBundle(bundle);
	// 	bundle.put( LEVATIN_COOLDOWN, levatin_cd );
	// 	bundle.put( LEVATIN_STOP_POS, levatin_stop_pos );
	// 	bundle.put( LEVATIN_THROW, levatin_throw );

	// 	int[] bundleArr = new int[levatinCells.size()];
	// 	for (int i = 0; i < levatinCells.size(); i++){
	// 		bundleArr[i] = levatinCells.get(i);
	// 	}
    //     bundle.put(LEVATIN_CELLS, bundleArr);
	// }
	
	// @Override
	// public void restoreFromBundle(Bundle bundle) {
	// 	super.restoreFromBundle(bundle);
	// 	levatin_cd = bundle.getInt( LEVATIN_COOLDOWN );
	// 	levatin_stop_pos = bundle.getInt( LEVATIN_STOP_POS );
	// 	levatin_throw = bundle.getBoolean( LEVATIN_THROW );

	// 	for (int i : bundle.getIntArray(LEVATIN_CELLS)){
	// 		levatinCells.add(i);
	// 	}
	// }

}
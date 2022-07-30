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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.FrostBomb;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.sprites.*;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class CirnoBoss extends Mob {

	{
		spriteClass = TenguSprite.class;

		HP = HT = Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 120 : 80;

		defenseSkill = 6;

		EXP = 10;

		HUNTING = new Hunting();
		flying = true;
		viewDistance = 69;

		properties.add(Property.BOSS);
		properties.add(Property.IMMOVABLE);
		immunities.add( Chill.class );
		immunities.add( Frost.class );
	}

	private int bomb_cd = 5;
	private int storm_cd = 6;

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
		return Random.NormalIntRange(2, 5);
	}

	@Override
	public int attackSkill(Char target) {
		return 10;
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 2);
	}

	@Override
	protected boolean canAttack( Char enemy ) {
		return new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE).collisionPos == enemy.pos;
	}

	@Override
	public boolean act() {
		return super.act();
	}
	@Override
	public void die( Object cause ) {
		super.die( cause );
		Dungeon.level.drop( new PotionOfFrost(), pos ).sprite.drop();
		Dungeon.level.unseal();
		GameScene.bossSlain();
	}

	@Override
	public int attackProc(Char hero, int damage) {
		damage = super.attackProc(enemy, damage);
		if (hero instanceof Hero) {
			Buff.prolong(enemy, Chill.class, 2f);
			return damage;
		}
		return damage;
	}

	private class Hunting extends Mob.Hunting{
		
		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {
			
			enemySeen = enemyInFOV;
			if (enemyInFOV && !isCharmedBy( enemy ) && canAttack( enemy )) {
				
				if (canUseAbility()){
					return useAbility();
				}
				return doAttack( enemy );
				
			} else {
				
				if (enemyInFOV) {
					target = enemy.pos;
				} else {
					chooseEnemy();
					if (enemy == null){
						//if nothing else can be targeted, target hero
						enemy = Dungeon.hero;
					}
					target = enemy.pos;
				}
				
				//if not charmed, attempt to use an ability, even if the enemy can't be seen
				if (canUseAbility()){
					return useAbility();
				}
				
				spend( TICK );
				return true;
				
			}
		}
	}


	//SKILLLLLLLLL
	boolean near = false;
	ArrayList<Integer> storm_pos = new ArrayList<>();
	public boolean throwBomb(final Char thrower){
		for (int i : PathFinder.NEIGHBOURS8){
			int cell = thrower.pos + i;
				if (Dungeon.hero.pos == cell){
					near = true;
				}
		}
		for (int i : PathFinder.NEIGHBOURS8){
			FrostBomb bomb = new FrostBomb();
			bomb.onCirnoThrow(thrower.pos + i * (near ? Random.IntRange(1,2) : Random.IntRange(3,4)));
		}
		bomb_cd = 10;
		storm_cd = 6;
		return true;
	}

	public boolean stormWarn(final Char thrower){
		for (int i : PathFinder.NEIGHBOURS8){
			storm_pos.add(thrower.pos + i*(near ? Random.IntRange(3,4) : Random.IntRange(2,3)));
			sprite.parent.addToBack(new TargetedCell(thrower.pos + i*(near ? Random.IntRange(3,4) : Random.IntRange(2,3)), 0xFF0000));
		}
		return true;
	}

	public boolean storm(final Char thrower){
		for (int i : storm_pos){
			PotionOfLiquidFlame bomb = new PotionOfLiquidFlame();
			bomb.shatter(i);
		}
		near = false;
		bomb_cd = 7;
		storm_cd = 16;
		return true;
	}

	public boolean canUseAbility(){
		if (bomb_cd > 0 && storm_cd > 0){
			bomb_cd--;
			storm_cd--;
			return false;
		} else {
			return true;
		}
	}
	
	public boolean useAbility(){
		if(bomb_cd < 1){
			return throwBomb(this);
		} else if (storm_cd == 1){
			return stormWarn(this);
		} else {
			return storm(this);
		}
	}
}
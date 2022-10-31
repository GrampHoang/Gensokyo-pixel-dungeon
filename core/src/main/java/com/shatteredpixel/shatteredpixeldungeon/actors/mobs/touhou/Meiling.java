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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MonkSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EarthParticle;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.particles.Emitter;

import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Meiling extends Mob {

	{
		spriteClass = MonkSprite.class;

		HP = HT = 25;
		defenseSkill = 12;
		viewDistance = 8;

		EXP = 5;
        maxLvl = 10;

		HUNTING = new Hunting();

		baseSpeed = 1f;
	}

    private float rock_cd = 200;
	private float punch_cd = 5;
    // ArrayList<Integer> punch_pos = new ArrayList<>();
    // ArrayList<Integer> punch_pos_drop = new ArrayList<>();
    int[] punch_pos = new int[]{0,0,0,0,0,0};
    int[] punch_pos_drop = new int[]{0,0,0,0,0,0};
    public boolean mad(){
        return (HP*3 < HT); // Below 33% HP she will turn mad
    }

	@Override
	public int damageRoll() {
        if (mad()) {
            return Random.NormalIntRange( 4, 10 );
        }
        else {
		    return Random.NormalIntRange( 2, 8 );
        }
	}

	@Override
	public int attackSkill( Char target ) {
		return 12;
	}

	@Override
	public float attackDelay() {
        if (mad()) {
            return super.attackDelay();
        }
        else{
		    return super.attackDelay()*1.5f;
        }
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 2);
	}

    @Override
	public void damage(int dmg, Object src) {
		if ((HP*3 <= HT)){
			((MonkSprite)sprite).spray(true);
			yell(Messages.get(this, "Meiling is mad!"));
		}
		super.damage(dmg, src);
	}

	private static final String LAST_ENEMY_POS = "last_enemy_pos";
	private static final String LEAP_POS = "leap_pos";
	private static final String LEAP_CD = "leap_cd";
    private static final String PUNCH_POS = "punch_pos";
    private static final String PUNCH_POS_DROP = "punch_pos_drop";
    private static final String ROCK_CD = "rock_cd";
    private static final String PUNCH_CD = "punch_cd";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(LAST_ENEMY_POS, lastEnemyPos);
		bundle.put(LEAP_POS, leapPos);
		bundle.put(LEAP_CD, leapCooldown);
        bundle.put(ROCK_CD, rock_cd);
        bundle.put(PUNCH_CD, punch_cd);
        bundle.put(PUNCH_POS, punch_pos);
        bundle.put(PUNCH_POS_DROP, punch_pos_drop);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		lastEnemyPos = bundle.getInt(LAST_ENEMY_POS);
		leapPos = bundle.getInt(LEAP_POS);
		leapCooldown = bundle.getFloat(LEAP_CD);
        punch_pos = bundle.getIntArray(PUNCH_POS);
        punch_pos_drop = bundle.getIntArray(PUNCH_POS_DROP);
        punch_cd = bundle.getFloat(PUNCH_CD);
        rock_cd = bundle.getFloat(ROCK_CD);
	}

	private int lastEnemyPos = -1;

	@Override
	protected boolean act() {
        if (punch_pos_drop != null){
            for (int i : punch_pos_drop){
                if (i != 0){
                    Char ch = Actor.findChar(i);
                    if (ch != null && !(ch instanceof Meiling)){
                        Buff.prolong( ch, Paralysis.class, Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 4 : 2 );
                    }
                    Camera.main.shake( 1, 0.5f );
                    Sample.INSTANCE.play(Assets.Sounds.ROCKS);
                    CellEmitter.get( i ).start( Speck.factory( Speck.ROCK ), 0.07f, 10 );
                }
            }
            Arrays.fill(punch_pos_drop, 0);
        }
        
        if (punch_pos != null){
            int j = 0;
            for (int i : punch_pos){
                sprite.parent.addToBack(new TargetedCell(i, 0xFF0000));
                punch_pos_drop[j] = i;
                j++;
          }
          Arrays.fill(punch_pos, 0);
        }

		AiState lastState = state;
		boolean result = super.act();
		if (paralysed <= 0) leapCooldown --;

		//if state changed from wandering to hunting, we haven't acted yet, don't update.
		if (!(lastState == WANDERING && state == HUNTING)) {
			if (enemy != null) {
				lastEnemyPos = enemy.pos;
			} else {
				lastEnemyPos = Dungeon.hero.pos;
			}
		}

		return result;
	}

	private int leapPos = -1;
	private float leapCooldown = 0;

	public class Hunting extends Mob.Hunting {

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {

			if (leapPos != -1){

				leapCooldown = Random.NormalIntRange(2, 3);
				Ballistica b = new Ballistica(pos, leapPos, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID);

				//check if leap pos is not obstructed by terrain
				if (rooted || b.collisionPos != leapPos){
					leapPos = -1;
					return true;
				}

				final Char leapVictim = Actor.findChar(leapPos);
				final int endPos;

				//ensure there is somewhere to land after leaping
				if (leapVictim != null){
					int bouncepos = -1;
					for (int i : PathFinder.NEIGHBOURS8){
						if ((bouncepos == -1 || Dungeon.level.trueDistance(pos, leapPos+i) < Dungeon.level.trueDistance(pos, bouncepos))
								&& Actor.findChar(leapPos+i) == null && Dungeon.level.passable[leapPos+i]){
							bouncepos = leapPos+i;
						}
					}
					if (bouncepos == -1) {
						leapPos = -1;
						return true;
					} else {
						endPos = bouncepos;
					}
				} else {
					endPos = leapPos;
				}

				//do leap
				sprite.visible = Dungeon.level.heroFOV[pos] || Dungeon.level.heroFOV[leapPos] || Dungeon.level.heroFOV[endPos];
				sprite.jump(pos, leapPos, new Callback() {
					@Override
					public void call() {

						if (leapVictim != null && alignment != leapVictim.alignment){
							Buff.prolong(leapVictim, Paralysis.class, 1.5f);
							leapVictim.sprite.flash();
							Sample.INSTANCE.play(Assets.Sounds.HIT);
						}

						if (endPos != leapPos){
							Actor.addDelayed(new Pushing(Meiling.this, leapPos, endPos), -1);
						}

						pos = endPos;
						leapPos = -1;
						sprite.idle();
						Dungeon.level.occupyCell(Meiling.this);
						next();
					}
				});
				return false;
			}

			enemySeen = enemyInFOV;
            if (canUseAbility()){
                return useAbility();
            } else if (enemyInFOV && !isCharmedBy( enemy ) && canAttack( enemy )) {
				return doAttack( enemy );

			} else {

				if (enemyInFOV) {
					target = enemy.pos;
				} else if (enemy == null) {
					state = WANDERING;
					target = Dungeon.level.randomDestination( Meiling.this );
					return true;
				}

				if (leapCooldown <= 0 && enemyInFOV && !rooted
						&& Dungeon.level.distance(pos, enemy.pos) >= 3) {

					int targetPos = enemy.pos;
					if (lastEnemyPos != enemy.pos){
						int closestIdx = 0;
						for (int i = 1; i < PathFinder.CIRCLE8.length; i++){
							if (Dungeon.level.trueDistance(lastEnemyPos, enemy.pos+PathFinder.CIRCLE8[i])
									< Dungeon.level.trueDistance(lastEnemyPos, enemy.pos+PathFinder.CIRCLE8[closestIdx])){
								closestIdx = i;
							}
						}
						targetPos = enemy.pos + PathFinder.CIRCLE8[(closestIdx+4)%8];
					}

					Ballistica b = new Ballistica(pos, targetPos, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID);
					//try aiming directly at hero if aiming near them doesn't work
					if (b.collisionPos != targetPos && targetPos != enemy.pos){
						targetPos = enemy.pos;
						b = new Ballistica(pos, targetPos, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID);
					}
					if (b.collisionPos == targetPos){
						//get ready to leap
						leapPos = targetPos;
						//don't want to overly punish players with slow move or attack speed
						spend(GameMath.gate(TICK, enemy.cooldown(), 3*TICK));
						if (Dungeon.level.heroFOV[pos] || Dungeon.level.heroFOV[leapPos]){
							GLog.w("Meiling is about to leap to your position!");
							sprite.parent.addToBack(new TargetedCell(leapPos, 0xFF0000));
							((MonkSprite)sprite).leapPrep( leapPos );
							Dungeon.hero.interrupt();
						}
						return true;
					}
				}

				int oldPos = pos;
				if (target != -1 && getCloser( target )) {

					spend( 1 / speed() );
					return moveSprite( oldPos,  pos );

				} else {
					spend( TICK );
					if (!enemyInFOV) {
						sprite.showLost();
						state = WANDERING;
						target = Dungeon.level.randomDestination( Meiling.this );
					}
					return true;
				}
			}
		}

        

	}

    //SKILL

	public boolean canUseAbility(){
        if(Dungeon.hero.buff(Paralysis.class) != null){
            return false;
        }
        if (punch_cd < 1 && Dungeon.level.distance(this.pos, Dungeon.hero.pos) < 2){
            return true;
        } else if (rock_cd < 1){
            return true;
        } else {
            punch_cd--;
            rock_cd--;
            return false;
        }
	}

	public boolean useAbility(){
		if(rock_cd < 1){
            rock_cd = 30;
            GLog.w("Meiling is about to punch the ceiling!");
			return dropRocks(Dungeon.hero);
		} else {
            punch_cd = 10;
			return punchShot(Dungeon.hero);
		}
	}

    public boolean punchShot( Char target ) {
        Ballistica aim = new Ballistica(this.pos, target.pos, Ballistica.STOP_SOLID);
        int j = 0;
        for (int c : aim.subPath(1, 6)) {
            punch_pos[j] = c;
            j++;
        }
        return true;
    }

    public boolean dropRocks( Char target ) {

		Dungeon.hero.interrupt();
		final int rockCenter;
        if (Dungeon.level.adjacent(pos, target.pos)){
			int oppositeAdjacent = target.pos + (target.pos - pos);
			Ballistica trajectory = new Ballistica(target.pos, oppositeAdjacent, Ballistica.MAGIC_BOLT);
			WandOfBlastWave.throwChar(target, trajectory, 2, false, false, getClass());
			if (target == Dungeon.hero){
				Dungeon.hero.interrupt();
			}
			rockCenter = trajectory.path.get(Math.min(trajectory.dist, 2));
		} else {
			rockCenter = target.pos;
		}

		int safeCell;
		do {
			safeCell = rockCenter + PathFinder.NEIGHBOURS8[Random.Int(8)];
		} while (safeCell == pos
				|| (Dungeon.level.solid[safeCell] && Random.Int(2) == 0));

		ArrayList<Integer> rockCells = new ArrayList<>();

		int start = rockCenter - Dungeon.level.width() * 3 - 3;
		int pos;
		for (int y = 0; y < 7; y++) {
			pos = start + Dungeon.level.width() * y;
			for (int x = 0; x < 7; x++) {
				if (!Dungeon.level.insideMap(pos)) {
					pos++;
					continue;
				}
				//add rock cell to pos, if it is not solid, and isn't the safecell
				if (!Dungeon.level.solid[pos] && pos != safeCell && Random.Int(Dungeon.level.distance(rockCenter, pos)) == 0) {
					//don't want to overly punish players with slow move or attack speed
					rockCells.add(pos);
				}
				pos++;
			}
		}
		Buff.append(this, FallingRockBuff.class, Math.min(target.cooldown(), 3*TICK)).setRockPositions(rockCells);
        return true;
	}

    public static class FallingRockBuff extends FlavourBuff {

		private int[] rockPositions;
		private ArrayList<Emitter> rockEmitters = new ArrayList<>();

		public void setRockPositions( List<Integer> rockPositions ) {
			this.rockPositions = new int[rockPositions.size()];
			for (int i = 0; i < rockPositions.size(); i++){
				this.rockPositions[i] = rockPositions.get(i);
			}

			fx(true);  
		}

		@Override
		public boolean act() {
			for (int i : rockPositions){
				CellEmitter.get( i ).start( Speck.factory( Speck.ROCK ), 0.07f, 10 );

				Char ch = Actor.findChar(i);
				if (ch != null && !(ch instanceof Meiling)){
					Buff.prolong( ch, Paralysis.class, Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 4 : 2 );
					if (ch == Dungeon.hero){
						Statistics.bossScores[2] -= 100;
					}
				}
			}

			Camera.main.shake( 3, 0.7f );
			Sample.INSTANCE.play(Assets.Sounds.ROCKS);

			detach();
			return super.act();
		}

		@Override
		public void fx(boolean on) {
			if (on && rockPositions != null){
				for (int i : this.rockPositions){
                    Dungeon.hero.sprite.parent.addToBack(new TargetedCell(i, 0xFF0000));
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

		private static final String POSITIONS = "positions";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(POSITIONS, rockPositions);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			rockPositions = bundle.getIntArray(POSITIONS);
		}
	}
    
}

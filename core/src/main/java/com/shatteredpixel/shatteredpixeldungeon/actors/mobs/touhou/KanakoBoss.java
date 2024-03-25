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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou.YukariGap.YukariChen;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou.YukariGap.YukariRan;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile.MagicParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.RainbowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DelayedExplosiveTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.YogSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.YukariTalismanSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PathFinderUtils;
import com.watabou.utils.Random;
 
 public class KanakoBoss extends Mob {
     {	
        spriteClass = YogSprite.class;

		HP = HT = 2000;

		EXP = 50;

		//so that allies can attack it. States are never actually used.
		state = HUNTING;
        baseSpeed = 0.5f;
		viewDistance = 12;

		properties.add(Property.BOSS);
     }
     
     private int phase = 0;

     private int abilityCooldown = 10;
     private int abilityTracker = 0; // To track various thing

     private int teleportCooldown = 10;

     private static final int MIN_ABILITY_CD = 5;
     private static final int MAX_ABILITY_CD = 10;

    private void setBaseSpeed(){
        if (phase != 0) baseSpeed = 1f;
    }

     @Override
	public void notice() {
		super.notice();
		if (!BossHealthBar.isAssigned()) {
			BossHealthBar.assignBoss(this);
		}
	}

     @Override
     public int damageRoll() {
         return Random.NormalIntRange( 0, 0 );
     }
     
     @Override
     public int attackSkill( Char target ) {
         return 36;
     }
     
     @Override
     public int drRoll() {
         return Random.NormalIntRange(0, 16);
     }
 
     @Override
     public void die(Object cause) {
         super.die(cause);
     }

    @Override
	protected boolean getCloser( int target ) {
		if (phase == 0) {
            // spend(1f);
			return enemySeen && getFurther( target );
		} else {
			return super.getCloser( target );
		}
	}
 
     @Override
     protected boolean act() {

        abilityCooldown--;

        //char logic
		if (fieldOfView == null || fieldOfView.length != Dungeon.level.length()){
			fieldOfView = new boolean[Dungeon.level.length()];
		}
		Dungeon.level.updateFieldOfView( this, fieldOfView );

        throwItems();

		//mob logic
		enemy = chooseEnemy();

		enemySeen = enemy != null && enemy.isAlive() && fieldOfView[enemy.pos] && enemy.invisible <= 0;
		//end of char/mob logic

		if (phase == 0){
			if (Dungeon.hero.viewDistance >= Dungeon.level.distance(pos, Dungeon.hero.pos)) {
				Dungeon.observe();
			}
			if (Dungeon.level.heroFOV[pos]) {
				notice();
			}
		}
        if (phase == 0){
            if (abilityTracker > 0){
                if(!Dungeon.hero.isAlive()){
                    return super.act();
                }
                enemy = Dungeon.hero;

                KanakoTalisman.spawnMass(2);
                //We want her to shoot laser before she move, but start charging after she move so the targeted cell stay consistent
                Ballistica aimer = new Ballistica(this.pos, enemy.pos, Ballistica.STOP_CHARS);
                int colliPos = aimer.path.get(aimer.path.indexOf(aimer.collisionPos) - 1);

                int[] shoot = PathFinderUtils.backtrianglePosNumber(PathFinder.CIRCLE8, colliPos, enemy.pos);
                int laser1Pos = enemy.pos;
                int laser2Pos = enemy.pos;
                int laser1NextPos = laser1Pos;
                int laser2NextPos = laser2Pos;
                for(int i = 1; i <= abilityTracker; i++){
                    laser1Pos = PathFinder.CIRCLE8[shoot[0]] + laser1Pos;
                    laser2Pos = PathFinder.CIRCLE8[shoot[1]] + laser2Pos;
                    if (i == abilityTracker - 1){
                        laser1NextPos = laser1Pos;
                        laser2NextPos = laser2Pos;
                    }
                }
                // Shoot
                boisterousdance(this.pos, enemy, laser1Pos, laser2Pos);
                //Move
                boolean act = super.act();
                //Aim for next laser
                if (abilityTracker > 0){
                    Ballistica next1 = new Ballistica(this.pos, laser1NextPos, Ballistica.STOP_CHARS);
                    Ballistica next2 = new Ballistica(this.pos, laser2NextPos, Ballistica.STOP_CHARS);
                    for (int p : next1.subPath(0, Dungeon.level.distance(this.pos, next1.collisionPos))){
                        sprite.parent.add(new TargetedCell(p, 0xA020F0));
                    }
                    for (int p : next2.subPath(0, Dungeon.level.distance(this.pos, next1.collisionPos))){
                        sprite.parent.add(new TargetedCell(p, 0xA020F0));
                    }
                }
                return act;

                // spend(TICK);
                // return true;
            }
            if (abilityCooldown == 0){
                abilityTracker = 4;
                abilityCooldown = Random.Int(MIN_ABILITY_CD, MAX_ABILITY_CD);
            }
        }

        return super.act();
     }
     

    private void boisterousdance(int from, Char enemy, int laser1Pos, int laser2Pos){
        Ballistica laser1shoot = new Ballistica(from, laser1Pos, Ballistica.WONT_STOP);
        laser1Pos = laser1shoot.collisionPos;
        sprite.parent.add(new Beam.YukariRay(sprite.center(), DungeonTilemap.raisedTileCenterToWorld(laser1Pos)));
        for (int p : laser1shoot.subPath(0, Dungeon.level.distance(from, laser1Pos))){
            Char ch = Actor.findChar(p);
			if(ch != null && !(ch instanceof KanakoBoss)){
				ch.damage(Math.max(ch.HP/6, 10), this);
				Buff.prolong(ch, Vertigo.class, 5f);

                if(ch instanceof KanakoTalisman){
                    ch.die(null);
                }
			}
        }

        Ballistica laser2shoot = new Ballistica(from, laser2Pos, Ballistica.WONT_STOP);
        laser2Pos = laser2shoot.collisionPos;
        sprite.parent.add(new Beam.YukariRay(sprite.center(), DungeonTilemap.raisedTileCenterToWorld(laser2Pos)));
        for (int p : laser2shoot.subPath(0, Dungeon.level.distance(from, laser2Pos))){
            Char ch = Actor.findChar(p);
			if(ch != null && !(ch instanceof KanakoBoss)){
				ch.damage(Math.max(ch.HP/6, 10), this);
				Buff.prolong(ch, Vertigo.class, 5f);
                if(ch instanceof KanakoTalisman){
                    ch.die(null);
                }
			}
        }

        abilityTracker--;
    }


    @Override
    public int attackProc(Char enemy, int damage) {
        return super.attackProc(enemy, damage);
    }

    public void toSafety(){
        if (phase == 0){
            abilityTracker = 0;
            abilityCooldown = 5;
        }
		int telepos;
		do {
			telepos = Random.Int( Dungeon.level.length() );
		} while (!Dungeon.level.passable[telepos]);
		CellEmitter.center(this.pos).burst(RainbowParticle.BURST, 10);
		ScrollOfTeleportation.appear(this, telepos);
		Dungeon.level.occupyCell(this);
	}


    @Override
	public void damage(int dmg, Object src) {
		if (!Dungeon.level.mobs.contains(this)){
			return;
		}

		int hpBracket = HT / 10;
		int beforeHitHP = HP;
		super.damage(dmg, src);

		dmg = beforeHitHP - HP;
		if (phase == 0){
			toSafety();
		}

        if ((beforeHitHP/hpBracket - HP/hpBracket) >= 2){
			HP = hpBracket * ((beforeHitHP/hpBracket)-1) + 1;
		}

        phase = (HT-HP)/200;
	}



    private static final String PHASE                = "phase";
    private static final String ABILITY_CD          = "abilityCooldown";
	private static final String ABILITY_TRACKER     = "abilityTracker";
	private static final String TPCD                = "TeleportCooldown";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
        bundle.put( PHASE, phase );
		bundle.put( ABILITY_CD, abilityCooldown );
        bundle.put( ABILITY_TRACKER, abilityTracker );
        bundle.put( TPCD, teleportCooldown );
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		BossHealthBar.assignBoss(this);
        phase = bundle.getInt(PHASE);
        setBaseSpeed();
		abilityCooldown = bundle.getInt( ABILITY_CD );
        abilityTracker = bundle.getInt( ABILITY_TRACKER );
        teleportCooldown = bundle.getInt( TPCD );
	}

    public static class KanakoTalisman extends Mob {
		{
			spriteClass = YukariTalismanSprite.class;

			EXP = 0;
			maxLvl = 1;
			lootChance = 0f;
			viewDistance = 0;
            state = WANDERING;
		}

        private int walkToPos = 0;

		@Override
		public void damage(int dmg, Object src) {
			return;
		}
		
		public static void spawnMass(int count){
			for (int i = 0; i < count; i++){
				KanakoTalisman tal = new KanakoTalisman();
				int cell;
				do {
					cell = Random.Int( Dungeon.level.length() );
				} while ( Dungeon.level.distance(cell, 593) < 4 ||
					(!Dungeon.level.passable[cell] || !Dungeon.level.openSpace[cell]));
				tal.pos = cell;
                tal.walkToPos = Dungeon.hero.pos;
				GameScene.add( tal, 1 );
				Dungeon.level.occupyCell( tal );
			}
		}

		@Override
		protected boolean act() {
            spend(1f);
			return true;
		}

		@Override
		public void die(Object cause) {
            explodeMagic();
			destroy();
			sprite.killAndErase();
			Dungeon.level.mobs.remove(this);
		}

        private void explodeMagic(){
            for (int n : PathFinder.NEIGHBOURS9) {
				int c = this.pos + n;
				if (c >= 0 && c < Dungeon.level.length()) {
					if (Dungeon.level.heroFOV[c]) {
						CellEmitter.get(c).burst(MagicParticle.FACTORY, 4);
					}
					Char ch = Actor.findChar(c);
					if (ch != null && !(ch instanceof KanakoBoss)) {
                        int dmg = 20 - ch.drRoll();
						ch.damage(dmg, this);
					}
				}
			}
        }
        private static final String WALKPOS                = "WALKPOS";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put( WALKPOS, walkToPos );
        }
        
        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            walkToPos = bundle.getInt(WALKPOS);
        }
	}
}
 
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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.*;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MoveDetect;
import com.shatteredpixel.shatteredpixeldungeon.items.FireOath;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SunnySprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.effects.MasterSparkBig;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.utils.Callback;

public class Sunny extends Mob {

	{
		HP = HT = Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 50 : 40;
		EXP = 4;
		defenseSkill = 8;
		spriteClass = SunnySprite.class;

        state = WANDERING;
		viewDistance = 20;

		properties.add(Property.FAIRY);
		properties.add(Property.BOSS);
        immunities.add(Burning.class);
	}

    public int anger = 0;
	private int SKILL_COOLDOWN = 18;
	private int sun_cd = 7;
    private boolean charging_skill = false;
	private int aim = 1;
	
    @Override
	public float speed() {
		return super.speed();
	}

	//Sunny deal more damage
    @Override
	public int damageRoll() {
		return Random.NormalIntRange( 3*(anger+1), 6*(anger+1) );
	}

	@Override
	public int attackSkill( Char target ) {
		return 10;
	}

	@Override
	public int defenseSkill(Char enemy) {
		return (int)(super.defenseSkill(enemy) * ((anger > 0) ? 1.5f : 1));
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 2);
	}

	@Override
	public boolean act() {
		if(Dungeon.hero != null && Dungeon.hero.buff(MoveDetect.class) != null && Dungeon.hero.justMoved){
			throwRock();
        }

        if (enemySeen && !isCharmedBy( enemy ) && canAttack( enemy )) {

            if (canUseReady()){
                return useReady();
            }
            if (canUseAbility()){
                return useAbility();
            }
            return doAttack( enemy );
            
        } else {
            if (canUseReady()){
                return useReady();
            }
            if (canUseAbility()){
                return useAbility();
            }

            if (enemySeen || charging_skill) {
                target = Dungeon.hero.pos;
            } else {
                sprite.showLost();
                charging_skill = false;
                state = WANDERING;
                return super.act();
            }
            
		    return super.act();
        }
	}

	@Override
	protected boolean canAttack( Char enemy ) {
		Ballistica attack = new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE);
		// When super angry only do melee, else range attack only when no move detect
		if (anger < 2) return !(Dungeon.level.adjacent(pos, enemy.pos)) && attack.collisionPos == enemy.pos && (Dungeon.hero.buff(MoveDetect.class) == null);
		else return Dungeon.level.adjacent(pos, enemy.pos);
	}
	
	@Override
	public boolean doAttack(Char enemy) {
		if (!(Dungeon.level.adjacent(pos, enemy.pos))) spend(TICK/2);
		return super.doAttack(enemy);
	}
	
    @Override
	public int attackProc(Char enemy, int damage) {
		// if(!(Dungeon.level.adjacent(pos, enemy.pos))) spend(TICK/4);
		if(Random.IntRange(1, 6 / ( anger + 1)) == 1){
			// 1/6 -> 1/3 -> 1/2 chance
			Buff.affect(enemy, Burning.class).reignite(enemy, (float)(3 + anger));
		}
		return super.attackProc(enemy, damage);
	}

	// @Override
	// public void updateSpriteState() {
	// 	super.updateSpriteState();

	// 	if (pumpedUp > 0){
	// 		((GooSprite)sprite).pumpUp( pumpedUp );
	// 	}
	// }

	@Override
	protected boolean getCloser( int target ) {
		if (state == HUNTING && anger < 2) {
			return enemySeen && getFurther( target );
		} else {
			return super.getCloser( target );
		}
	}

	@Override
	public void aggro(Char ch) {
		if (ch == null || fieldOfView == null || fieldOfView[ch.pos]) {
			super.aggro(ch);
		}
	}


	@Override
	public void die( Object cause ) {
		if(anger > 1){
			Dungeon.level.unseal();
			GameScene.bossSlain();
			Dungeon.level.drop( new FireOath(), pos ).sprite.drop();
		}
		for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
			if(mob instanceof Luna){
				((Luna)mob).anger++;
			} else if(mob instanceof Star){
				((Star)mob).anger++;
			}
			if (anger > 0) BossHealthBar.assignBoss(mob);
		}
		Badges.validateBossSlain();
		if (Statistics.qualifiedForBossChallengeBadge){
			Badges.validateBossChallengeCompleted();
		}
		// Statistics.bossScores[0] += 350;
		// Statistics.bossScores[0] = Math.min(1050, Statistics.bossScores[0]);

		super.die( cause );
	}
    
    //////////////////////////////////////////////////////////////
    // SKILL
    //
    private boolean canUseReady(){
        sun_cd--;
        if ((sun_cd <= 0 && (enemySeen || Dungeon.hero != null) && charging_skill == false)){
            return true;
        } else {
            return false;
        }
    }
    private boolean useReady(){
		GLog.n(Messages.get(this, "charging_skill"));
        spend(TICK);
        if (Dungeon.hero != null){
            aim = Dungeon.hero.pos;
            Dungeon.hero.interrupt();
        } else{
            aim = enemy.pos;
        }
        charging_skill = true;
        this.sprite.add(CharSprite.State.CHARGING);
		Ballistica p = new Ballistica(this.pos, aim, Ballistica.WONT_STOP);
		for(int j : p.path){
			sprite.parent.add(new TargetedCell(j, 0xFF0000));
		}
        return true;
    }

    private boolean canUseAbility(){
        if(charging_skill == true){
            spend( TICK );
            return true;
        } else {
            return false;
        }
    }

    private boolean useAbility(){      
        return sunBeam();
    }

    private boolean sunBeam(){
        spend( TICK );
        charging_skill = false;
        this.sprite.remove(CharSprite.State.CHARGING);
        sun_cd = SKILL_COOLDOWN / (anger +1);
        Ballistica p = new Ballistica(this.pos, aim, Ballistica.WONT_STOP);
        for(int i : p.path){
			if(Dungeon.level.map[i] == Terrain.EMPTY){
				int duration = 3;
				if(isLunatic()) duration = 5;
                GameScene.add( Blob.seed( i, duration, Fire.class ) );
                //Fire last longer near hero
                if(Dungeon.level.distance(Dungeon.hero.pos, i) <= 2){
                    GameScene.add( Blob.seed( i, duration*3, Fire.class ) );
                }
            }
            Char ch = Actor.findChar(i);
            if (ch != null && ch.alignment != this.alignment){
				if(isLunatic()){
					ch.damage(15,this);
				} else{
					ch.damage(10,this);
				}
                Buff.affect(ch, Burning.class).reignite(ch, 4f);
            }
		}
        sprite.parent.add(new MasterSparkBig.BigMasterSpark(DungeonTilemap.raisedTileCenterToWorld( this.pos), DungeonTilemap.raisedTileCenterToWorld(p.collisionPos), 1f));
        return true;
    }

	private final String ANGER = "angery"; //yes
	private final String SUN_CD = "sun__cooldown";
    private final String CHARGING_SKILL = "ready";
    private final String AIM = "hero_pos";

	@Override
	public void storeInBundle( Bundle bundle ) {

		super.storeInBundle( bundle );

		bundle.put( ANGER , anger );
		bundle.put( SUN_CD, sun_cd );
        bundle.put( CHARGING_SKILL, charging_skill );
        bundle.put( AIM, aim );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {

		super.restoreFromBundle( bundle );

		anger = bundle.getInt( ANGER );
		if (anger > 1) BossHealthBar.assignBoss(this);

		sun_cd = bundle.getInt(SUN_CD);
        charging_skill = bundle.getBoolean(CHARGING_SKILL);
        aim = bundle.getInt(AIM);
	}
	
	private void throwRock(){
		Dungeon.hero.interrupt();
		// Char ch = this;
            ((MissileSprite)this.sprite.parent.recycle( MissileSprite.class )).
            reset( this.pos, Dungeon.hero.pos, new Bullet(), new Callback() {
                @Override
                public void call() {
                    // ch.onAttackComplete();
					Dungeon.hero.damage(Random.Int(2*anger), this);
                }
            } );
	}

	public class Bullet extends Item {
		{
			image = ItemSpriteSheet.BULLET;
		}
	}

}

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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.LunaSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.utils.Callback;

public class Luna extends Mob {

	{
		HP = HT = Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 50 : 40;
		EXP = 4;
		defenseSkill = 8;
		spriteClass = LunaSprite.class;

        state = WANDERING;
		viewDistance = 20;

		properties.add(Property.BOSS);
        immunities.add(Burning.class);
	}

    public int anger = 0;
	private int moon_cd = 1;
    private boolean charging_skill = false;
	
    @Override
	public float speed() {
		return super.speed() * (anger > 1 ? 1f : 0.8f);
	}

    @Override
	public int damageRoll() {
		if (anger > 0) {
			return Random.NormalIntRange( 4*anger, 16 );
		} else {
			return Random.NormalIntRange( 4,  8);
		}
	}

	@Override
	public int attackSkill( Char target ) {
		return 10;
	}

	@Override
	public int defenseSkill(Char enemy) {
		return (int)(super.defenseSkill(enemy) * ((anger > 0) ? (1 + anger/2) : 1));
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
	public int attackProc(Char enemy, int damage) {
        Buff.affect(enemy, Weakness.class, 5f);
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

	// @Override
	// public void damage(int dmg, Object src) {
	// 	if (!BossHealthBar.isAssigned()){
	// 		BossHealthBar.assignBoss( this );
	// 		Dungeon.level.seal();
	// 	}
	// 	boolean bleeding = (HP*2 <= HT);
	// 	super.damage(dmg, src);
	// 	if ((HP*2 <= HT) && !bleeding){
	// 		BossHealthBar.bleed(true);
	// 		sprite.showStatus(CharSprite.NEGATIVE, Messages.get(this, "enraged"));
	// 		((GooSprite)sprite).spray(true);
	// 		yell(Messages.get(this, "gluuurp"));
	// 	}
	// 	LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
	// 	if (lock != null) lock.addTime(dmg*2);
	// }

	@Override
	public void die( Object cause ) {
		
		
        if(anger > 1){
			Dungeon.level.unseal();
			GameScene.bossSlain();
		}
		for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
			if (mob instanceof Sunny){
				((Sunny)mob).anger++;
			} else if(mob instanceof Luna){
				((Luna)mob).anger++;
			} else if(mob instanceof Star){
				((Star)mob).anger++;
			}
		}

		
		Badges.validateBossSlain();
		if (Statistics.qualifiedForBossChallengeBadge){
			Badges.validateBossChallengeCompleted();
		}
		Statistics.bossScores[0] += 350; //Goo has a 50 point gimme
		Statistics.bossScores[0] = Math.min(1050, Statistics.bossScores[0]);
        super.die( cause );
	}
	
	@Override
	public void notice() {
		super.notice();
		if (!BossHealthBar.isAssigned()) {
			BossHealthBar.assignBoss(this);
			for (Char ch : Actor.chars()){
				if (ch instanceof DriedRose.GhostHero){
					((DriedRose.GhostHero) ch).sayBoss();
				}
			}
		}
	}
    
    //////////////////////////////////////////////////////////////
    // SKILL
    //
    private boolean canUseReady(){
        moon_cd--;
        if ((moon_cd <= 0 && (enemySeen || Dungeon.hero != null) && charging_skill == false)){
            return true;
        } else {
            return false;
        }
    }
    private boolean useReady(){
        if (Dungeon.hero != null){
            Dungeon.hero.interrupt();
        }
        GLog.n(Messages.get(this, "charging_skill"));
        spend(TICK);
        charging_skill = true;
        this.sprite.add(CharSprite.State.CHARGING);
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
        return moonSilence();
    }

    private boolean moonSilence(){
        spend( TICK );
        charging_skill = false;
        this.sprite.remove(CharSprite.State.CHARGING);
        moon_cd = 15;
        if (Dungeon.hero != null){
            //Silence, magic immue hero, basically prevent any magic thing
            //Then delete map
            Dungeon.hero.interrupt();
            Buff.affect(Dungeon.hero, Silence.class, 8f);
            Buff.affect(Dungeon.hero, MagicImmune.class, 8f);
            BArray.setFalse(Dungeon.level.visited);
			BArray.setFalse(Dungeon.level.mapped);
            GameScene.updateFog(); //just in case hero wasn't moved
            Dungeon.observe();
            }
        return true;
    }

	private final String ANGER = "angery"; //yes
	private final String MOON_CD = "sun__cooldown";
    private final String CHARGING_SKILL = "ready";

	@Override
	public void storeInBundle( Bundle bundle ) {

		super.storeInBundle( bundle );

		bundle.put( ANGER , anger );
		bundle.put( MOON_CD, moon_cd );
        bundle.put( CHARGING_SKILL, charging_skill );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {

		super.restoreFromBundle( bundle );

		anger = bundle.getInt( ANGER );
		if (state != SLEEPING) BossHealthBar.assignBoss(this);

		moon_cd = bundle.getInt(MOON_CD);
        charging_skill = bundle.getBoolean(CHARGING_SKILL);
	}

	private void throwRock(){
		Char ch = this;
            ((MissileSprite)this.sprite.parent.recycle( MissileSprite.class )).
            reset( this.pos, Dungeon.hero.pos, new Bullet(), new Callback() {
                @Override
                public void call() {
                    ch.onAttackComplete();
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

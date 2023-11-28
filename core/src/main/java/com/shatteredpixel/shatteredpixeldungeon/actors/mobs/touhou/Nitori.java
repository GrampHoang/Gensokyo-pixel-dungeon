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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.AquaBlast;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GeyserTrap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.NitoriSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class Nitori extends Mob implements Callback {

    private static final float TIME_TO_ZAP	= 1f;

	{	
		spriteClass = NitoriSprite.class;
		HP = HT = 135;
		defenseSkill = 26;
		EXP = 14;
		maxLvl = 30;

        loot = Generator.Category.POTION;
		lootChance = 0.3f;
	}

	private int skill_cd = 15;


	@Override
	public int damageRoll() {
		return Random.NormalIntRange(35, 45);
	}

	@Override
	public int attackSkill(Char target) {
		return 32;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(6, 14);
	}

	@Override
	public void die(Object cause) {
		super.die(cause);
	}

    @Override
	public int attackProc(Char enemy, int damage) {
		return super.attackProc(enemy, damage);
	}

    @Override
	protected boolean canAttack( Char enemy ) {
		return new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
	}
	
    @Override
	protected boolean act() {
        if (enemy != null) GLog.w(Integer.toString(skill_cd));
		if (skill_cd <= 0 && enemy != null){
			spend(1f);
            skill_cd = Random.Int(25, 32);
			return rainOfBalls(enemy);
		}
        if (Dungeon.level.water[pos]) skill_cd -= 3;
        else skill_cd -= 1;
		return super.act();
	}

    private boolean rainOfBalls(Char enemy){
        getSpotAndThrow(0, 7);
        getSpotAndThrow(8, 15);
        getSpotAndThrow(16, 23);
        return true;
    }

    private void getSpotAndThrow(int min, int max){
        int dropPos = 0;
        int tryCount = 0;
        do {
            dropPos = PathFinder.NEIGHBOURS24[Random.IntRange(min,max)] + enemy.pos;
            tryCount++;
        } while (Dungeon.level.solid[dropPos] == true && tryCount < 30);
        throwBall(dropPos);
    }
    private void throwBall(int pos){
        PointF sky = this.sprite.center();
	    sky.y -= 60;
        PointF targetPoint = DungeonTilemap.tileToWorld( pos );
        ((MissileSprite) this.sprite.parent.recycle(MissileSprite.class)).
					reset(sky,
                        targetPoint,
                        new AquaBlast(),
                        new Callback() {
						@Override
						public void call() {
                            for(int i : PathFinder.NEIGHBOURS8){
                                Char ch = Actor.findChar(i + pos);
                                if (ch != null && ch.alignment != alignment){
                                    ch.damage(20, this);
                                }
                            }
                            new GeyserTrap().set(pos).activate();
						}
					});
    }

	protected boolean doAttack( Char enemy ) {

		if (Dungeon.level.adjacent( pos, enemy.pos )) {
			
			return super.doAttack( enemy );
			
		} else {
			
			if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
				sprite.zap( enemy.pos );
				return false;
			} else {
				zap();
				return true;
			}
		}
	}
	
	//used so resistances can differentiate between melee and magical attacks
	public static class NitoriBall{}    //Yes
	
	protected void zap() {
		spend( TIME_TO_ZAP );
		
		if (hit( this, enemy, true )) {
			if (Random.Int(10) < 5){
                new GeyserTrap().set(enemy.pos).activate();
            }
            int dmg = Random.NormalIntRange( 30,  40) - enemy.drRoll();
			// dmg = Math.round(dmg * AscensionChallenge.statModifier(this));
			enemy.damage( dmg, new NitoriBall() );
		} else {
			enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
            if (Random.Int(10) < 2){
                new GeyserTrap().set(enemy.pos).activate();
            }
		}
	}
	
	public void onZapComplete() {
		zap();
		next();
	}
	
	@Override
	public void call() {
		next();
	}


	private static final String SKILL_CD     = "skillcd";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( SKILL_CD, skill_cd );
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		skill_cd = bundle.getInt( SKILL_CD );
	}

}

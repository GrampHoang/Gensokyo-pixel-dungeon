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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wraith;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou.ShanghaiDoll;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou.HouraiDoll;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.*;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.AliceSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Callback;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Collections;

public class Alice extends Mob {

	private static final float TIME_TO_ZAP	= 1f;
	
	{
		spriteClass = AliceSprite.class;

		HP = HT = 50;

		defenseSkill = 17;

		EXP = 9;

		maxLvl = 17;

        loot = PotionOfHealing.class;
		lootChance = 0.1667f;
	}

	private float SUMMON_CD = 30f;
    private float summon_cd = 5f;

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(6, 16);
	}

	@Override
	public int attackSkill(Char target) {
		return 22;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(2, 8);
	}

    @Override
	public float speed() {
		return 3f * super.speed() / 4f;
	}

    @Override
	protected boolean canAttack( Char enemy ) {
		Ballistica attack = new Ballistica( pos, enemy.pos, Ballistica.WONT_STOP);
		return !Dungeon.level.adjacent(pos, enemy.pos) && attack.collisionPos == enemy.pos;
	}

    @Override
	protected boolean getCloser( int target ) {
		if (state == HUNTING) {
			return enemySeen && getFurther( target );
		} else {
			return super.getCloser( target );
		}
	}

	@Override
	public void aggro(Char ch) {
		//cannot be aggroed to something it can't see
		if (ch == null || fieldOfView == null || fieldOfView[ch.pos]) {
			super.aggro(ch);
		}
	}

    @Override
	protected boolean act() {
        if (enemySeen){
            if (summon_cd > 0){
                summon_cd--;
            } else {
                summonDolls();
            }
        }
		return super.act();
	}
    protected void summonDolls(){
        summon_cd = SUMMON_CD;
		boolean hourai_not_spawned = true;
		boolean shanghai_not_spawned = true;
        for (int c : PathFinder.NEIGHBOURS8) {
            if (Actor.findChar(this.pos + c) == null
						&& Dungeon.level.passable[this.pos + c]
						&& (Dungeon.level.openSpace[this.pos + c] || !hasProp(Actor.findChar(this.pos), Property.LARGE))){
				if (hourai_not_spawned){
					HouraiDoll hourai = HouraiDoll.spawnAt(this.pos + c);
					Dungeon.level.occupyCell( hourai );
					hourai_not_spawned = false;
				} else if (shanghai_not_spawned){
					ShanghaiDoll shanghai = ShanghaiDoll.spawnAt(this.pos + c);
					Dungeon.level.occupyCell( shanghai );
					shanghai_not_spawned = false;
				} else{
					break;
				}
            }
        }
    }
}

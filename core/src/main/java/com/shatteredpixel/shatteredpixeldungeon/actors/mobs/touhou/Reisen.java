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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ReisenSprite;
import com.watabou.utils.Random;

public class Reisen extends Mob {

	{
		spriteClass = ReisenSprite.class;

		HP = HT = 70;
		defenseSkill = 18;
		
		EXP = 11;
		maxLvl = 21;
		
		loot = Generator.Category.POTION;
		lootChance = 0.5f;

	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 8, 18 );
	}
	
	@Override
	public float attackDelay() {
		if(Dungeon.level.adjacent(pos, enemy.pos)){
			return super.attackDelay()*0.5f;
		}
		return super.attackDelay();
	}

	@Override
	public int attackSkill( Char target ) {
		return 25;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 8);
	}
	
	@Override
	protected boolean canAttack( Char enemy ) {
		return new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
	}

	@Override
	public int attackProc( Char enemy, int damage ) {
		if (Dungeon.level.adjacent( pos, enemy.pos )) {
			if(isLunatic() && Random.IntRange(0,9) == 1){
				//10% chance to push per hit
				Ballistica trajectory = new Ballistica(this.pos, enemy.pos, Ballistica.STOP_TARGET);
				//trim it to just be the part that goes past them
				trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size() - 1), Ballistica.PROJECTILE);
				//knock them back along that ballistica
				WandOfBlastWave.throwChar(enemy, trajectory, 1, false, true, this.getClass());
			}
			return super.attackProc( enemy, damage );
		} else {
			if(isLunatic()){
				damage = super.attackProc( enemy, damage*3 );
			} else{
				damage = super.attackProc( enemy, damage*2 );
			}

			int effect = Random.Int(10);
			if (effect > 6) {

				if (effect >= 9){
					Buff.prolong(enemy, Vertigo.class, 2f);

				} else
					Buff.prolong( enemy, Charm.class, 2f);

			}
			return damage;
		}
	}
	
	@Override
	public void die( Object cause ) {
		super.die( cause );
	}

}

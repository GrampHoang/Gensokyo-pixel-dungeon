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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.EnergyCrystal;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WoodStick;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ElementalSprite;

import com.watabou.utils.Random;

public class Fairy extends Mob {
	{
		spriteClass = ElementalSprite.Chaos.class;
		HP = HT = 6;
		defenseSkill = 3;
		maxLvl = 5;
		properties.add(Property.FAIRY);
	}


	@Override
	public int damageRoll() {
		return Random.NormalIntRange(1, 3);
	}

	@Override
	public int attackSkill(Char target) {
		return 8;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 1);
	}

	@Override
	public void die(Object cause) {
        if (Random.IntRange(0, 10) == 8){
            Dungeon.level.drop( new EnergyCrystal().dropOne(), pos );
        }
		if(Random.Int(500) == 1){
			Dungeon.level.drop( new WoodStick(), pos ).sprite.drop();
		}
		super.die(cause);
	}

	@Override
	protected boolean canAttack( Char enemy ) {
		if (isLunatic()){
			Ballistica attack = new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE);
			return Dungeon.level.distance(this.pos, enemy.pos) < 3 && attack.collisionPos == enemy.pos;
		} else {
			return super.canAttack(enemy);
		}
	}
}

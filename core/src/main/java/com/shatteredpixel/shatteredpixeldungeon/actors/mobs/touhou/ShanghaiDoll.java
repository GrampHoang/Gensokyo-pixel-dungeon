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

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MonkSprite;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.watabou.utils.Random;

public class ShanghaiDoll extends Mob {
	
	{
		spriteClass = MonkSprite.class;
		
		HP = HT = 120;
		defenseSkill = 30;
	
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 8, 10 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 10;
	}
	
    @Override
	public int attackProc( Char enemy, int damage ) {
	    damage = super.attackProc( enemy, damage );
        Buff.affect(enemy, Cripple.class, 2f);
        Buff.affect(enemy, Bleeding.class).set(3f);
        return damage;
    }

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 2);
	}
}

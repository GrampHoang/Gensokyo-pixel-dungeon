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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GnollSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class Gnoll extends Mob {
	
	{
		spriteClass = GnollSprite.class;
		
		HP = HT = 12;
		defenseSkill = 4;
		
		EXP = 2;
		maxLvl = 8;
		
		loot = Gold.class;
		lootChance = 0.5f;

		if(isLunatic()){
			immunities.add(Poison.class);
			immunities.add(ToxicGas.class);
		}
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 1, 6 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 10;
	}
	
	@Override
	public int defenseProc( Char enemy, int damage ) {
		if(isLunatic() && enemySeen == true && Random.IntRange(0,3) == 2){
			for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
				if(mob instanceof Gnoll){
					mob.beckon( this.pos );
				}
			}
			GLog.w("*Gnoll's howl*");		
			this.sprite.centerEmitter().start( Speck.factory( Speck.SCREAM ), 0.2f, 2 );
			Sample.INSTANCE.play( Assets.Sounds.CHALLENGE );
		}
		return super.defenseProc(enemy, damage);
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 2);
	}
}

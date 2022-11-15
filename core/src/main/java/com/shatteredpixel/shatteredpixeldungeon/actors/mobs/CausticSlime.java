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

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.GooBlob;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CausticSlimeSprite;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class CausticSlime extends Slime {
	
	{
		spriteClass = CausticSlimeSprite.class;
		
		properties.add(Property.ACIDIC);
		if(isLunatic()){
			immunities.add(ToxicGas.class);
		}
	}
	
	@Override
	public int attackProc( Char enemy, int damage ) {
		if (Random.Int( 2 ) == 0) {
			Buff.affect( enemy, Ooze.class ).set( Ooze.DURATION );
			enemy.sprite.burst( 0x000000, 5 );
		}
		
		return super.attackProc( enemy, damage );
	}
	
	@Override
	public void die( Object cause ) {
		super.die( cause );
		if (cause == Chasm.class) return;

		if(isLunatic()){
			WandOfBlastWave.BlastWave.blast(this.pos);
			this.sprite.burst( 0x000000, 5 );
			for(int i : PathFinder.NEIGHBOURS8){
				Char ch = Actor.findChar(this.pos+i);
				if (ch != null){
					if (ch instanceof CausticSlime){
						ch.HP += 8;
					} else {
						Buff.affect(ch, Slow.class, 2f);
						Buff.affect( enemy, Ooze.class ).set( Ooze.DURATION );
					}
				}
			}
		}
	}

	@Override
	public void rollToDropLoot() {
		if (Dungeon.hero.lvl > maxLvl + 2) return;
		
		super.rollToDropLoot();
		
		int ofs;
		do {
			ofs = PathFinder.NEIGHBOURS8[Random.Int(8)];
		} while (Dungeon.level.solid[pos + ofs] && !Dungeon.level.passable[pos + ofs]);
		Dungeon.level.drop( new GooBlob(), pos + ofs ).sprite.drop( pos );
	}
}

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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MonkSprite;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.Random;

public class ShanghaiDoll extends Mob {
	
	{
		spriteClass = MonkSprite.class;

		HP = HT = 45;
		defenseSkill = 16;
		
		EXP = 0;
		maxLvl = 0;
	
		lootChance = 0;
		
		// HP = HT = 120;
		// defenseSkill = 30;
	
	}

	private static final float SPAWN_DELAY	= 2f;
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 4, 6 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 18;
	}
	
    @Override
	public int attackProc( Char enemy, int damage ) {
	    damage = super.attackProc( enemy, damage );
        Buff.affect(enemy, Cripple.class, 1f);
        Buff.affect(enemy, Bleeding.class).set(2f);
        return damage;
    }

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 2);
	}

	public static ShanghaiDoll spawnAt( int pos ) {
		if ((!Dungeon.level.solid[pos] || Dungeon.level.passable[pos]) && Actor.findChar( pos ) == null) {
			
			ShanghaiDoll w = new ShanghaiDoll();
			w.pos = pos;
			w.state = w.HUNTING;
			GameScene.add( w, SPAWN_DELAY );
			Dungeon.level.occupyCell(w);

			w.sprite.alpha( 0 );
			w.sprite.parent.add( new AlphaTweener( w.sprite, 1, 0.5f ) );
			
			// w.sprite.emitter().burst( ShadowParticle.CURSE, 5 );
			
			return w;
		} else {
			return null;
		}
	}

	public class ShanghaiDollMob extends ShanghaiDoll {
		{
			spriteClass = MonkSprite.class;
			HP = HT = 45;
			defenseSkill = 16;
			
			EXP = 0;
			maxLvl = 0;
		
			lootChance = 0;
		}
	
	}

}

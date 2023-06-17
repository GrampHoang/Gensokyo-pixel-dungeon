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

package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou.ReimuBoss;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Ofuda;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class ReimuBossSprite extends MobSprite {
    
	private Animation fly;
	private Animation read;
    private Animation cast;

	public ReimuBossSprite() {
		super();
		
		texture( Assets.Sprites.REIMU );
		
		TextureFilm frames = new TextureFilm( texture, 12, 15 );
		
		idle = new Animation( 1, true );
		idle.frames( frames, 0, 0, 0, 1, 0, 0, 1, 1 );
		
		run = new Animation( 20, true );
		run.frames( frames, 2, 3, 4, 5, 6, 7 );
		
		die = new Animation( 20, false );
		die.frames( frames, 8, 9, 10, 11, 12, 11 );
		
		attack = new Animation( 15, false );
		attack.frames( frames, 13, 14, 15, 0 );
		
        cast = attack.clone();

		zap = attack.clone();

		operate = new Animation( 8, false );
		operate.frames( frames, 16, 17, 16, 17 );
		
		fly = new Animation( 1, true );
		fly.frames( frames, 18 );

		read = new Animation( 20, false );
		read.frames( frames, 19, 20, 20, 20, 20, 20, 20, 20, 20, 19 );
		
		play( idle );
	}

    @Override
	public void attack( int cell ) {
		if (!Dungeon.level.adjacent(cell, ch.pos)) {

			((MissileSprite)parent.recycle( MissileSprite.class )).
					reset( this, cell, new Ofuda(), new Callback() {
						@Override
						public void call() {
							ch.onAttackComplete();
						}
					} );

			play( cast );
			turnTo( ch.pos , cell );

		} else {

			super.attack( cell );

		}
	}

	public void zap( int cell ) {
		
		turnTo( ch.pos , cell );
		play( zap );
		
		MagicMissile.boltFromChar( parent,
				MagicMissile.MAGIC_MISSILE,
				this,
				cell,
				new Callback() {
					@Override
					public void call() {
						((ReimuBoss)ch).marred();
					}
				} );
		Sample.INSTANCE.play( Assets.Sounds.MISS );
	}
}

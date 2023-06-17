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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Callback;

public class ReisenSprite extends MobSprite {

	private Animation cast;

	public ReisenSprite() {
		super();

		texture( Assets.Sprites.REISEN_MOB );

		TextureFilm frames = new TextureFilm( texture, 12, 16 );

		idle = new MovieClip.Animation( 2, true );
		idle.frames( frames, 0,0 );

		run = new MovieClip.Animation( 12, true );
		run.frames( frames, 0,0 );

		attack = new MovieClip.Animation( 12, false );
		attack.frames( frames, 0,0 );

		cast = attack.clone();

		die = new MovieClip.Animation( 12, false );
		die.frames( frames, 20 );

		play( idle );
	}

	@Override
	public void attack( int cell ) {
		if (!Dungeon.level.adjacent(cell, ch.pos)) {

			((MissileSprite)parent.recycle( MissileSprite.class )).
					reset( this, cell, new Bullet(), new Callback() {
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

	public class Bullet extends Item {
		{
			image = ItemSpriteSheet.BULLET;
		}
	}
}

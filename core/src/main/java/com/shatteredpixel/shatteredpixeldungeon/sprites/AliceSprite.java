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
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.noosa.TextureFilm;

public class AliceSprite extends MobSprite {
	
	public AliceSprite() {
		super();
		
		texture( Assets.Sprites.ALICE );
		
		TextureFilm frames = new TextureFilm( texture, 12, 16 );
		
		idle = new Animation( 15, true );
		idle.frames( frames, 0, 1, 2, 0, 1, 2 );
		
		run = new Animation( 15, true );
		run.frames( frames, 0, 1, 2, 0, 1, 2 );
		
		attack = new Animation( 20, false );
		attack.frames( frames, 0, 1, 2, 0 );
		
		zap = attack.clone();

		die = new Animation( 15, false );
		die.frames( frames, 0, 3, 3, 4, 4 );
		
		play( idle );
	}
	
	@Override
	public int blood() {
		return 0xFF8BA077;
	}

	public void zap( int cell ) {
		
		turnTo( ch.pos , cell );
		play( zap );

		// MagicMissile.boltFromChar( parent,
		// 		MagicMissile.SHADOW,
		// 		this,
		// 		cell,
		// 		new Callback() {
		// 			@Override
		// 			public void call() {
		// 				((Warlock)ch).onZapComplete();
		// 			}
		// 		} );
		Sample.INSTANCE.play( Assets.Sounds.ZAP );
	}
	
	@Override
	public void onComplete( Animation anim ) {
		if (anim == zap) {
			idle();
		}
		super.onComplete( anim );
	}

}

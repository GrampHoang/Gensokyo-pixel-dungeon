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
import com.watabou.noosa.TextureFilm;

public class AyaNPCSprite extends MobSprite {

	public AyaNPCSprite() {
		super();
		
		texture( Assets.Sprites.AYANPC );
		
		TextureFilm frames = new TextureFilm( texture, 13, 15 );
		
		idle = new Animation( 1, true );
		idle.frames( frames, 0, 0, 0, 1, 2, 2, 1, 1 );

		run = new Animation( 1, true );
		run.frames( frames, 0, 0, 1, 0, 0, 1, 1, 2, 2, 0, 1);
		
		operate = new Animation( 1, false );
		operate.frames( frames, 0, 0, 0, 1, 0, 0, 1, 1 );
		
		die = new Animation( 2, false );
		die.frames( frames, 2, 2, 2, 0 );

		play( idle );
	}
}   

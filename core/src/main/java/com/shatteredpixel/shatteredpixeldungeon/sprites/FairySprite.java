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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.particles.Emitter;

public abstract class FairySprite extends MobSprite {

	protected abstract int texOffset();
	
	private Emitter particles;

	public FairySprite() {
		super();
		
		int c = texOffset();

		texture( Assets.Sprites.FAIRY );
		
		TextureFilm frames = new TextureFilm( texture, 13, 14 );	// 13 14
		
		idle = new Animation( 1, true );
		idle.frames( frames, c+0, c+1 );
		
		run = new Animation( 6, true );
		run.frames( frames, c+0, c+1 );

		attack = new Animation( 8, false );
		attack.frames( frames, c+0, c+1);
		
		die = new Animation( 9, false );
		die.frames( frames, c+1, c+0 , c+2);
		
		play(idle);
	}

	protected Emitter createEmitter() {
		Emitter emitter = emitter();
		emitter.pour( MagicMissile.MagicParticle.FACTORY, 0.2f );
		return emitter;
	}
	
	@Override
	public void link( Char ch ) {
		super.link( ch );
		
		if (particles == null) {
			particles = createEmitter();
		}
	}
	@Override
	public void update() {
		super.update();
		
		if (particles != null){
			particles.visible = visible;
		}
	}
	
	@Override
	public void die() {
		super.die();
		if (particles != null){
			particles.on = false;
		}
	}
	
	@Override
	public void kill() {
		super.kill();
		if (particles != null){
			particles.killAndErase();
		}
	}

	public static class Blue extends FairySprite{

		@Override
		protected int texOffset() {
			return 0;
		}
	}

	public static class Red extends FairySprite{

		@Override
		protected int texOffset() {
			return 3;
		}
	}

	public static class Yellow extends FairySprite{

		@Override
		protected int texOffset() {
			return 6;
		}
	}


}
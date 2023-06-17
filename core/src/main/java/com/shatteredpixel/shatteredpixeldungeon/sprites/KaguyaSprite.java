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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou.Kaguya;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class KaguyaSprite extends MobSprite {
	
	private Animation crumple;
	
	public KaguyaSprite() {
		super();
		
		texture( Assets.Sprites.KAGUYA );
		
		TextureFilm frames = new TextureFilm( texture, 12, 14 );
		
		idle = new Animation( 2, true );
		idle.frames( frames, 0, 1 );
		
		run = new Animation( 15, true );
		run.frames( frames, 0, 1 );
		
		attack = new Animation( 12, false );
		attack.frames( frames, 0, 1);
		
		crumple = new Animation( 15, false);
		crumple.frames( frames, 0, 2, 3, 4 );

		zap = attack.clone();
		
		die = new Animation( 15, false );
		die.frames( frames, 0, 2, 3, 4 );
		
		play( idle );
	}
	
	public void zap( int cell ) {
		
		turnTo( ch.pos , cell );
		play( zap );

		MagicMissile.boltFromChar( parent,
				MagicMissile.SPARK_CONE,
				this,
				cell,
				new Callback() {
					@Override
					public void call() {
						((Kaguya)ch).onZapComplete();
					}
				} );
		Sample.INSTANCE.play( Assets.Sounds.ZAP );
	}
	
	public void crumple(){
		hideEmo();
		play(crumple);
	}

	@Override
	public void onComplete( Animation anim ) {
		if (anim == zap) {
			idle();
		}
		super.onComplete( anim );
	}
}

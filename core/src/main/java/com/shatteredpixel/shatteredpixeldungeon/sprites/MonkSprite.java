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

package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SacrificialParticle;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Random;

public class MonkSprite extends MobSprite {
	
	private Animation kick;
	private Emitter spray;

	public MonkSprite() {
		super();
		
		texture( Assets.Sprites.MONK );
		
		TextureFilm frames = new TextureFilm( texture, 15, 14 );
		
		idle = new Animation( 6, true );
		idle.frames( frames, 1, 0, 1, 2 );
		
		run = new Animation( 15, true );
		run.frames( frames, 11, 12, 13, 14, 15, 16 );

		attack = new Animation( 12, false );
		attack.frames( frames, 3, 4, 3, 4 );
		
		kick = new Animation( 10, false );
		kick.frames( frames, 5, 6, 5 );
		
		die = new Animation( 15, false );
		die.frames( frames, 1, 7, 8, 8, 9, 10 );
		
		spray = centerEmitter();
		spray.autoKill = false;
		spray.pour( Speck.factory(Speck.STEAM),0.3f);
		spray.on = false;

		play( idle );
	}

	public void leapPrep( int cell ){
		turnTo( ch.pos, cell );
		play( kick );
	}

	@Override
	public void link(Char ch) {
		super.link(ch);
		if (ch.HP*3 <= ch.HT)
			spray(true);
	}

	public void spray(boolean on){
		spray.on = on;
	}

	@Override
	public void update() {
		super.update();
		spray.pos(center());
		spray.visible = visible;
	}

	@Override
	public void attack( int cell ) {
		super.attack( cell );
		if (Random.Float() < 0.5f) {
			play( kick );
		}
	}
	
	@Override
	public void onComplete( Animation anim ) {
		super.onComplete( anim == kick ? attack : anim );
		if (anim == die) {
			spray.killAndErase();
		}
	}
}

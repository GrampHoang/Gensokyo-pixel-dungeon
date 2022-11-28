/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2021 Evan Debenham
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
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BurstingPowerParticle;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.particles.Emitter;

public class YukariSprite extends MobSprite {

	private Emitter burst;

	public YukariSprite() {
		super();
		
		texture( Assets.Sprites.YUKARI );
		
		TextureFilm frames = new TextureFilm( texture, 12, 15 );
		
		idle = new Animation( 2, true );
		idle.frames( frames, 0, 0, 0, 1, 0, 0, 1, 1 );
		
		run = new Animation( 12, true );
		run.frames( frames, 0, 1 );
		
		attack = new Animation( 12, false );
		attack.frames( frames,  0,1 );
		
		die = new Animation( 12, false );
		die.frames( frames, 0, 1 );
		
		burst = emitter();
		burst.autoKill = false;
		burst.pour(BurstingPowerParticle.FACTORY, 0.1f);
		burst.on = false;

		play( idle );
	}

	@Override
	public void link(Char ch) {
		super.link(ch);
		if (ch.HP*2 <= ch.HT)
		burstfx(true);
	}

	public void burstfx(boolean on){
		burst.on = on;
	}

	@Override
	public void update() {
		super.update();
		burst.visible = visible;
	}

	@Override
	public void onComplete( Animation anim ) {
		super.onComplete( anim );
		if (anim == die) {
			burst.killAndErase();
		}
	}

}

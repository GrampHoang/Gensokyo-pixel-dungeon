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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Warlock;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou.Nitori;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.AquaBlast;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class NitoriSprite extends MobSprite {
	
	public NitoriSprite() {
		super();
		
		texture( Assets.Sprites.NITORI );
		
		TextureFilm frames = new TextureFilm( texture, 12, 15 );
		
		idle = new Animation( 2, true );
		idle.frames( frames, 0, 1);
		
		run = new Animation( 15, true );
		run.frames( frames, 0, 1 );
		
		attack = new Animation( 12, false );
		attack.frames( frames, 0, 1 );
		
		zap = attack.clone();
		
		die = new Animation( 15, false );
		die.frames( frames, 0, 1);
		
		play( idle );
	}
	
	public void zap( int cell ) {
		
		turnTo( ch.pos , cell );
		play( zap );
        
        ((MissileSprite) parent.recycle(MissileSprite.class)).
					reset(this,
							cell,
							new AquaBlast(),
							new Callback() {
						@Override
						public void call() {
							((Nitori)ch).onZapComplete();
						}
					});


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

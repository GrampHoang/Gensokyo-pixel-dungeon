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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Elemental;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou.MagicBook;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.RainbowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Callback;

public abstract class BookSprite extends MobSprite {
	
	protected int boltType;
	
	
	private Emitter particles;
	protected abstract Emitter createEmitter();
	
	public BookSprite() {
		super();
		
		texture( Assets.Sprites.BOOK );
		
		TextureFilm frames = new TextureFilm( texture, 15, 15 );
		
		idle = new Animation( 3, true );
		idle.frames( frames, 0, 1, 0 );
		
		run = new Animation( 5, true );
		run.frames( frames, 0, 1, 0 );
		
		attack = new Animation( 14, false );
		attack.frames( frames, 1, 2, 3, 6 , 6, 3, 2);
		
		zap = attack.clone();
		
		die = new Animation( 15, false );
		die.frames( frames, 7, 8, 9, 10, 11, 10);
		
		play( idle );
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
	
	public void zap( int cell ) {
		
		turnTo( ch.pos , cell );
		play( zap );
		
		MagicMissile.boltFromChar( parent,
				boltType,
				this,
				cell,
				new Callback() {
					@Override
					public void call() {
						((MagicBook)ch).onZapComplete();
					}
				} );
		Sample.INSTANCE.play( Assets.Sounds.ZAP );
	}
	
	@Override
	public void onComplete( Animation anim ) {
		if (anim == zap) {
			idle();
		}
		super.onComplete( anim );
	}
	
	public static class Fire extends BookSprite {
		
		{
			boltType = MagicMissile.FIRE;
		}

		@Override
		protected Emitter createEmitter() {
			Emitter emitter = emitter();
			emitter.pour( FlameParticle.FACTORY, 0.06f );
			return emitter;
		}
		
		@Override
		public int blood() {
			return 0xFFFFBB33;
		}
	}
	
	public static class Frost extends BookSprite {
		
		{
			boltType = MagicMissile.FROST;
		}

		@Override
		protected Emitter createEmitter() {
			Emitter emitter = emitter();
			emitter.pour( MagicMissile.MagicParticle.FACTORY, 0.06f );
			return emitter;
		}
		
		@Override
		public int blood() {
			return 0xFF8EE3FF;
		}
	}
	
	public static class Shock extends BookSprite {
		
		//different bolt, so overrides zap
		@Override
		public void zap( int cell ) {
			turnTo( ch.pos , cell );
			play( zap );
			((MagicBook)ch).onZapComplete();
			parent.add( new Beam.LightRay(center(), DungeonTilemap.raisedTileCenterToWorld(cell)));
		}
		
		@Override
		protected Emitter createEmitter() {
			Emitter emitter = emitter();
			emitter.pour( SparkParticle.STATIC, 0.06f );
			return emitter;
		}
		
		@Override
		public int blood() {
			return 0xFFFFFF85;
		}
	}
	
	public static class Chaos extends BookSprite {

		{
			boltType = MagicMissile.RAINBOW;
		}
		
		@Override
		protected Emitter createEmitter() {
			Emitter emitter = emitter();
			emitter.pour( RainbowParticle.BURST, 0.025f );
			return emitter;
		}
		
		@Override
		public int blood() {
			return 0xFFE3E3E3;
		}
	}
}

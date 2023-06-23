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

package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.ColorMath;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import com.watabou.utils.SparseArray;

public class SpeckTouhou extends Image {

	public static final int STAR        = 1;
	public static final int STAR_MS		= 15;
	public static final int STAR_FLY	= 16;
	public static final int STAR_HAKKERO= 17;

	public static final int STAR_CIRCLE_1 = 201;
	public static final int STAR_CIRCLE_2 = 202;
	public static final int STAR_CIRCLE_3 = 203;
	public static final int STAR_CIRCLE_4 = 204;
	public static final int STAR_CIRCLE_5 = 205;
	public static final int STAR_CIRCLE_6 = 206;
	public static final int STAR_CIRCLE_7 = 207;
	public static final int STAR_CIRCLE_8 = 208;
	public static final int STAR_CIRCLE_9 = 209;

    public static final int STAR_CIRCLE_11 = 211;
	public static final int STAR_CIRCLE_12 = 212;
	public static final int STAR_CIRCLE_13 = 213;
	public static final int STAR_CIRCLE_14 = 214;
	public static final int STAR_CIRCLE_15 = 215;
	public static final int STAR_CIRCLE_16 = 216;
	public static final int STAR_CIRCLE_17 = 217;
	public static final int STAR_CIRCLE_18 = 218;
	public static final int STAR_CIRCLE_19 = 219;

	private static final int SIZE = 7;
	
	private int type;
	private float lifespan;
	private float left;
	
	private static TextureFilm film;
	
	private static SparseArray<Emitter.Factory> factories = new SparseArray<>();
	
	public SpeckTouhou() {
		super();
		
		texture( Assets.Effects.SPECKS );
		if (film == null) {
			film = new TextureFilm( texture, SIZE, SIZE );
		}
		
		origin.set( SIZE / 2f );
	}

	public SpeckTouhou image( int type ){
		reset(0, 0, 0, type);

		left = lifespan = Float.POSITIVE_INFINITY;
		this.type = -1;

		resetColor();
		scale.set( 1 );
		speed.set( 0 );
		acc.set( 0 );
		angle = 0;
		angularSpeed = 0;

		return this;
	}
	
	public void reset( int index, float x, float y, int type ) {
		revive();
		this.type = type;
		switch (type) {
		
		case STAR_MS:
		case STAR_FLY:
		case STAR_HAKKERO:
		case STAR_CIRCLE_1:
		case STAR_CIRCLE_2:
		case STAR_CIRCLE_3:
		case STAR_CIRCLE_4:
		case STAR_CIRCLE_5:
		case STAR_CIRCLE_6:
		case STAR_CIRCLE_7:
		case STAR_CIRCLE_8:
		case STAR_CIRCLE_9:
        case STAR_CIRCLE_11:
		case STAR_CIRCLE_12:
		case STAR_CIRCLE_13:
		case STAR_CIRCLE_14:
		case STAR_CIRCLE_15:
		case STAR_CIRCLE_16:
		case STAR_CIRCLE_17:
		case STAR_CIRCLE_18:
		case STAR_CIRCLE_19:
			frame( film.get( STAR ) );
			break;
        }
		this.x = x - origin.x;
		this.y = y - origin.y;
		
		resetColor();
		scale.set( 1 );
		speed.set( 0 );
		acc.set( 0 );
		angle = 0;
		angularSpeed = 0;
		
		switch (type) {			
		case STAR:
			speed.polar( Random.Float( 2 * 3.1415926f ), Random.Float( 128 ) );
			acc.set( 0, 128 );
			angle = Random.Float( 360 );
			angularSpeed = Random.Float( -360, +360 );
			lifespan = 1f;
			break;

		case STAR_MS:
			speed.set( Random.IntRange(-20,20), Random.IntRange(-20,20));
			lifespan = 1f;
			angle = Random.Float( 360 );
			angularSpeed = Random.Float( -360, +360 );
			break;

		case STAR_HAKKERO:
			speed.set( Random.IntRange(-2,2), Random.IntRange(-2, 2));
			lifespan = 0.2f;
			angle = Random.Float( 360 );
			angularSpeed = Random.Float( -360, +360 );
			break;

		case STAR_FLY:
			speed.set( Random.IntRange(-20,20), Random.IntRange(10,30));
			lifespan = 0.3f;
			angle = Random.Float( 360 );
			angularSpeed = Random.Float( -360, +360 );
			break;
		
		case STAR_CIRCLE_1:
			speed.polar( 3.1415926f / 5, 30 );
			acc.set( -speed.x*1.3f, -speed.y*1.3f);
			lifespan = 1f;
			angularSpeed = 360;
			break;
		case STAR_CIRCLE_2:
			speed.polar( 2 * 3.1415926f / 5, 30 );
			acc.set( -speed.x*1.3f, -speed.y*1.3f);
			lifespan = 1f;
			angularSpeed = 360;
			break;
		case STAR_CIRCLE_3:
			speed.polar(  3 * 3.1415926f / 5, 30 );
			acc.set( -speed.x*1.3f, -speed.y*1.3f);
			lifespan = 1f;
			angularSpeed = 360;
			break;
		case STAR_CIRCLE_4:
			speed.polar(  4 * 3.1415926f / 5, 30 );
			acc.set( -speed.x*1.3f, -speed.y*1.3f);
			lifespan = 1f;
			angularSpeed = 360;
			break;
		case STAR_CIRCLE_5:
			speed.polar(  5 * 3.1415926f / 5, 30 );
			acc.set( -speed.x*1.3f, -speed.y*1.3f);
			lifespan = 1f;
			angularSpeed = 360;
			break;
		case STAR_CIRCLE_6:
			speed.polar(  6 * 3.1415926f / 5, 30 );
			acc.set( -speed.x*1.3f, -speed.y*1.3f);
			lifespan = 1f;
			angularSpeed = 360;
			break;
		case STAR_CIRCLE_7:
			speed.polar(  7 * 3.1415926f / 5, 30 );
			acc.set( -speed.x*1.3f, -speed.y*1.3f);
			lifespan = 1f;
			angularSpeed = 360;
			break;
		case STAR_CIRCLE_8:
			speed.polar(  8 * 3.1415926f / 5, 30 );
			acc.set( -speed.x*1.3f, -speed.y*1.3f);
			lifespan = 1f;
			angularSpeed = 360;
			break;
		case STAR_CIRCLE_9:
			speed.polar(  9 * 3.1415926f / 5, 30 );
			acc.set( -speed.x*1.3f, -speed.y*1.3f);
			lifespan = 1f;
			angularSpeed = 360;
			break;

        case STAR_CIRCLE_11:
			speed.polar( 3.1415926f / 5, 10 );
			acc.set( speed.x*3f, speed.y*3f);
			lifespan = 1.5f;
			angularSpeed = 360;
			break;
		case STAR_CIRCLE_12:
			speed.polar( 2 * 3.1415926f / 5, 10);
			acc.set( speed.x*3f, speed.y*3f);
			lifespan = 1.5f;
			angularSpeed = 360;
			break;
		case STAR_CIRCLE_13:
			speed.polar(  3 * 3.1415926f / 5, 10);
			acc.set( speed.x*3f, speed.y*3f);
			lifespan = 1.5f;
			angularSpeed = 360;
			break;
		case STAR_CIRCLE_14:
			speed.polar(  4 * 3.1415926f / 5, 10);
			acc.set( speed.x*3f, speed.y*3f);
			lifespan = 1.5f;
			angularSpeed = 360;
			break;
		case STAR_CIRCLE_15:
			speed.polar(  5 * 3.1415926f / 5, 10);
			acc.set( speed.x*3f, speed.y*3f);
			lifespan = 1.5f;
			angularSpeed = 360;
			break;
		case STAR_CIRCLE_16:
			speed.polar(  6 * 3.1415926f / 5, 10);
			acc.set( speed.x*3f, speed.y*3f);
			lifespan = 1.5f;
			angularSpeed = 360;
			break;
		case STAR_CIRCLE_17:
			speed.polar(  7 * 3.1415926f / 5, 10);
			acc.set( speed.x*3f, speed.y*3f);
			lifespan = 1.5f;
			angularSpeed = 360;
			break;
		case STAR_CIRCLE_18:
			speed.polar(  8 * 3.1415926f / 5, 10);
			acc.set( speed.x*3f, speed.y*3f);
			lifespan = 1.5f;
			angularSpeed = 360;
			break;
		case STAR_CIRCLE_19:
			speed.polar(  9 * 3.1415926f / 5, 10);
			acc.set( speed.x*3f, speed.y*3f);
			lifespan = 1.5f;
			angularSpeed = 360;
			break;
		}
		left = lifespan;
	}
	
	@Override
	public void update() {
		super.update();
		
		left -= Game.elapsed;
		if (left <= 0) {
			
			kill();
			
		} else {
			
			float p = 1 - left / lifespan;	// 0 -> 1
			
			switch (type) {
				
			case STAR:
			case STAR_CIRCLE_1:
			case STAR_CIRCLE_2:
			case STAR_CIRCLE_3:
			case STAR_CIRCLE_4:
			case STAR_CIRCLE_5:
			case STAR_CIRCLE_6:
			case STAR_CIRCLE_7:
			case STAR_CIRCLE_8:
			case STAR_CIRCLE_9:
				am = 1;
				break;
            }
		}
	}

	public static Emitter.Factory factory( final int type ) {
		return factory( type, false );
	}

	public static Emitter.Factory factory( final int type, final boolean lightMode ) {

		Emitter.Factory factory = factories.get( type );

		if (factory == null) {
			factory = new Emitter.Factory() {
				@Override
				public void emit ( Emitter emitter, int index, float x, float y ) {
					SpeckTouhou p = (SpeckTouhou)emitter.recycle( SpeckTouhou.class );
					p.reset( index, x, y, type );
				}
				@Override
				public boolean lightMode() {
					return lightMode;
				}
			};
			factories.put( type, factory );
		}

		return factory;
	}
}

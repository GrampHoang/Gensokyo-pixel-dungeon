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
import com.watabou.glwrap.Blending;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PointF;


public class MasterSparkBig extends Image {

	private static final double A = 180 / Math.PI;
	
	private  float duration;
	
	private float timeLeft;
	private MasterSparkBig(PointF s, PointF e, Effects.Type asset, float duration) {
		super( Effects.get( asset ) );
		
		origin.set( 0, height / 2 );
		
		x = s.x - origin.x;
		y = s.y - origin.y;
		
		float dx = e.x - s.x;
		float dy = e.y - s.y;
		angle = (float)(Math.atan2( dy, dx ) * A);
		scale.x = (float)Math.sqrt( dx * dx + dy * dy ) / width * 1.1f;
		
		Sample.INSTANCE.play( Assets.Sounds.RAY );
		
		timeLeft = this.duration = duration;
	}

	public static class BigMasterSpark extends MasterSparkBig{
		public BigMasterSpark(PointF s, PointF e, float dur){
			super(s, e, Effects.Type.LIGHT_RAY, dur);
		}
	}
    
	@Override
	public void update() {
		super.update();
		//float size = 2f;
		float p = 2f - 2f/((float)Math.pow(5,Math.sqrt(timeLeft)));
		alpha( p );
		scale.set( scale.x, p );
		
		if ((timeLeft -= Game.elapsed) <= 0) {
			killAndErase();
		}
	}
	
	@Override
	public void draw() {
		Blending.setLightMode(); 
		super.draw();
		Blending.setNormalMode();
	}
}

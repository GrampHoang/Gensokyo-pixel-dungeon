
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class YoumuSprite extends MobSprite {
	
	public YoumuSprite() {
		super();
		
		texture( Assets.Sprites.YOUMU );
		
		TextureFilm frames = new TextureFilm( texture, 13, 16 );
		
		idle = new Animation( 2, true );
		idle.frames( frames, 0, 0, 0, 1 );
		
		run = new Animation( 10, true );
		run.frames( frames, 0, 1 );
		
		attack = new Animation( 15, false );
		attack.frames( frames, 0, 1 );
		
		die = new Animation( 10, false );
		die.frames( frames, 0, 4 );
		
		play( idle );
	}
}

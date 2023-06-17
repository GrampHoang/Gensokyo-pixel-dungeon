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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class RumiaRibbon extends MeleeWeapon {

	{
		image = ItemSpriteSheet.RUMIA_RIBBON;
		hitSound = Assets.Sounds.HIT_CRUSH;
		hitSoundPitch = 1f;

		tier = 3;
		ACC = 0.8f; //28% boost to accuracy
	}

	@Override
	public int max(int lvl) {
		return  5*(tier+1) +    //20
				lvl*(tier+1);   //scaling unchanged
	}

    @Override
	public int proc(Char attacker, Char defender, int damage) {
        //chance to heal scales from 10%-40% based on missing 
        if(attacker.HT > attacker.HP*2){
            float missingPercent = (attacker.HT - attacker.HP) / (float)attacker.HT;
            float healChance = 0.1f + .3f*missingPercent;

            if (Random.Float() < healChance){
                
                //heals for 60% of damage dealt
                int healAmt = Math.round(damage * 0.6f);
                healAmt = Math.min( healAmt, attacker.HT - attacker.HP );
                
                if (healAmt > 0 && attacker.isAlive()) {
                    
                    attacker.HP += healAmt;
                    attacker.sprite.emitter().start( Speck.factory( Speck.HEALING ), 0.4f, 1 );
                    attacker.sprite.showStatus( CharSprite.POSITIVE, Integer.toString( healAmt ) );
                    
                }
            }
        }
        return super.proc(attacker, defender, damage);
	}

}

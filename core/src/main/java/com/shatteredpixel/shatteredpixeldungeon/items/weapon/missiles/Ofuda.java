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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Ofuda extends MissileWeapon {

	{
		image = ItemSpriteSheet.OFUDA;
		hitSound = Assets.Sounds.CHARGEUP;
		hitSoundPitch = 0.9f;
		
		bones = false;
		
		tier = 1;
		baseUses = 1;
		sticky = false;
	}

	Weapon wep = (Weapon)Dungeon.hero.belongings.weapon;
	
	@Override
	public int min(int lvl) {
		if (wep.augment == Augment.SPEED){
            return wep.min()/2;
        }
        else {
            return wep.min();
        }
    }
	
	@Override
	public int max(int lvl) {
		if (wep.augment == Augment.SPEED){
            return wep.max()/2;
        }
        else {
            return wep.max();
        }
	}
	
    @Override
	public int min() {
		if (wep.augment == Augment.SPEED){
            return wep.min()/2;
        }
        else {
            return wep.min();
        }
    }
	
	@Override
	public int max() {
		if (wep.augment == Augment.SPEED){
            return wep.max()/2;
        }
        else {
            return wep.max();
        }
	}

    @Override
	public float castDelay(Char user, int dst) {
		if (wep.augment == Augment.SPEED){
            return 0.5f;
        }
        else {
            return 1f;
        }
	}

	@Override
	public int proc( Char attacker, Char defender, int damage ) {
			if (Dungeon.hero.hasTalent(Talent.LUCKY_SHOT)){
				if(Random.IntRange(0,99) < 15 + 5 * Dungeon.hero.pointsInTalent(Talent.LUCKY_SHOT)){
					Buff.affect( defender, Bleeding.class ).set( Math.round(damage*0.3f) );
				}
				if(wep.enchantment != null
					&& Random.IntRange(0,2) < Dungeon.hero.pointsInTalent(Talent.LUCKY_SHOT)
					&& attacker.buff(MagicImmune.class) == null ){
					wep.enchantment.proc(wep, attacker, defender, damage);
				}
			} else {
				if(Random.IntRange(0,9) < 1){
					Buff.affect( defender, Bleeding.class ).set( Math.round(damage*0.3f) );
				}
			}

			if (Dungeon.hero.hasTalent(Talent.SEALCRIP)){
				if(Random.IntRange(0,9) < Dungeon.hero.pointsInTalent(Talent.SEALCRIP)){
					Buff.prolong( defender, Cripple.class, 4f);
				}
				if(Random.IntRange(0,19) < Dungeon.hero.pointsInTalent(Talent.SEALCRIP)){
					Buff.prolong( defender, Slow.class, 4f);
				}
			}
			
		return super.proc( attacker, defender, damage );
	}

	@Override
	protected void onThrow( int cell ) {
		Char enemy = Actor.findChar( cell );
		if (enemy == null || enemy == curUser) {
				parent = null;
				super.onThrow( cell );
		} else {
			curUser.shoot( enemy, this );
		}
	}
}

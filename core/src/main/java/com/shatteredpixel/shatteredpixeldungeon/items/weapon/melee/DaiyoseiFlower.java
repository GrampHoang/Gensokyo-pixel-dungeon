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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

public class DaiyoseiFlower extends WeaponWithSP {

	{
		image = ItemSpriteSheet.DAIYOSEI_FLOWER;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1f;

		tier = 2;
		DLY = 1f;

        chargeGain = 8;
    }

	public static int HEAL_PERCENTAGE = 20;

	@Override
	public int max(int lvl) {
		return  Math.round(4f*(tier+1)) - 2 + //10 base
				lvl*Math.round(1f*(tier+1)); // no change scale
	}

    @Override
	protected boolean useSkill(){
        Hero hero = Dungeon.hero;
		hero.sprite.emitter().start( Speck.factory( Speck.HEALING ), 0.4f, 3 );
		int healAmount = hero.HT * HEAL_PERCENTAGE / 100;
		hero.HP += Math.round(healAmount);
        if(hero.HP > hero.HT){
			healAmount = hero.HT - hero.HP + healAmount;
			hero.HP = hero.HT;
		}
		curUser.sprite.showStatus(CharSprite.POSITIVE, Integer.toString(healAmount));
		Dungeon.hero.spendAndNext(1f);
        return true;
	}

	@Override
	public String skillInfo(){
		return Messages.get(DaiyoseiFlower.class, "skill_desc", chargeGain, chargeNeed, HEAL_PERCENTAGE);
	}
}

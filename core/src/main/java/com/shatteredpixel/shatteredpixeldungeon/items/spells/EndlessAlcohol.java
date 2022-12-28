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

package com.shatteredpixel.shatteredpixeldungeon.items.spells;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLevitation;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WeaponWithSP;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;

public class EndlessAlcohol extends Spell {
	
	{
		image = ItemSpriteSheet.VIAL;
	}
	
	@Override
	protected void onCast(Hero hero) {
        hero.busy();
        KindOfWeapon weapon = hero.belongings.weapon();
		if (weapon instanceof WeaponWithSP){
			if (((WeaponWithSP)weapon).refundSP(100) ){
				GLog.p("Your weapon gain 100% charge!");
				hero.sprite.operate(hero.pos);
				Sample.INSTANCE.play( Assets.Sounds.DRINK );
				hero.spend( 1f );
				detach( curUser.belongings.backpack );
				updateQuickslot();
			} else {
				hero.next();
			}
		}  else {
            GLog.p("Your weapon don't have skill");
			hero.next();
		// GLog.p(Messages.get(this, "not_suitable"));
        }
        
		
	}
	
	@Override
	public int value() {
		//prices of ingredients, divided by output quantity
		return Math.round(quantity * ((30 + 40) / 2f));
	}
	
	public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {
		
		{
			inputs =  new Class[]{PotionOfHealing.class, Alchemize.class};
			inQuantity = new int[]{1, 1};
			
			cost = 10;
			
			output = EndlessAlcohol.class;
			outQuantity = 1;
		}
		
	}
}

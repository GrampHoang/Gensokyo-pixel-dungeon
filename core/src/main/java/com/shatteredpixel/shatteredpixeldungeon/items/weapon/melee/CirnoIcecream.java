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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Chilling;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class CirnoIcecream extends WeaponWithSP {

	{
		image = ItemSpriteSheet.CIRNO_ICECREAM;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1f;

		tier = 2;
		DLY = 1f;

        chargeGain = 5;

		Chilling chill = new Chilling();
		enchant((Weapon.Enchantment)chill);

		usesTargeting = false;
    }

	// public int HUNGER_FILL = 100 + charge/2 ;

	@Override
	public ItemSprite.Glowing glowing() {
		return enchantment != null && (cursedKnown || !enchantment.curse()) ?
				new ItemSprite.Glowing(enchantment.glowing().color, 0.66f*enchantment.glowing().period) : LIGHT_BLUE;
	}

	private static ItemSprite.Glowing LIGHT_BLUE = new ItemSprite.Glowing( 0xadd8e6, 0.33f );

	@Override
	public int max(int lvl) {
		return  Math.round(4f*(tier+1)) - 1 + //11 base
				lvl*Math.round(1f*(tier+1)); // no change scale
	}

    @Override
	public int proc(Char attacker, Char defender, int damage) {
        if ( Random.IntRange(0, 9) == 1){
            Buff.affect(defender, Chill.class, 3f );
        }
        return super.proc(attacker, defender, damage);
	}

    @Override
	protected boolean useSkill(){
        Buff.affect(Dungeon.hero, Hunger.class).satisfy(50);
		// new Flare( 5, 32 ).color( 0xFF0000, true ).show( curUser.sprite, 2f );
		Buff.affect( Dungeon.hero, Chill.class, 6f );
		Dungeon.hero.spendAndNext(1f);
        return true;
	}

    public static final String AC_DEVOUR = "DEVOUR";

    @Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (isEquipped( hero ))
			actions.add(AC_DEVOUR);
		return actions;
	}

    @Override
	public void execute(Hero hero, String action ) {
		super.execute(hero, action);
		if (action.equals(AC_DEVOUR)){
            if(cursed && cursedKnown){
                GLog.w("Don't eat cursed ice-cream");
            } else {
				Buff.affect(Dungeon.hero, Hunger.class).satisfy( 100 + charge/2 );
                new Flare( 10, 32 ).color( 0xadd8e6, true ).show( curUser.sprite, 0.3f );
				Dungeon.quickslot.clearItem(curItem);
                updateQuickslot();
                Buff.affect(Dungeon.hero, Frost.class, 8f);
                if(cursed) MysteryMeat.effect(Dungeon.hero);
				curItem = null;
            }
		}
	}
	
	@Override
	public String skillInfo(){
		return Messages.get(CirnoIcecream.class, "skill_desc", chargeGain, chargeNeed, 100 + charge/2);
	}

}

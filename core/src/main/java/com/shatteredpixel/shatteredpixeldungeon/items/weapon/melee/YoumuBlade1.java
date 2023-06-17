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
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.YinYang;
import com.shatteredpixel.shatteredpixeldungeon.items.encounters.YoumuEnc;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

public class YoumuBlade1 extends MeleeWeapon {
	
	{
		image = ItemSpriteSheet.GARDENDER_BLADE;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 2f;
		ACC = 2;
		tier = 4;
	}

	public int kill_count = 0;

	@Override
	public String status() {
		
		//if isn't IDed, or is cursed, don't display anything
		//If never seen YoumuEnc, display nothing. BUT it still work and count
		if (!isIdentified() || cursed || !Catalog.isSeen(YoumuEnc.class)){
			return null;
		}
		return Integer.toString(kill_count);
	}

	public void kill(KindOfWeapon blade){
		kill_count++;
		if (kill_count > 100){
			evolve(blade);
		}
	}

	public void evolve(KindOfWeapon blade){
		YoumuBlade1 hba = (blade instanceof YoumuBlade2) ? new YoumuBlade3() : new YoumuBlade2();
		//Upgrade lvl
		hba.level(blade.trueLevel());
		//Transfer Yinyang if Reimu
		YinYang yy = ((MeleeWeapon)blade).checkYinYang();
		if (yy != null){
			hba.affixSeal(yy);
		}
		//Transfer Enchant
		if (((MeleeWeapon)blade).getEnchant() != null){
			Enchantment ench = ((MeleeWeapon)blade).getEnchant();
			hba.enchant(ench);
		}
		//Tranfer Augment
		hba.augment = ((Weapon)blade).augment;

		hba.identify();
		
		if (blade.isEquipped( Dungeon.hero )) {
			((EquipableItem)blade).doUnequip( Dungeon.hero, false );
			blade.detach( Dungeon.hero.belongings.backpack );
			Dungeon.hero.belongings.weapon = hba;
		} else {
			blade.detach( Dungeon.hero.belongings.backpack );
			hba.collect();
		}
		GLog.p("You weapon became stronger!");
	}

	@Override
	public String info() {
		String sup = super.info();
		if (Catalog.isSeen(YoumuEnc.class)){
			sup = Messages.get(YoumuBlade1.class, "youmu_found") + evolve_desc() + "\n\n" + sup;
		}
		return sup;
	}
	
	public String evolve_desc() {
		return " " + Messages.get(YoumuBlade1.class, "youmu_found2");
	}

    @Override
	public int max(int lvl) {
		return  Math.round(3f*(tier+1)) + 3		//base 18, from 25
				+ lvl*(tier);						//4 per level, from 5
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( "COUNT", kill_count );
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		kill_count = bundle.getInt( "COUNT" );
	}
}

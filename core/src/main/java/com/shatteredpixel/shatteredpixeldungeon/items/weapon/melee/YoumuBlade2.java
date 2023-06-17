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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.YinYang;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class YoumuBlade2 extends YoumuBlade1{
	{
		image = ItemSpriteSheet.GHOST_FRIEND;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 2f;
		ACC = 999;
		tier = 4;
	}
	
	// @Override
	// public void kill(){
	// 	kill_count++;
	// 	if (kill_count > 2){
	// 		evolve(this);
	// 	}
	// }

	// public void evolve(YoumuBlade2 blade){
	// 	YoumuBlade2 hba = new YoumuBlade3();
	// 	//Upgrade lvl
	// 	hba.level(blade.trueLevel());
	// 	//Transfer Yinyang if Reimu
	// 	YinYang yy = ((MeleeWeapon)blade).checkYinYang();
	// 	if (yy != null){
	// 		hba.affixSeal(yy);
	// 	}
	// 	//Transfer Enchant
	// 	if (((MeleeWeapon)blade).getEnchant() != null){
	// 		Enchantment ench = ((MeleeWeapon)blade).getEnchant();
	// 		hba.enchant(ench);
	// 	}
	// 	//Tranfer Augment
	// 	hba.augment = ((Weapon)blade).augment;

	// 	hba.identify();
		
	// 	if (blade.isEquipped( Dungeon.hero )) {
	// 		((EquipableItem)blade).doUnequip( Dungeon.hero, false );
	// 		blade.detach( Dungeon.hero.belongings.backpack );
	// 		Dungeon.hero.belongings.weapon = hba;
	// 	} else {
	// 		blade.detach( Dungeon.hero.belongings.backpack );
	// 		hba.collect();
	// 	}
	// 	// detach(curUser.belongings.backpack);
	// 	GLog.p("You weapon became stronger!");
	// }
}

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

package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.DungeonSeed;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.GameSettings;
import com.watabou.utils.Point;

import java.util.Locale;

public class UFOSettings extends GameSettings {
	public static final String RED_HP 	    	= "red_hp";
	public static final String RED_ACC 	    	= "red_acc";
	public static final String RED_EVA 	    	= "red_eva";
	public static final String RED_HUNGER 	    = "red_hunger";

	public static final String RED_VISION	    = "red_vision";
	public static final String RED_ATTSPEED 	= "red_attpseed";
	public static final String RED_QUEST 	    = "red_quest";
	public static final String RED_GOLD 	    = "red_gold";

	public static final String RED_ITEM 	    = "red_item";
	public static final String RED_SEARCH 	    = "red_search";
	public static final String RED_MOBS 	    = "red_mobs";
	public static final String RED_SHOP 	    = "red_shop";

    //Red UFO
	//quest
	public static boolean red_Quest() {
		return getBoolean( RED_QUEST,  false);
	}
	public static void red_Quest( boolean value ) {
		put( RED_QUEST, value );
	}
	//HP
	public static boolean red_HP() {
		return getBoolean( RED_HP,  false);
	}
	public static void red_HP( boolean value ) {
		put( RED_HP, value );
	}
	//Hunger
	public static boolean red_Hunger() {
		return getBoolean( RED_HUNGER,  false);
	}
	public static void red_Hunger( boolean value ) {
		put( RED_HUNGER, value );
	}
	//Attack Speed
	public static boolean red_AttSpeed() {
		return getBoolean( RED_ATTSPEED,  false);
	}
	public static void red_AttSpeed( boolean value ) {
		put( RED_ATTSPEED, value );
	}
	//Gold
	public static boolean red_Gold() {
		return getBoolean( RED_GOLD,  false);
	}
	public static void red_Gold( boolean value ) {
		put( RED_GOLD, value );
	}
	//Item
	public static boolean red_Item() {
		return getBoolean( RED_ITEM,  false);
	}
	public static void red_Item( boolean value ) {
		put( RED_ITEM, value );
	}
	//Accuracy
	public static boolean red_Acc() {
		return getBoolean( RED_ACC,  false);
	}
	public static void red_Acc( boolean value ) {
		put( RED_ACC, value );
	}
	//Accuracy
	public static boolean red_Eva() {
		return getBoolean( RED_EVA,  false);
	}
	public static void red_Eva( boolean value ) {
		put( RED_EVA, value );
	}
	//Vision
	public static boolean red_Vision() {
		return getBoolean( RED_VISION,  false);
	}
	public static void red_Vision( boolean value ) {
		put( RED_VISION, value );
	}
	//Search
	public static boolean red_Search() {
		return getBoolean( RED_SEARCH,  false);
	}
	public static void red_Search( boolean value ) {
		put( RED_SEARCH, value );
	}
	//Mob
	public static boolean red_Mobs() {
		return getBoolean( RED_MOBS,  false);
	}
	public static void red_Mobs( boolean value ) {
		put( RED_MOBS, value );
	}
	//Shop
	public static boolean red_Shop() {
		return getBoolean( RED_SHOP,  false);
	}
	public static void red_Shop( boolean value ) {
		put( RED_SHOP, value );
	}

}

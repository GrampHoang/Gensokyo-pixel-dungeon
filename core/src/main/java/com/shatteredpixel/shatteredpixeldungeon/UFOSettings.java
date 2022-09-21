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
	public static final String RED_UFO 	    	= "red_ufo";
	public static final String BLUE_UFO 	    	= "blue_ufo";
	public static final String GREEN_UFO 	    	= "green_ufo";

	public static int getRed(){
		return getInt(RED_UFO, 0);
	}
	public static void changeRed(int value){
		put( RED_UFO, getRed() + value );
	}


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





	public static final String BLUE_HP 	    	= "blue_hp";
	public static final String BLUE_RUNSPEED 	= "blue_runspeed";
	public static final String BLUE_HUNGER 	    = "blue_hunger";

	public static final String BLUE_SEARCH 	    = "blue_search";
	public static final String BLUE_QUEST 	    = "blue_quest";

	public static final String BLUE_ACC		    = "blue_acc";
	public static final String BLUE_DAMAGE 	    = "blue_damage";

	public static final String BLUE_GOLD 	    = "blue_gold";
	public static final String BLUE_ITEM 	    = "blue_item";

    //Blue UFO
	//HP
	public static boolean blue_HP() {
		return getBoolean( BLUE_HP,  false);
	}
	public static void blue_HP( boolean value ) {
		put( BLUE_HP, value );
	}
	//Hunger
	public static boolean blue_Hunger() {
		return getBoolean( BLUE_HUNGER,  false);
	}
	public static void blue_Hunger( boolean value ) {
		put( BLUE_HUNGER, value );
	}
	//Run Speed
	public static boolean blue_RunSpeed() {
		return getBoolean( BLUE_RUNSPEED,  false);
	}
	public static void blue_RunSpeed( boolean value ) {
		put( BLUE_RUNSPEED, value );
	}

	//Quest
	public static boolean blue_Quest() {
		return getBoolean( BLUE_QUEST,  false);
	}
	public static void blue_Quest( boolean value ) {
		put( BLUE_QUEST, value );
	}
	//Search
	public static boolean blue_Search() {
		return getBoolean( BLUE_SEARCH,  false);
	}
	public static void blue_Search( boolean value ) {
		put( BLUE_SEARCH, value );
	}

	//Accuracy
	public static boolean blue_Acc() {
		return getBoolean( BLUE_ACC,  false);
	}
	public static void blue_Acc( boolean value ) {
		put( BLUE_ACC, value );
	}
	//Damage
	public static boolean blue_Damage() {
		return getBoolean( BLUE_DAMAGE,  false);
	}
	public static void blue_Damage( boolean value ) {
		put( BLUE_DAMAGE, value );
	}

	//Gold
	public static boolean blue_Gold() {
		return getBoolean( BLUE_GOLD,  false);
	}
	public static void blue_Gold( boolean value ) {
		put( BLUE_GOLD, value );
	}
	//Item
	public static boolean blue_Item() {
		return getBoolean( BLUE_ITEM,  false);
	}
	public static void blue_Item( boolean value ) {
		put( BLUE_ITEM, value );
	}


	public static final String GREEN_RUNSPEED  	= "green_runspeed";
	public static final String GREEN_SEARCH 	= "green_search";
	public static final String GREEN_HP 	    = "green_hp";

	public static final String GREEN_ACC 	    = "green_ac";
	public static final String GREEN_EVA 	    = "green_eva";

	public static final String GREEN_GOLD		= "green_gold";
	public static final String GREEN_QUEST 	    = "green_quest";

	public static final String GREEN_ITEM	    = "green_item";
	public static final String GREEN_SHOP 	    = "green_shop";
	public static final String GREEN_STRENGTH 	= "green_strength";

	public static boolean green_RunSpeed() {
		return getBoolean( GREEN_RUNSPEED,  false);
	}
	public static void green_RunSpeed( boolean value ) {
		put( GREEN_RUNSPEED, value );
	}
	public static boolean green_Search() {
		return getBoolean( GREEN_SEARCH,  false);
	}
	public static void green_Search( boolean value ) {
		put( GREEN_SEARCH, value );
	}
	public static boolean green_HP() {
		return getBoolean( GREEN_HP,  false);
	}
	public static void green_HP( boolean value ) {
		put( GREEN_HP, value );
	}
	public static boolean green_Acc() {
		return getBoolean( GREEN_ACC,  false);
	}
	public static void green_Acc( boolean value ) {
		put( GREEN_ACC, value );
	}
	public static boolean green_Eva() {
		return getBoolean( GREEN_EVA,  false);
	}
	public static void green_Eva( boolean value ) {
		put( GREEN_EVA, value );
	}
	public static boolean green_Shop() {
		return getBoolean( GREEN_SHOP,  false);
	}
	public static void green_Shop( boolean value ) {
		put( GREEN_SHOP, value );
	}
	public static boolean green_Gold() {
		return getBoolean( GREEN_GOLD,  false);
	}
	public static void green_Gold( boolean value ) {
		put( GREEN_GOLD, value );
	}
	public static boolean green_Quest() {
		return getBoolean( GREEN_QUEST,  false);
	}
	public static void green_Quest( boolean value ) {
		put( GREEN_QUEST, value );
	}
	public static boolean green_Item() {
		return getBoolean( GREEN_ITEM,  false);
	}
	public static void green_Item( boolean value ) {
		put( GREEN_ITEM, value );
	}
	public static boolean green_Strength() {
		return getBoolean( GREEN_STRENGTH,  false);
	}
	public static void green_Strength( boolean value ) {
		put( GREEN_STRENGTH, value );
	}

}

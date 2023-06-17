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

package com.shatteredpixel.shatteredpixeldungeon.items.encounters;

import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class TenshiEnc extends EncounterNotes {
    {
		image = ItemSpriteSheet.HISOUBLADE_FIRE;
	}

	public void setSeen(){
		Catalog.setSeen(TenshiEnc.class);
	}

	public String npc(){
		return "Tenshi";
	}

	// Floor 15-19, standing in plain view
	// you will fight her
	// get reward based on how well you do
	// EncNote: Flawless fight (take 0 hit) or survive looooong enough (till the end of scarlet weather)
	// Unlock: Awaken Heaven's sword with 1 fire Oath
}
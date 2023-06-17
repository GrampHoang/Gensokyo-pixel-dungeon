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

public class YoumuEnc extends EncounterNotes {
    {
		image = ItemSpriteSheet.GHOST_FRIEND;
	}

	public void setSeen(){
		Catalog.setSeen(YoumuEnc.class);
	}

	public String npc(){
		return "Youmu";
	}

	// floor 10-14, kill lots of ghost, maybe ask for 10 kills, but after 30 kills Yuyuko spawn. (ghost spawn faster after 15 kills)
	// Give her sword, or her ghost friend
	// EncNote: Stay until Yuyuko spawn and beat her
	// Unlock: Note on how her sword upgrade works and the fact that they instantkill Wraith
}
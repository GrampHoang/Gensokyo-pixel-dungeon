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

public class ReimuEnc extends EncounterNotes {
    {
		image = ItemSpriteSheet.YINYANG;
	}

	public void setSeen(){
		Catalog.setSeen(ReimuEnc.class);
	}

	public String npc(){
		return "Reimu";
	}

    // 21-24 , Standing around
	// quest about killing enemy, either 8 eyes or 8 S U C C
    // give her yinyang (for what? May need buff yinyang), an SoU and a pie.
	// EncNote: Kill 16 instead
	// Unlock: ?

}
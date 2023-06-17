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

public class SatoriEnc extends EncounterNotes {
    {
		image = ItemSpriteSheet.ARTIFACt_SOMEONEPHONE;
	}

	public void setSeen(){
		Catalog.setSeen(SatoriEnc.class);
	}

	public String npc(){
		return "Satori";
	}

    // Boss floors, Satori just stand in some obscure conners. spawned after boss is deadth
    // Fairies boss: either upright or bottom left, whichever the Hero doesn't have vision
    // Remilia boss: either bottom left or bottom right room,  whichever the Hero doesn't have vision
    // Marisa: any corner?  whichever the Hero doesn't have vision
    // Yog: no
	
	// when interact: give KoishiEnc if met Koishi, or talk about a random encouter that you haven't encouter
	// EncNote: Meet her once
	// Unlock: Nothing
}
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

public class MarisaEnc extends EncounterNotes {
    {
		image = ItemSpriteSheet.HAKKERO;
	}

	public void setSeen(){
		Catalog.setSeen(MarisaEnc.class);
	}

	public String npc(){
		return "Marisa";
	}

	// Spawn on 21-24, in one of her secret bookroom
	// Give quest to kill rippers, lots of rippers
	// When done: give you 1 Lullaby, 1 Retribution, 2 PotHealing
	// If give 2x more than she need: 1 Lullaby, 1 Retribution, 1 PsiBlast, 4 PotHealing and 1 SoU
	// EncNote: Give her more than 2x what she ask you to
	// Unlock: Buff for wands if their true level >= +12
}
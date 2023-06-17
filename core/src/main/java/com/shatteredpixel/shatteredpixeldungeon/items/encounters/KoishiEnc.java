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

public class KoishiEnc extends EncounterNotes {
    {
		image = ItemSpriteSheet.ARTIFACT_KOISHIHAT;
	}

	public void setSeen(){
		Catalog.setSeen(KoishiEnc.class);
	}

	public String npc(){
		return "Koishi";
	}

    // 6-9 Koishi is 100% transparent, wander around.
	// The only way to really see her is apply debuff randomly or mind vision, or being lucky
	// Floor she spawn on won't have doors
	// If caught: Give you 1 PoH,  1 PoMindVision, 1 PsionicBlast. Or and SoU And a smile
	// EncNote: you have to meet Satori after having meet Koishi, her note will show you what floor she is on in each run
	// Unlock: Maybe Koishi's Phone?
}
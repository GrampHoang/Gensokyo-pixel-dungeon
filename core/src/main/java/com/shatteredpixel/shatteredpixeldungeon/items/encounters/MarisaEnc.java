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
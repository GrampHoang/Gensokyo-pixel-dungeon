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
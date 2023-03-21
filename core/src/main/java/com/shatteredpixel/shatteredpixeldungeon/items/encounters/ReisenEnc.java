package com.shatteredpixel.shatteredpixeldungeon.items.encounters;

import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ReisenEnc extends EncounterNotes {
    {
		image = ItemSpriteSheet.REISENGUN;
	}

	public void setSeen(){
		Catalog.setSeen(ReisenEnc.class);
	}

	public String npc(){
		return "Reisen";
	}

    // 16-19, standing around
	// Asking you to pick up Susgold from Tewi
    // pay money (for lost golds) and 2 randoms knowns potions, also id 2 more potions for you
	// EncNote: 
	// Unlock: ?

}
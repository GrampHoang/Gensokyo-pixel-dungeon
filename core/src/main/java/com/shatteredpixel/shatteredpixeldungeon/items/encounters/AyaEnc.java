package com.shatteredpixel.shatteredpixeldungeon.items.encounters;

import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class AyaEnc extends EncounterNotes {
    {
		image = ItemSpriteSheet.YINYANG;
	}

	public void setSeen(){
		Catalog.setSeen(AyaEnc.class);
	}

	public String npc(){
		return "Aya";
	}
	// 2-4, run around in very fast speed
	// Caught up with her cause her to drop a PoH
	// EncNote: You need to catch her 4 times, in each region (she won't go to hell, too deep)
	// Obviously she give you 4 PoH in total
	// Unlock: ?
}
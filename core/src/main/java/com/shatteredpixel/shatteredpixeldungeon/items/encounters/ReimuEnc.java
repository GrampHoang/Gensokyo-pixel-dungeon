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
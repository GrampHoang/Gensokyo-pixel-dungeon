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
}
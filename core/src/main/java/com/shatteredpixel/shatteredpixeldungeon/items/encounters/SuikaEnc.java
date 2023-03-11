package com.shatteredpixel.shatteredpixeldungeon.items.encounters;

import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class SuikaEnc extends EncounterNotes {
    {
		image = ItemSpriteSheet.VIAL;
	}

	public void setSeen(){
		Catalog.setSeen(SuikaEnc.class);
	}

	public String npc(){
		return "Suika";
	}
}
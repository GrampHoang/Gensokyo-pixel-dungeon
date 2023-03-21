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

	// Floor 4, lying on grass in a hidden room, drunk
	// ask for 2 PoH to mix with her Sa-ke
	// Give you 3 endless alcohol
	// EncNote: Meet her when you are doing Ascension run
	// Unlock: Endless Alcohol recipe
}
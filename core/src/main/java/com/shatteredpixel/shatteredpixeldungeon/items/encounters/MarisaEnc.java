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
}
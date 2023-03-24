package com.shatteredpixel.shatteredpixeldungeon.items.encounters;

import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class SakuyaEnc extends EncounterNotes {
    {
		image = ItemSpriteSheet.KNIFE;
	}

	public void setSeen(){
		Catalog.setSeen(SakuyaEnc.class);
	}

	public String npc(){
		return "Sakuya";
	}

	// floor 6-9, pick up trashes
	// Give her knife, thrwoing knife or fully upgrade the hourglass if you are using it
}
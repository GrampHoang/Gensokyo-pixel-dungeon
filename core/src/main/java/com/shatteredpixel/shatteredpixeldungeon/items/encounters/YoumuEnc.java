package com.shatteredpixel.shatteredpixeldungeon.items.encounters;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class YoumuEnc extends EncounterNotes {
    {
		image = ItemSpriteSheet.GHOST_FRIEND;
	}

	public void setSeen(){
		Catalog.setSeen(YoumuEnc.class);
	}

	public String npc(){
		return "Youmu";
	}
}
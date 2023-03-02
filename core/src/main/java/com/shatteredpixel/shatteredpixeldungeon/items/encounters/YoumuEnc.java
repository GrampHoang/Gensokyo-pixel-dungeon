package com.shatteredpixel.shatteredpixeldungeon.items.encounters;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class YoumuEnc extends Item {
    {
		image = ItemSpriteSheet.HISOUBLADE_FIRE;
	}

	public void setSeen(){
		Catalog.setSeen(YoumuEnc.class);
	}
}
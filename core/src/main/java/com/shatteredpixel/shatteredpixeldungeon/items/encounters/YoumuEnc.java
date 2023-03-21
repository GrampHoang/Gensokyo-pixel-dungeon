package com.shatteredpixel.shatteredpixeldungeon.items.encounters;

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

	// floor 10-14, kill lots of ghost, maybe ask for 10 kills, but after 30 kills Yuyuko spawn. (ghost spawn faster after 15 kills)
	// Give her sword, or her ghost friend
	// EncNote: Stay until Yuyuko spawn and beat her
	// Unlock: Maybe a better version of her sword
}
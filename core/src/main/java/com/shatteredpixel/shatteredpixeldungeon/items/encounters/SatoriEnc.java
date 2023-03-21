package com.shatteredpixel.shatteredpixeldungeon.items.encounters;

import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class SatoriEnc extends EncounterNotes {
    {
		image = ItemSpriteSheet.ARTIFACt_SOMEONEPHONE;
	}

	public void setSeen(){
		Catalog.setSeen(SatoriEnc.class);
	}

	public String npc(){
		return "Satori";
	}

    // Boss floors, Satori just stand in some obscure conners. spawned after boss is deadth
    // Fairies boss: either upright or bottom left, whichever the Hero doesn't have vision
    // Remilia boss: either bottom left or bottom right room,  whichever the Hero doesn't have vision
    // Marisa: any corner?  whichever the Hero doesn't have vision
    // Yog: no
	
	// when interact: give KoishiEnc if met Koishi, or talk about a random encouter that you haven't encouter
	// EncNote: Meet her once
	// Unlock: Nothing
}
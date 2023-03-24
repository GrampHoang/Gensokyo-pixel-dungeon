package com.shatteredpixel.shatteredpixeldungeon.items.encounters;

import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class KoishiEnc extends EncounterNotes {
    {
		image = ItemSpriteSheet.ARTIFACT_KOISHIHAT;
	}

	public void setSeen(){
		Catalog.setSeen(KoishiEnc.class);
	}

	public String npc(){
		return "Koishi";
	}

    // 6-9 Koishi is 100% transparent, wander around.
	// The only way to really see her is apply debuff randomly or mind vision, or being lucky
	// Floor she spawn on won't have doors
	// If caught: Give you 1 PoH,  1 PoMindVision, 1 PsionicBlast. Or and SoU And a smile
	// EncNote: you have to meet Satori after having meet Koishi, her note will show you what floor she is on in each run
	// Unlock: Maybe Koishi's Phone?
}
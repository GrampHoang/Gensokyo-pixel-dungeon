package com.shatteredpixel.shatteredpixeldungeon.items.encounters;

import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class YuyukoEnc extends EncounterNotes {
    {
		image = ItemSpriteSheet.PUMPKIN_PIE;
	}

	public void setSeen(){
		Catalog.setSeen(AyaEnc.class);
	}

	public String npc(){
		return "Yuyuko";
	}
	// 11-14, Chilling around in a hidden room
	// Feed her foods, maybe lots of food
	// 1 any kind of meat ->
	// 1 Normal ration ->
	// 1 Pie ->
	// 3 Pies ->
	// Yes, 3, in the 3rd region lmao
	// EncNote: Fullfil her completely
	// Unlock: ?
}
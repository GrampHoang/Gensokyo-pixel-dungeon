package com.shatteredpixel.shatteredpixeldungeon.items.encounters;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

public abstract class EncounterNotes extends Item {
    {
		image = ItemSpriteSheet.MASTERY;
	}
	
	// @Override
	// public final boolean doPickUp(Hero hero, int pos) {
	// 	GameScene.pickUpJournal(this, pos);
	// 	Sample.INSTANCE.play( Assets.Sounds.ITEM );
	// 	hero.spendAndNext( TIME_TO_PICK_UP );
	// 	return true;
	// }

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}
}
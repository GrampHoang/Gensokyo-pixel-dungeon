package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BossMercy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.TenshiNPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.items.encounters.*;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;


public class DebugSword extends WeaponWithSP {

	{
		image = ItemSpriteSheet.CLOWN_TORCH;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1f;

		tier = 1;
		DLY = 0.4f;
        ACC = 999f;
        chargeGain = 4;
        chargeNeed = 0;
    }

	@Override
	public int min(int lvl) {
		return  Math.round(68f*(tier+1)) +
				lvl*Math.round(69f*(tier+1));
	}

	@Override
	public int max(int lvl) {
		return  Math.round(69f*(tier+1)) +
				lvl*Math.round(69f*(tier+1));
	}

    @Override
	protected boolean useSkill(){
        if (!Catalog.isSeen(SuikaEnc.class)) {
            Catalog.setSeen(MarisaEnc.class);
			Catalog.setSeen(SuikaEnc.class);
			Catalog.setSeen(TenshiEnc.class);
			Catalog.setSeen(YoumuEnc.class);
			Catalog.setSeen(AyaEnc.class);
        } else {
			GLog.w("Unseen");
			Catalog.setUnSeen(SuikaEnc.class);
			Catalog.setUnSeen(MarisaEnc.class);
			Catalog.setUnSeen(TenshiEnc.class);
			Catalog.setUnSeen(YoumuEnc.class);
			Catalog.setUnSeen(AyaEnc.class);
		}
		Dungeon.hero.spendAndNext(1f);
        return true;
	}
	
	@Override
	public String skillInfo(){
		return "This is a debug weapon";
	}
}

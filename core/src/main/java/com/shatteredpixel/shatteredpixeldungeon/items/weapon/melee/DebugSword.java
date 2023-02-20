package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.items.encounters.MarisaEnc;
import com.shatteredpixel.shatteredpixeldungeon.items.encounters.SuikaEnc;
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

	public static float AMOK_DUR = 4f;

	@Override
	public int max(int lvl) {
		return  Math.round(69f*(tier+1)) +
				lvl*Math.round(69f*(tier+1));
	}

    @Override
	protected boolean useSkill(){
		SuikaEnc encounter = new SuikaEnc();

        if (!Catalog.isSeen(SuikaEnc.class)) {
			GLog.w("Seen");
            Catalog.setSeen(SuikaEnc.class);
        } else {
			GLog.w("Unseen");
			Catalog.setUnSeen(SuikaEnc.class);
		}
		Dungeon.hero.spendAndNext(1f);
        return true;
	}
	
	@Override
	public String skillInfo(){
		return Messages.get(ClownTorch.class, "skill_desc", chargeGain, chargeNeed, (int)AMOK_DUR);
	}
}

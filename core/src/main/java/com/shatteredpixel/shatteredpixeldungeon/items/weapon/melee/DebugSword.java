/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2022 Evan Debenham
 *
 * Gensokyo Pixel Dungeon
 * Copyright (C) 2022-2023 GrampHoang
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

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
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeilingHand.PullWave;
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
		return  Math.round(20f*(tier+1)) +
				lvl*Math.round(21f*(tier+1));
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
		PullWave.blast(curUser.pos);
		Dungeon.hero.spendAndNext(1f);
        return true;
	}
	
	@Override
	public String skillInfo(){
		return "This is a debug weapon";
	}
}

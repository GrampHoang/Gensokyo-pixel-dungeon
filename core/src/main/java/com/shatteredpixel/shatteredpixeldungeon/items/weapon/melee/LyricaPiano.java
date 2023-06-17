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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char.Alignment;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MusicFlow;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;


public class LyricaPiano extends WeaponWithSP {

	{
		image = ItemSpriteSheet.LYRICA_PIANO;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1f;

		tier = 4;
		DLY = 1f;

        chargeGain = 10;
    }

	@Override
	public int max(int lvl) {
		return  Math.round(4f*(tier+1)) + //20 base
				lvl*Math.round(1f*(tier)); // 4 instead of 5 per level
	}

    @Override
	public int proc(Char attacker, Char defender, int damage) {
        if (attacker.buff(MusicFlow.class) == null){
            Buff.prolong( attacker, MusicFlow.class, MusicFlow.DURATION).increaseFlow(1);
        }
        else{
            MusicFlow flow = attacker.buff(MusicFlow.class);
            flow.increaseFlow(1);
            Buff.prolong( attacker, MusicFlow.class, MusicFlow.DURATION );
        }
        attacker.sprite.centerEmitter().start( Speck.factory( Speck.NOTE ), 0.3f, 4 );
        return super.proc(attacker, defender, damage);
	}

    @Override
	protected boolean useSkill(){
        Dungeon.hero.busy();
		if (Dungeon.hero.buff(MusicFlow.class) == null){
            Buff.prolong( Dungeon.hero, MusicFlow.class, MusicFlow.DURATION).increaseFlow(5);
        }
        else{
            MusicFlow flow = Dungeon.hero.buff(MusicFlow.class);
            flow.increaseFlow(5);
            Buff.prolong( Dungeon.hero, MusicFlow.class, MusicFlow.DURATION );
        }
        Dungeon.hero.sprite.centerEmitter().start( Speck.factory( Speck.NOTE ), 0.3f, 4 );
        Dungeon.hero.spendAndNext(1f);
        return true;
    }

    @Override
    public String skillInfo(){
		return Messages.get(this, "skill_desc", chargeGain, chargeNeed, max()/2);
	}

    public class Note extends Item {
		{
			image = ItemSpriteSheet.NOTE_TEAL;
		}
	}
}

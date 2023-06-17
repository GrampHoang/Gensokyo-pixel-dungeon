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
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.watabou.utils.PathFinder;

public class MarisaBroom extends WeaponWithSP {

	{
		image = ItemSpriteSheet.MARISA_BROOM;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1f;

		tier = 3;
		DLY = 1f;

        chargeGain = 25;
    }

	public static float BLIND_DUR = 3f;
	
	@Override
	public int max(int lvl) {
		return  Math.round(4f*(tier+1)) + //16 base
				lvl*Math.round(1f*(tier+1)); // no change scale
	}

    @Override
	protected boolean useSkill(){
		Buff.prolong( Dungeon.hero, Blindness.class, BLIND_DUR);
		for (int i : PathFinder.NEIGHBOURS8){
            Char ch = Actor.findChar(i + Dungeon.hero.pos);
            if (ch != null){
                Buff.prolong( ch, Blindness.class, BLIND_DUR);
            }
            CellEmitter.get(i + Dungeon.hero.pos).burst(SmokeParticle.FACTORY, 4);
        }
        CellEmitter.center(Dungeon.hero.pos).burst(BlastParticle.FACTORY, 5);
		Dungeon.hero.spendAndNext(1f);
        return true;
	}

	@Override
	public String skillInfo(){
		return Messages.get(MarisaBroom.class, "skill_desc", chargeGain, chargeNeed, BLIND_DUR);
	}
}

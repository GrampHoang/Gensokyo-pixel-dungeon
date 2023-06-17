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
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Callback;

public class LusanaViolin extends WeaponWithSP {

	{
		image = ItemSpriteSheet.LUSANA_VIOLIN;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1f;

		tier = 4;
		DLY = 1f;

        chargeGain = 10;
    }

	@Override
	public int max(int lvl) {
		return  Math.round(3f*(tier+1)) + 2 +//17 base
				lvl*Math.round(0.6f*(tier+1)); // 3 instead of 5 per level
	}

    @Override
	public int proc(Char attacker, Char defender, int damage) {
        int damageDo = super.proc(attacker, defender, damage);
        final int damageDoFinal = damageDo;
        int count = 0;
        for (int i : PathFinder.NEIGHBOURS8){
            Char ch = Actor.findChar(attacker.pos + i);
            //Exist and not same alignment, not the current target
            if (ch != null && ch.alignment != attacker.alignment && ch != defender){
                count++;
                ch.damage(damageDo, attacker);
            }
        }
        if (count == 0) {
            damageDo = (int)Math.round(damageDo * 1.5f);
            defender.sprite.centerEmitter().start( Speck.factory( Speck.NOTE ), 0.2f, 6 );
        } else {
            defender.sprite.centerEmitter().start( Speck.factory( Speck.NOTE ), 0.2f, 3 );
        }
        return damageDo;
	}

    @Override
	protected boolean useSkill(){
        Dungeon.hero.busy();
		for (int i : PathFinder.NEIGHBOURS8){
            Char ch = Actor.findChar(Dungeon.hero.pos + i);
            //Exist, not attacker and not same alignment
            if (ch != null && ch.alignment != Alignment.ALLY){
                ch.damage(max(), Dungeon.hero); 
                ch.sprite.centerEmitter().start( Speck.factory( Speck.NOTE ), 0.3f, 3 );
            }
            CellEmitter.get(Dungeon.hero.pos + i).start( Speck.factory( Speck.NOTE ), 0.2f, 1 );
        }
        Dungeon.hero.spendAndNext(1f);
        return true;
    }

    @Override
    public String skillInfo(){
		return Messages.get(this, "skill_desc", chargeGain, chargeNeed, max());
	}

    public class Note extends Item {
		{
			image = ItemSpriteSheet.NOTE_TEAL;
		}
	}
}

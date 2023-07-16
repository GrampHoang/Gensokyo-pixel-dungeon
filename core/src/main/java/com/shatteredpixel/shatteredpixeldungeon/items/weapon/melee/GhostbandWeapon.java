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
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Callback;
import com.watabou.utils.Bundle;

public abstract class GhostbandWeapon extends WeaponWithSP {

	{
		image = ItemSpriteSheet.LUSANA_VIOLIN;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1f;

		tier = 4;
		DLY = 1f;

        chargeGain = 10;
        chargeNeed = 100;
    }

    protected int hitCounter = 0;

	@Override
	public int max(int lvl) {
		return  Math.round(3f*(tier+1)) + 3 +  // 18 base instead of 25
				lvl*Math.round(0.8f*(tier+1)); // 4 instead of 5 per level
	}

    @Override
	public int proc(Char attacker, Char defender, int damage) {
        if (hitCounter >= 5){
            activateAttackSkill(attacker, defender);
            hitCounter = 0;
        } else {
            hitCounter++;
        }
        return super.proc(attacker, defender, damage);
	}

    protected abstract void activateAttackSkill(Char attacker, Char defender);

    @Override
	protected boolean useSkill(){
		refundSP();
		GameScene.selectCell(targeter);
        return true;
	}

	private CellSelector.Listener targeter = new CellSelector.Listener() {

		@Override
		public void onSelect(Integer cell) {
			if (cell == null){
				return;
			}

			Char ch = Actor.findChar(cell);

            if (ch == null){
                GLog.w(Messages.get(GhostbandWeapon.class, "no_target"));
                return;
            } else if (Dungeon.level.distance(ch.pos, Dungeon.hero.pos) > 1) {
                GLog.w(Messages.get(GhostbandWeapon.class, "too_far"));
            } else{
                activateAttackSkill(curUser, ch);
				spendSP();
                Dungeon.hero.spendAndNext(1f);
            }
			updateQuickslot();
		}

		@Override
		public String prompt() {
			return Messages.get(GhostbandWeapon.class, "prompt");
		}

	};

    protected int skillDamage(){
        return Random.IntRange(min(), max());
    }

    private static final String HITCOUNT = "hitcount";

    @Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);
		bundle.put( HITCOUNT , hitCounter );
	}

    @Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);
		hitCounter = bundle.getInt( HITCOUNT );
	}
}

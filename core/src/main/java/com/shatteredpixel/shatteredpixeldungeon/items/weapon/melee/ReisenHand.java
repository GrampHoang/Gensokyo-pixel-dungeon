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

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.Random;

public class ReisenHand extends WeaponWithSP {

	{
		image = ItemSpriteSheet.REISENHAND;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1.3f;

		tier = 1;
		DLY = 0.4f; //2.5x speed
		
		bones = false;

		chargeGain = 12;
		chargeNeed = 100;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return enchantment != null && (cursedKnown || !enchantment.curse()) ?
				new ItemSprite.Glowing(enchantment.glowing().color, 0.33f*enchantment.glowing().period) : PURPLE;
	}

	private static ItemSprite.Glowing PURPLE = new ItemSprite.Glowing( 0xa020f0, 0.33f );


	@Override
	public int max(int lvl) {
		return (3 + lvl);  //just simplify the normal scaling equation, nerfed because of extra effect.
	}

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
                pushNdmg(ch);
				spendSP();
                Dungeon.hero.spendAndNext(1f);
            }
			updateQuickslot();
		}

		@Override
		public String prompt() {
			return Messages.get(this, "prompt");
		}

	};

	private void pushNdmg(Char ch){
		Ballistica trajectory = new Ballistica(Dungeon.hero.pos, ch.pos, Ballistica.STOP_TARGET);
		//trim it to just be the part that goes past them
		trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size() - 1), Ballistica.PROJECTILE);
		//knock them back along that ballistica
		WandOfBlastWave.throwChar(ch, trajectory, 1, false, true, this.getClass());
		Buff.affect(ch, Paralysis.class, 0.4f);
		ch.damage(Random.Int(min()*2, max()), Dungeon.hero);
	}

    public String statsInfo(){
		return Messages.get(this, "stats_desc");
	}

	public String skillInfo(){
		return Messages.get(this, "skill_desc", chargeGain, chargeNeed, 1 + Math.round(level()/5), min()*2, max());
	}
}

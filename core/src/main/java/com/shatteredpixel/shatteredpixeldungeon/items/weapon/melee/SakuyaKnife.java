/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2022 Evan Debenham
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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.RainbowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class SakuyaKnife extends WeaponWithSP {

	{
		image = ItemSpriteSheet.KNIFE;
		hitSound = Assets.Sounds.HIT_STAB;
		hitSoundPitch = 1.3f;

		tier = 3;
		DLY = 0.5f; //2x speed

		chargeGain = 10;
        chargeNeed = 30;
	}

	@Override
	public int max(int lvl) {
		return  Math.round(2f*(tier+1)) +     //8 base, down from 20
				lvl*Math.round(0.5f*(tier+1));  //+2 per level, down from +4
	}

    @Override
	protected boolean useSkill(){
		GameScene.selectCell(targeter);
        return true;
	}

	private CellSelector.Listener targeter = new CellSelector.Listener() {

		@Override
		public void onSelect(Integer cell) {
			if (cell == null){
				return;
			}

            Ballistica attack = new Ballistica(Dungeon.hero.pos, cell, Ballistica.STOP_TARGET);
			Mob target = null;

			if (cell != null && attack.collisionPos == cell){
				Char ch = Actor.findChar(cell);
				if (ch != null && ch.alignment != Char.Alignment.ALLY && ch instanceof Mob){
					target = (Mob)ch;
				}
			}

            final Mob targetDamage = target;

			if (target == null){
				GLog.w(Messages.get(this, "cancel"));
				return;

			} else {
				//throw knife
                ((MissileSprite)Dungeon.hero.sprite.parent.recycle( MissileSprite.class )).
                reset(Dungeon.hero.pos, cell, new ThrowKnife(), new Callback() {
                    @Override
                    public void call() {
                        // ch.onAttackComplete();
                        targetDamage.damage(min()*2, max());
                    }
                } );

			}
		}

		@Override
		public String prompt() {
			return Messages.get(this, "prompt");
		}

	};

    public class ThrowKnife extends Item {
		{
			image = ItemSpriteSheet.KNIFE;
		}
	}
}

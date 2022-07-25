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

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroAction;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLivingEarth;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class KoishiHat extends Artifact {

	{
		image = ItemSpriteSheet.ARTIFACT_CAPE;
		levelCap = 10;
        exp = 0;
        unique = true;
        bones = false;
	}
    public boolean invis = false;

    @Override
	public int value() {
		return 0;
	}

    @Override
	protected ArtifactBuff passiveBuff() {
		return new Koishibuff();
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
	}
	
	@Override
	public String desc() {
		String desc = super.desc();
		return desc;
	}

    public class Koishibuff extends ArtifactBuff{
        public float turn_to_invis = 15 - Math.round((0.5f * level())); //total turn needed to invis
        public float turn_till_invis = turn_to_invis;					 //how many turn left till invis

        @Override
        public boolean act() {
            if (turn_till_invis > 0){
                turn_till_invis--;
            }

			if(Dungeon.hero.curAction instanceof HeroAction.Attack){
				turn_till_invis = turn_to_invis;
			}

			if(invis == true && Dungeon.hero.buff(Invisibility.class) == null){
                turn_till_invis = turn_to_invis;
                invis = false;
            }

            if (turn_till_invis == 0){
                Buff.prolong(Dungeon.hero, Invisibility.class, 514f);
                invis = true;
            }

            spend(TICK);
            return true;
        }

        @Override
		public String toString() {
			return  "Closed Mind";
		}

		@Override
		public String desc() {
			return String.format("Koishi closed her mind, which makes her presence cannot be noticed by other beings unless she allows it. After %1$.1f turns of not attacking, she will turn invisible. \n\nTurns visible remaining: %2$.1f.", turn_to_invis, turn_till_invis);
		}

		@Override
		public int icon() {
			return BuffIndicator.KOISHI;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.brightness(0.6f);
		}

		@Override
		public float iconFadePercent() {
			return (15f - turn_till_invis) / 15f;
		}

		@Override
		public String iconTextDisplay() {
			return Integer.toString(Math.round(turn_till_invis));
		}
	
    }
}

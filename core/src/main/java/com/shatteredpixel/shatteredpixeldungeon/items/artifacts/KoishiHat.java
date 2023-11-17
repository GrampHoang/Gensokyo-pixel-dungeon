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

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Doom;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ExpNullify;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Reality;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Fury;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroAction;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class KoishiHat extends Artifact {

	{
		image = ItemSpriteSheet.ARTIFACT_KOISHIHAT;
		levelCap = 10;
        exp = 0;
        unique = true;
        bones = false;
		keptThoughLostInvent = true;
	}

	public float imaginary_friend = 0;
	public float total = 15f - imaginary_friend - level()/3;
	public float cooldown = 15f;
    public boolean invis = false;

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions( hero );
		actions.remove( AC_THROW );
		actions.remove( AC_DROP );
		return actions;
	}

	public void updateTalent(){
		total = 15f - imaginary_friend;
		if (Dungeon.hero.buff(KoishiHat.Koishibuff.class) != null){
			Dungeon.hero.buff(KoishiHat.Koishibuff.class).updateTalent();
		}
	}

	@Override
	public int level() {
		int level = Dungeon.hero == null ? 0 : Dungeon.hero.lvl/3;
		return level;
	}

    @Override
	public int value() {
		return 0;
	}

    @Override
	protected ArtifactBuff passiveBuff() {
		return new Koishibuff();
	}
	
	@Override
	public String desc() {
		String desc = super.desc();
		return desc;
	}

	@Override
	public boolean doUnequip(Hero hero, boolean collect, boolean single) {
		if(Statistics.deepestFloor == 1){
			Statistics.qualifiedForKoishiKillerBadge = true;
		}
		if (Dungeon.hero.buff(Invisibility.class) != null){
			Dungeon.hero.buff(Invisibility.class).detach();
		}
		Buff.affect(Dungeon.hero, Fury.class);
		Buff.append(Dungeon.hero, Doom.class);
		return super.doUnequip(hero, collect, single);
	}

	@Override
	public boolean doEquip(Hero hero) {
		Statistics.qualifiedForKoishiKillerBadge = false;
		if(Dungeon.hero.buff(Fury.class) != null){
			GLog.w("Koishi hasn't calm down yet!");
			return false;
		} else {
			if (Dungeon.hero.buff(Doom.class) != null){
				Dungeon.hero.buff(Doom.class).detach();
			};
			boolean ret = super.doEquip(hero);
			Dungeon.hero.buff(KoishiHat.Koishibuff.class).updateTalent();
			return ret;
		}
	}

	private static final String TURN_TILL_INVIS =        "turnTillInvis";
	private static final String TURN_TO_INVIS =        "turnToInvis";
	@Override
		public void storeInBundle( Bundle bundle ) {
			super.storeInBundle( bundle );
			if (passiveBuff != null){
				cooldown = passiveBuff.koishiDummyCoolDown();
				total = passiveBuff.koishiDummyTotal();
			} else {
				cooldown = 15;
				total = 15f - imaginary_friend - level()/3;
			}
			bundle.put( TURN_TILL_INVIS, cooldown );
			bundle.put( TURN_TO_INVIS, total );
		}
		
		@Override
		public void restoreFromBundle( Bundle bundle ) {
			super.restoreFromBundle( bundle );
			cooldown = bundle.getFloat( TURN_TILL_INVIS );
			total = bundle.getFloat( TURN_TO_INVIS );
		}

    public class Koishibuff extends ArtifactBuff{
        public float turnToInvis = total; 						//total turn needed to invis
        public float turnTillInvis = cooldown;					 //how many turn left till invis
		public int earnExpCount = 0;		// For exp reduction
		
		public void koishiExpAct() {
			earnExpCount++;
			if (earnExpCount >= (2 + Dungeon.hero.pointsInTalent(Talent.LEARNING)) && Dungeon.depth != 1){
				earnExpCount = 0;
				Buff.affect(Dungeon.hero, ExpNullify.class);
				//Give exp nullify buff here
			}
		}

		public void updateTalent() {
			turnToInvis = 15f - level()/3 - imaginary_friend;
		}

		@Override
		public float koishiDummyCoolDown() {
			return turnTillInvis;
		}

		@Override
		public float koishiDummyTotal() {
			return turnToInvis;
		}

        @Override
        public boolean act() {
            if (turnTillInvis > 0){
                turnTillInvis--;
            }

			if(Dungeon.hero.curAction instanceof HeroAction.Attack){
				turnTillInvis = turnToInvis;
			}

			if(invis == true && Dungeon.hero.buff(Invisibility.class) == null){
                turnTillInvis = turnToInvis;
                invis = false;
            }

            if (turnTillInvis == 0){
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
			int count = 2 + Dungeon.hero.pointsInTalent(Talent.LEARNING);
			return Messages.get(this, "effect", turnToInvis, turnTillInvis, count);
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
			return (15f - turnTillInvis) / 15f;
		}

		@Override
		public String iconTextDisplay() {
			return Integer.toString(Math.round(turnTillInvis));
		}

    }
}

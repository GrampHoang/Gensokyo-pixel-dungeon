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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CheckedCell;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class SomeonePhone extends Artifact {

	{
		image = ItemSpriteSheet.ARTIFACT_HOLDER;

		exp = 0;
		levelCap = 10;
		charge = 0;
		partialCharge = 0;
		chargeCap = 100;

		defaultAction = AC_CALL;
	}

	public static final String AC_CALL = "CALL";

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (isEquipped( hero ) && !cursed) actions.add(AC_CALL);
		return actions;
	}

	@Override
	public void execute( Hero hero, String action ) {
		super.execute(hero, action);

		if (action.equals(AC_CALL)){
			if (!isEquipped(hero))  GLog.i( Messages.get(Artifact.class, "need_to_equip") );
			else if (charge < 25)  GLog.i( Messages.get(this, "low_charge") );
			else                    call();
		}
	}
	
    public void call(){
        // Buff.affect(Dungeon.hero, MindVision.class, 1 + level()/2);
        int count = 0;
        // GLog.w("Calling");
        Mob[] mobArray = Dungeon.level.mobs.toArray( new Mob[0] );
        for (Mob mob : mobArray) {
			if (mob.alignment != Char.Alignment.ALLY && count < level()) {
                count++;
                Buff.append(curUser, CharAwareness.class, 5 + level()).charID = mob.id();
                // GLog.w("Called");
			}
		}
		charge -= 25;
        Talent.onArtifactUsed(Dungeon.hero);
        updateQuickslot();
        Dungeon.observe();
        Dungeon.hero.checkVisibleMobs();
        GameScene.updateFog();
        curUser.spendAndNext(Actor.TICK);
        return;
    }

	@Override
	public void charge(Hero target, float amount) {
		if (charge < chargeCap){
			charge += amount; //charge 1,2,3 at +0-4, +5-9 and +10
			if (charge >= chargeCap) {
				charge = chargeCap;
				partialCharge = 0;
				GLog.p( Messages.get(this, "full_charge") );
			}
		}
	}

	@Override
	public String desc() {
		String desc = super.desc();

		if ( isEquipped( Dungeon.hero ) ){
			if (!cursed) {
				desc += "\n\n" + Messages.get(this, "desc_worn");

			} else {
				desc += "\n\n" + Messages.get(this, "desc_cursed");
			}
		}

		return desc;
	}

	public static class CharAwareness extends FlavourBuff {

		public int charID;

		private static final String CHAR_ID = "char_id";

		@Override
		public void detach() {
			super.detach();
			Dungeon.observe();
			GameScene.updateFog();
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			charID = bundle.getInt(CHAR_ID);
		}

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(CHAR_ID, charID);
		}

	}

    @Override
	protected ArtifactBuff passiveBuff() {
		return new telePhone();
	}

    public class telePhone extends ArtifactBuff {
		@Override
		public boolean act() {
			if (cursed && Random.Int(20) == 0){
                for (Mob mob : Dungeon.level.mobs) {
                    if (mob.paralysed <= 0
                            && Dungeon.level.distance(Dungeon.hero.pos, mob.pos) <= 8
                            && mob.alignment == Char.Alignment.ENEMY) {
                        mob.beckon(Dungeon.hero.pos);
                    }
                }
            }
			// updateQuickslot();
			spend( TICK );
			return true;
		}

		public void charge(boolean kill, int boost){
			if (!cursed) {
				if (charge < chargeCap){
					if (kill){ 
						charge += Math.min(1, boost);
					} else{
						charge += (1 + level()/5); //charge 1,2,3 at +0-4, +5-9 and +10
					}
					if (charge >= chargeCap) {
						charge = chargeCap;
						partialCharge = 0;
						GLog.p( Messages.get(this, "full_charge") );
					}
					updateQuickslot();
				}
			}
		}

		public void gainExp( float levelPortion ) {
			if (cursed || levelPortion == 0) return;

			exp += levelPortion; //30 for surprise atack

			if (exp > Math.round(150*Math.pow(1.2f, level())) && level() < levelCap){
				exp -= Math.round(150*Math.pow(1.2f, level()));
				GLog.p( Messages.get(this, "levelup") );
				upgrade();
			}

		}
	}

}

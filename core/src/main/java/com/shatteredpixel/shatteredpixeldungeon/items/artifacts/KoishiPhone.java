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
import com.shatteredpixel.shatteredpixeldungeon.effects.Surprise;
import com.shatteredpixel.shatteredpixeldungeon.effects.Wound;
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
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class KoishiPhone extends SomeonePhone {

	{
		image = ItemSpriteSheet.ARTIFACT_HOLDER;

		exp = 0;
		levelCap = 10;
		charge = 0;
		partialCharge = 0;
		chargeCap = 100;

		defaultAction = AC_CALL;
        unique = true;
	}

	public static final String AC_CALL_STAB = "CALL AND STAB";

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (isEquipped( hero ) && !cursed) actions.add(AC_CALL_STAB);
		return actions;
	}

	@Override
	public void execute( Hero hero, String action ) {
		super.execute(hero, action);

		if (action.equals(AC_CALL_STAB)){
			if (!isEquipped(hero))  GLog.i( Messages.get(Artifact.class, "need_to_equip") );
			else if (charge < 100)  GLog.i( Messages.get(this, "low_charge") );
			else                    call_stab();
		}
	}
	
    public void call_stab(){
        Mob[] mobArray = Dungeon.level.mobs.toArray( new Mob[0] );
        for (Mob mob : mobArray) {
			if (mob.alignment != Char.Alignment.ALLY) {
                Buff.append(curUser, TalismanOfForesight.CharAwareness.class, 5 + level()).charID = mob.id();
                mob.damage(mob.HP-1, this);
                Wound.KoishiWound.hit(mob);
                Surprise.hit(mob);
			}
		}
		Camera.main.shake( 2f, 0.5f );
		GameScene.flash(0xAAAAAA);
        Sample.INSTANCE.play(Assets.Sounds.DEGRADE, 0.75f, 0.88f);
        Talent.onArtifactUsed(Dungeon.hero);
        updateQuickslot();
        Dungeon.observe();
        Dungeon.hero.checkVisibleMobs();
        GameScene.updateFog();
        curUser.spendAndNext(Actor.TICK);
        return;
    }

	// @Override
	// public void charge(Hero target, float amount) {
	// 	if (charge < chargeCap){
	// 		charge += Math.round(2*amount);
	// 		if (charge >= chargeCap) {
	// 			charge = chargeCap;
	// 			partialCharge = 0;
	// 			GLog.p( Messages.get(this, "full_charge") );
	// 		}
	// 	}
	// }

	// @Override
	// public String desc() {
	// 	String desc = super.desc();

	// 	if ( isEquipped( Dungeon.hero ) ){
	// 		if (!cursed) {
	// 			desc += "\n\n" + Messages.get(this, "desc_worn");

	// 		} else {
	// 			desc += "\n\n" + Messages.get(this, "desc_cursed");
	// 		}
	// 	}

	// 	return desc;
	// }
}

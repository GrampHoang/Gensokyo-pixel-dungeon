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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Hourai extends Buff {
	
	{
		type = buffType.POSITIVE;
		announced = true;
		revivePersists = true;
	}
	
	private enum State{
		START, CHAOS, PAIN, IMMORTAL
	}


	private State state = State.START;

	private static final int DUR_S = 69;
	private static final int DUR_C = 1500;
	private static final int DUR_P = 1000;
	private int turnCount = DUR_S;

	private static final String STATE = "state";
	private static final String TURNCOUNT = "turncount";
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(STATE, state);
		bundle.put(TURNCOUNT, turnCount);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		state = bundle.getEnum(STATE, State.class);
		turnCount = bundle.getInt(TURNCOUNT);
	}

	public boolean isImmortal(){
		return state == State.IMMORTAL;
	}

	@Override
	public boolean act() {
		switch(state){
			case START:
				turnCount--;
				if(turnCount <= 0){
					state = State.CHAOS;
					turnCount = DUR_C;
				}
				break;
			case CHAOS:
				turnCount--;
				int rand = Random.IntRange(0, 60);
				if (rand < 6){
					Buff.affect(target, Cripple.class, 2f);
					GLog.w("Bad Common");
				} else if (rand < 10){
					GLog.w("Bad Rare");
				} else if (rand < 11){
					GLog.w("Bad Very rare");
				} else if (rand < 14){
					GLog.w("Not that bad");
				} else if (rand < 18){
					GLog.w("Not that bad");
				} else if (rand < 20){
					GLog.w("Not that bad");

				}
				// 1/3 chance to proc something random EVERY TURN


				if(turnCount <= 0){
					state = State.PAIN;
					turnCount = DUR_P;
				}
				break;
			case PAIN:
				turnCount--;
				int rando = Random.IntRange(0, 60);
				if (rando < 8){
					GLog.w("Bad Common");
				} else if (rando < 12){
					GLog.w("Bad Rare");
				} else if (rando < 14){
					GLog.w("Bad Very rare");
				} else if (rando < 16){
					GLog.w("Not that bad");
				} else if (rando < 18){
					GLog.w("Not that bad");
				} else if (rando < 20){
					GLog.w("Not that bad");
				}
				// 1/3 chance to proc something BAD EVERY TURN


				if(turnCount <= 0) state = State.IMMORTAL;
				break;
			case IMMORTAL:
				break;
			default:
				GLog.w("error");
				break;	
		}
		spend(TICK);
		return true;
	}

	@Override
	public void fx(boolean on) {
		if (on && isImmortal()) target.sprite.add( CharSprite.State.HEALING );
		else if (target.invisible == 0) target.sprite.remove( CharSprite.State.HEALING );
	}
	
	@Override
	public int icon() {
		return BuffIndicator.ANKH;
	}
	
	@Override
	public void tintIcon(Image icon) {
		switch (state){
			case IMMORTAL: default:
				icon.hardlight(1f, 1f, 1f);
				break;
			case START:
				icon.hardlight(0.2f, 0.2f, 0.2f);
				break;
			case CHAOS:
				icon.hardlight(0.4f, 0.6f, 0.6f);
				break;
			case PAIN:
				icon.hardlight(1f, 0, 0);
				break;
		}
	}

	@Override
	public String toString() {
		switch (state){
			case IMMORTAL:
				return Messages.get(this, "name_true");
			case START:
				return Messages.get(this, "name_start");
			case CHAOS:
				return Messages.get(this, "name_chaos");
			case PAIN:
				return Messages.get(this, "name_pain");
			default:	//This shouldn't appear
				return Messages.get(this, "name");
		}
	}
	
	@Override
	public String desc() {
		String desc = "";
		switch (state){
			case IMMORTAL:
				desc = Messages.get(this, "desc_immortal");
				break;
			case START:
				desc = Messages.get(this, "desc_start");
				break;
			case CHAOS:
				desc = Messages.get(this, "desc_chaos");
				break;
			case PAIN:
				desc = Messages.get(this, "desc_pain");
				break;
			default:	//This shouldn't appear
				desc = Messages.get(this, "desc");
				break;
		}
		desc += "\n\n" + Messages.get(this, "turn_count", turnCount);
		return desc;
	}
}

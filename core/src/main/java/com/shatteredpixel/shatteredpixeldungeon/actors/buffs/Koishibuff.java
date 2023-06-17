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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroAction;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

public class Koishibuff extends Buff {
	{
		type = buffType.POSITIVE;
	}

	public float turn_to_invis = 15;
    public float turn_till_invis = turn_to_invis;

	@Override
	public boolean act() {
		if (turn_till_invis > 0){
			turn_till_invis--;
		}

		if(Dungeon.hero.curAction instanceof HeroAction.Attack){
			turn_till_invis = turn_to_invis;
		}

		if (turn_till_invis <= 0){
			Buff.affect(Dungeon.hero, Invisibility.class, 515f);
		}

		spend(TICK);
		return true;
	}

	@Override
	public String toString() {
		return  Messages.get(this, "name");
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", turn_to_invis, turn_till_invis);
	}

	@Override
	public int icon() {
		return BuffIndicator.INVISIBLE;
	}
	
}

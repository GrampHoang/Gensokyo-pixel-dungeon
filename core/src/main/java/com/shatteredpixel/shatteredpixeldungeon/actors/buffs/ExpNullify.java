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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;

public class ExpNullify extends Buff {
	
	{
		type = buffType.NEGATIVE;
		announced = false;
	}
	
	@Override
	public void fx(boolean on) {
		if (on) target.sprite.add(CharSprite.State.MIND);
		else if (target.invisible == 0) target.sprite.remove(CharSprite.State.MIND);
	}
	
	@Override
	public int icon() {
		return BuffIndicator.VERTIGO;
	}
	
    @Override
	public void tintIcon(Image icon) {
		icon.hardlight(0.3f, 0.3f, 0.8f);
	}

	@Override
	public String toString() {
		if(target instanceof Hero)
			if(((Hero)target).heroClass == HeroClass.KOISHI)
				return Messages.get(this, "kname");
		return Messages.get(this, "name");
	}
	
	@Override
	public String desc() {
		if(target instanceof Hero)
			if(((Hero)target).heroClass == HeroClass.KOISHI)
				return Messages.get(this, "kdesc");
		return Messages.get(this, "desc");
	}
}

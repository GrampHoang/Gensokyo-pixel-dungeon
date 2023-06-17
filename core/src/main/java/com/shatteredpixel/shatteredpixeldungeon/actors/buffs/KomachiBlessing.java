/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2022 Evan Debenham
 *
 * Gensokyo Pixel Dungeon
 * Copyright (C) 2012-2023 GrampHoang
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
import com.watabou.utils.Bundle;

public class KomachiBlessing extends Buff {
	
	{
		type = buffType.POSITIVE;
	}
	protected boolean isMelee = true;

	public void setMelee(){
		isMelee = true;
	}

	public void setRange(){
		isMelee = false;
	}

	public boolean isMelee(){
		return isMelee;
	}
	@Override
	public void fx(boolean on) {
		if (on && isMelee) target.sprite.add( CharSprite.State.SHIELDEDMELEE );
		else if (on && !isMelee) target.sprite.add( CharSprite.State.SHIELDEDRANGE );
	}
	
	@Override
	public void detach() {
		target.sprite.remove( CharSprite.State.SHIELDEDMELEE );
		target.sprite.remove( CharSprite.State.SHIELDEDMELEE );
		super.detach();
	}

	@Override
	public int icon() {
		return BuffIndicator.ARMOR;
	}
	
	@Override
	public void tintIcon(Image icon) {
		if (isMelee){
			icon.hardlight(1f, 0.5f, 0.5f); //Kinda red
		} else {
			icon.hardlight(0.5f, 0.5f, 0.8f);	// Slightly blue
		}
	}

	@Override
	public String toString() {
		if (isMelee){
			return Messages.get(this, "name_melee");
		} else {
			return Messages.get(this, "name_range");
		}
	}
	
	@Override
	public String desc() {
		if (isMelee){
			return Messages.get(this, "desc_melee");
		} else {
			return Messages.get(this, "desc_range");
		}
	}
	private static String ISMELEE = "ismelee";
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( ISMELEE, isMelee);
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		isMelee = bundle.getBoolean( ISMELEE );
	}
}

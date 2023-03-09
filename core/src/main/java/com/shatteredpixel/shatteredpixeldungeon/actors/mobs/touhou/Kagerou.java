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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.*;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.KagerouSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Kagerou extends Mob {
	
	{
		spriteClass = KagerouSprite.class;
		
		HP = HT = 70;
		defenseSkill = 30;
		
		EXP = 11;
		maxLvl = 21;
		
		loot = new Food();
		lootChance = 0.083f;
	}
	
	private int stack = 0;

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 12, 25 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 30;
	}
	
	@Override
	public float attackDelay() {
		return super.attackDelay();
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 2);
	}
	
	@Override
	protected boolean act() {
		boolean result = super.act();
		if (buff(Thirst.class) == null && state == HUNTING) {
			stack = 0;
			Buff.affect( this, Thirst.class );
		}
		return result;
	}
	
	@Override
	public void move( int step, boolean travelling) {
		if (travelling && buff(Thirst.class) != null && enemySeen) {
			stack++;
			if (isLunatic()){
				stack++;
			}
		}
		super.move( step, travelling);
	}
	
	@Override
	public float speed() {
		return super.speed() * (float)Math.pow(1.05, stack);
	}


	@Override
	public int attackProc(Char enemy, int damage) {
		damage = super.attackProc(enemy, damage);
		if (buff(Thirst.class) != null && stack > 10){
			howl();
		} if (stack >= 1){
			Buff.affect(enemy, Bleeding.class).set((float) stack/2);
		}
		stack = 0;
		return damage;
	}
	
	private void howl(){
		this.sprite.centerEmitter().start( Speck.factory( Speck.SCREAM ), 0.3f, 3 );
		Sample.INSTANCE.play( Assets.Sounds.CHALLENGE );
		Buff.prolong(this, Cripple.class, 3f);
		stack += 5;
		if (isLunatic()){stack += 5;}
	}
	private static String STACK = "stack";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( STACK, stack );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		stack = bundle.getInt( STACK );
	}
	
	public static class Thirst extends Buff {
		
		{
			type = buffType.POSITIVE;
			announced = true;
		}

		@Override
		public int icon() {
			return BuffIndicator.AMOK;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(0f, 0f, 0f);
		}

		@Override
		public String toString() {
			return Messages.get(this, "name");
		}
		
		@Override
		public String desc() {
			return Messages.get(this, "desc");
		}
	}
}

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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MystiaVendor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MystiaSprite;
import com.watabou.utils.Random;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;

public class Mystia extends Mob {
	{
		spriteClass = MystiaSprite.class;
		HP = HT = 10;
		defenseSkill = 3;
		EXP = 2;
		maxLvl = 8;
		loot = Gold.class;
		lootChance = 0.5f;
		Buff.affect(this, SingCounter.class);
	}


	@Override
	public int damageRoll() {
		return Random.NormalIntRange(1, 6);
	}

	@Override
	public int attackSkill(Char target) {
		return 9;
	}
	
    @Override
	public float speed() {
		return super.speed()*0.9f;
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 2);
	}

    @Override
    public void damage(int dmg, Object src) {
		//Should be 1/15 chance
        sing(14);
        super.damage(dmg, src);
    }

	@Override
	public int attackProc(Char enemy, int damage) {
		this.buff(SingCounter.class).countUp(1);
		if(this.buff(SingCounter.class).count() > (isLunatic() ? 1 : 2) && this.alignment != Alignment.ALLY){
			sing(0);
			this.buff(SingCounter.class).countDown(5);
		}
		return super.attackProc(enemy, damage);
	}

	//Higher roll = less chance
	public void sing(int roll){
		if (Random.Int(roll) == 0 ){
			for (Mob mob : Dungeon.level.mobs) {
				//Wake them up basically
				mob.beckon( mob.pos );
				if (isLunatic()){
					Buff.affect(mob, Haste.class, 3f);
				}
			}

			if (Dungeon.level.heroFOV[pos]) {
				CellEmitter.center( pos ).start( Speck.factory( Speck.SCREAM ), 0.3f, 3 );
			}
			
			if (isLunatic() && enemy != null){
				Buff.affect(enemy, Blindness.class, 2f);
			}
		}
	}

    @Override
	protected boolean act() {
		return super.act();
	}

	@Override
	public void die(Object cause) {
		if(Random.Int(500) == 1){
			Dungeon.level.drop( new MystiaVendor(), pos ).sprite.drop();
		}
		super.die(cause);
	}
	
	public static class SingCounter extends CounterBuff{{revivePersists = true;}};
}

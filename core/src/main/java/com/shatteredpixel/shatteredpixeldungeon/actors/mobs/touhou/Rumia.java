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
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.*;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RumiaSprite;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.*;
import com.watabou.utils.Random;

public class Rumia extends Mob {
	{
		spriteClass = RumiaSprite.class;
		HP = HT = 22;
		defenseSkill = 12;
		EXP = 6;
		maxLvl = 13;
        loot = PotionOfShroudingFog.class;
		lootChance = 0.15f;

        flying = true;
	}


    @Override
	public int damageRoll() {
		return Random.NormalIntRange( 4, 12 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 10;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 4);
	}
	
	@Override
	protected boolean act() {
		if(isLunatic() && Blob.volumeAt(this.pos, SmokeScreen.class) > 0 && this.HP < this.HT){
			this.HP++;
		}
		return super.act();
	}

	@Override
	public int attackProc( Char enemy, int damage ) {
		damage = super.attackProc( enemy, damage );
		int reg = Math.min( damage - 4, HT - HP );
		
		if (reg > 0) {
			HP += reg;
			sprite.emitter().burst( Speck.factory( Speck.HEALING ), 1 );
            GameScene.add(Blob.seed(this.pos, 20, SmokeScreen.class));
            Buff.affect(enemy, Blindness.class, 3f);
		}
		
		return damage;
	}
	
}

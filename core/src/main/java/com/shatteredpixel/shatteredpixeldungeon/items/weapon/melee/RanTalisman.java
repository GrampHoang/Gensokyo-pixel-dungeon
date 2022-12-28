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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ChenSprite;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou.Chen;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;

import java.util.ArrayList;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class RanTalisman extends WeaponWithSP {

	{
		image = ItemSpriteSheet.OFUDA;
		hitSound = Assets.Sounds.HIT_STRONG;
		hitSoundPitch = 0.5f;

		tier = 4;
		DLY = 1f; 
		RCH = 99;    //extra reach
	}

	@Override
	public int max(int lvl) {
		return  Math.round(4f*(tier+1)) +    //20 base,
				lvl*Math.round((tier+1)); //Scale unchange
	}

    @Override
	public int proc(Char attacker, Char defender, int damage) {
        float reduction = Dungeon.level.distance(attacker.pos, defender.pos);
       	damage = (int) Math.round( (double)damage / reduction);
		return super.proc(attacker, defender, damage);
	}

	@Override
	protected boolean useSkill(){
		spawnChen();
		Dungeon.hero.spendAndNext(1f);
        return true;
	}

	public void spawnChen(){
		ArrayList<Integer> candidates = new ArrayList<>();
			for (int n : PathFinder.NEIGHBOURS8) {
				if (Dungeon.level.passable[Dungeon.hero.pos + n] && Actor.findChar( Dungeon.hero.pos + n ) == null) {
					candidates.add( Dungeon.hero.pos + n );
				}
			}

			if (!candidates.isEmpty()) {
				MiniChen spawn = new MiniChen();
				spawn.pos = Random.element( candidates );
				spawn.state = spawn.HUNTING;

				GameScene.add( spawn, 1 );
				Dungeon.level.occupyCell(spawn);
				CellEmitter.get(spawn.pos).start(Speck.factory(Speck.LIGHT), 0.2f, 4);
			}
	}


	public static class MiniChen extends Chen {
		{
			spriteClass = MiniChenSprite.class;
			alignment = Alignment.ALLY;
			state = WANDERING;
			//no loot or exp
			maxLvl = -5;
			lootChance = 0f;

			viewDistance = 8;
			HT = 10;
			HP = 10;
			baseSpeed = 2;
		}

		@Override
		public float spawningWeight() {
			return 0;
		}

		@Override	
		public int damageRoll() {
			if (Dungeon.level.water[pos]){
				return Random.NormalIntRange(6, 12);
			}
			return Random.NormalIntRange(8, 15);
		}
		
		public static class MiniChenSprite extends ChenSprite{
			
			public MiniChenSprite(){
				super();
				scale.x = 0.8f;
				scale.y = 0.8f;
			}
		}
	}

}

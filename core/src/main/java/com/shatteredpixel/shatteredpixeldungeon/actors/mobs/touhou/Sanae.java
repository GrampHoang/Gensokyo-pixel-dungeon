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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou;

import java.util.ArrayList;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.EnergyCrystal;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WoodStick;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.sprites.FairySprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MinorikoSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SanaeSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Sanae extends Mob {
	{	
		spriteClass = SanaeSprite.class;

		HP = HT = 120;
		defenseSkill = 26;
		
		EXP = 8;
		maxLvl = 30;

		baseSpeed = 1f;
		Buff.affect(this, ChampionEnemy.AntiMagic.class);
	}


	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 30, 40 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 36;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 16);
	}

	@Override
	public void die(Object cause) {
		super.die(cause);
	}

	@Override
	protected boolean act() {
		return super.act();
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		if (enemy instanceof Hero && 
			Random.Int(5) > (Dungeon.hero.heroClass == HeroClass.MAGE ? 0 : 2)){
			// 20% for Mage and Marisa because they might rely on multiple wands
			// 60% for other
			ArrayList<Wand> wanlist = Dungeon.hero.belongings.getAllItems(Wand.class);
			if (wanlist.size() > 0){
				Wand curWand = wanlist.get(Random.Int(wanlist.size()+1));
				Ballistica shot = new Ballistica( enemy.pos, target, curWand.collisionProperties(target));
				// int cell = shot.collisionPos;
				curWand.fx(shot, new Callback() {
					public void call() {
						curWand.onZap(shot);
					}
				});
			}
		}
		return super.attackProc(enemy, damage);
	}
}

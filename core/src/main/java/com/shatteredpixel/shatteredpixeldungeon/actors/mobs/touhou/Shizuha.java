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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.EnergyCrystal;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Waterskin;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WoodStick;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.FairySprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ShizuhaSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Shizuha extends Mob {
	{	
		spriteClass = ShizuhaSprite.class;

		HP = HT = 120;
		defenseSkill = 26;
		
		EXP = 8;
		maxLvl = 30;

		baseSpeed = 1f;
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
	public int attackProc(Char enemy, int damage) {
		if (enemy instanceof Hero && Random.Int(6) > 2){ //50%
			Waterskin flask = Dungeon.hero.belongings.getItem(Waterskin.class);
			if (flask != null && flask.volume > 0) {
				int countTiles = flask.volume/ (isLunatic() ? 3 : 5);
				for (int i = 0; i < countTiles; i++) {
					int cell = pos + PathFinder.NEIGHBOURS24[Random.Int(24)];
					if (canSpreadGrass(cell)){
						flask.volume--;
						Level.set(cell, (isLunatic() ? Terrain.FURROWED_GRASS : Terrain.HIGH_GRASS));
						GameScene.updateMap(cell);
						CellEmitter.get(cell).burst(LeafParticle.GENERAL, 10);
					}
				}
			}
		}
		return super.attackProc(enemy, damage);
	}

	@Override
	public boolean act() {

		boolean result = super.act();

		//3.6 grass tiles? on average
		int furrowedTiles = Random.chances(new float[]{0, 2, 2, 1, 1, 1});

		for (int i = 0; i < furrowedTiles; i++) {
			int cell = pos + PathFinder.NEIGHBOURS24[Random.Int(24)];
			if (canSpreadGrass(cell)){
				Level.set(cell, Terrain.FURROWED_GRASS);
				GameScene.updateMap(cell);
				CellEmitter.get(cell).burst(LeafParticle.GENERAL, 10);
			}
		}

		Dungeon.observe();

		// for (int i : PathFinder.NEIGHBOURS9) {
		// 	int cell = pos + i;
		// 	if (canSpreadGrass(cell)){
		// 		Level.set(pos+i, Terrain.GRASS);
		// 		GameScene.updateMap( pos + i );
		// 	}
		// }

		return result;
	}

	private boolean canSpreadGrass(int cell){
		return !Dungeon.level.solid[cell] && 
		!(Dungeon.level.map[cell] == Terrain.FURROWED_GRASS || Dungeon.level.map[cell] == Terrain.HIGH_GRASS);
	}
}

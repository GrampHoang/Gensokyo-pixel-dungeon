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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.ToxicGasRoom.ToxicGasSeed;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.ToxicGasRoom.ToxicVent;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ExplosiveTrap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Firebloom;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sorrowmoss;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class FlowerfieldRoom extends StandardRoom {

	boolean isRare = Random.IntRange(0, 1) == 0;

	@Override
	public float[] sizeCatProbs() {
		return new float[]{0, 0, 1};
	}

	@Override
	public boolean canMerge(Level l, Point p, int mergeTerrain) {
		int cell = l.pointToCell(pointInside(p, 1));
		return l.map[cell] == Terrain.EMPTY;
	}

	@Override
	public void paint(Level level) {
		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1, Terrain.GRASS );
		Painter.fill( level, this, 2, Terrain.HIGH_GRASS );
		for (Door door : connected.values()) {
			door.set( Door.Type.REGULAR );
		}

		int mines = (int)Math.round(Math.sqrt(square()));

		switch (sizeCat){
			case NORMAL:
				mines -= 3;
				break;
			case LARGE:
				mines += 13;
				break;
			case GIANT:
				if (isRare) mines+= 50;
				mines += 25;
				break;
		}
        if (sizeCat == SizeCategory.GIANT && isRare && Dungeon.isChallenged(Challenges.TOUHOU) == true){
            // TODO: Spawn Medicine Melancholy here too
            for (int i = 0; i < mines; i++ ){
                level.plant( new Sorrowmoss.Seed(), plantPos( level ));
            }

			for (int i = 0; i < mines/25; i++){
				int cell;
				do {
					cell = level.pointToCell(random(1));
				} while (level.map[cell] != Terrain.EMPTY);
				level.setTrap(new ToxicVent(), cell);
				Blob.seed(cell, 5, ToxicGasSeed.class, level);
				Painter.set(level, cell, Terrain.INACTIVE_TRAP);
			}
        } else {
            for (int i = 0; i < mines; i++ ){
                level.plant( randomSeed(), plantPos( level ));
            }
        }
		

	}

    private static Plant.Seed randomSeed(){
		Plant.Seed result;
		do {
			result = (Plant.Seed) Generator.randomUsingDefaults(Generator.Category.SEED);
		} while (result instanceof Firebloom.Seed || result instanceof Sungrass.Seed);
		return result;
	}

	private int plantPos( Level level ){
		int pos;
		do{
			pos = level.pointToCell(random());
		} while (level.plants.get(pos) != null);
		return pos;
	}

}

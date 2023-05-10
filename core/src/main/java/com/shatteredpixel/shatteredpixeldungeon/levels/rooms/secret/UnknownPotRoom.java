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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Maze;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DelayedExplosiveTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ExplosiveTrap;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class UnknownPotRoom extends SecretRoom {
	
	@Override
	public int minWidth() {
		return 10;
	}
	
	@Override
	public int minHeight() {
		return 10;
	}
	
	@Override
	public int maxWidth() {
		return 12;
	}
	
	@Override
	public int maxHeight() {
		return 12;
	}
	
	@Override
	public void paint(Level level) {
		Painter.fill(level, this, Terrain.WALL);
		Painter.fill(level, this, 1, Terrain.EMPTY);
		
		//true = space, false = wall
		Maze.allowDiagonals = false;
		boolean[][] maze = Maze.generate(this);
		boolean[] passable = new boolean[width()*height()];
		
        for (int x = 1; x < maze.length-1; x++) {
			for (int y = 1; y < maze[0].length-1; y++) {
				if (maze[x][y] == Maze.FILLED) {
                    Painter.fill(level, x + left, y + top, 1, 1, Terrain.SECRET_TRAP);
				}
			}
		}

		for (Point p : getPoints()){
			int cell = level.pointToCell(p);
			if (level.map[cell] == Terrain.SECRET_TRAP){
				level.setTrap(new DelayedExplosiveTrap().hide(), cell);
			}
		}

		PathFinder.setMapSize(width(), height());
		Point entrance = entrance();
		int entrancePos = (entrance.x - left) + width()*(entrance.y - top);
		
		PathFinder.buildDistanceMap( entrancePos, passable );
		
		int bestDist = 0;
		Point bestDistP = new Point();
		for (int i = 0; i < PathFinder.distance.length; i++){
			if (PathFinder.distance[i] != Integer.MAX_VALUE
					&& PathFinder.distance[i] > bestDist){
				bestDist = PathFinder.distance[i];
				bestDistP.x = (i % width()) + left;
				bestDistP.y = (i / width()) + top;
			}
		}
		
		Item prize;
		//1 floor set higher in probability, never cursed
		do {
			if (Random.Int(2) == 0) {
				prize = Generator.randomWeapon((Dungeon.depth / 5) + 1);
			} else {
				prize = Generator.randomArmor((Dungeon.depth / 5) + 1);
			}
		} while (prize.cursed || Challenges.isItemBlocked(prize));
		prize.cursedKnown = true;
		
		//33% chance for an extra update.
		if (Random.Int(3) == 0){
			prize.upgrade();
		}
		
		level.drop(prize, level.pointToCell(bestDistP)).type = Heap.Type.CHEST;
		
		PathFinder.setMapSize(level.width(), level.height());
		
		entrance().set(Door.Type.HIDDEN);
	}
}

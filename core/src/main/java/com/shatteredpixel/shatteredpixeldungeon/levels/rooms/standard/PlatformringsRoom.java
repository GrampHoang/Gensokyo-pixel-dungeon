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

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;

import java.util.ArrayList;

public class PlatformringsRoom extends PatchRoom {
	
	@Override
	public int minWidth() {
		return Math.max(super.minWidth(), 8);
	}
	
	@Override
	public int minHeight() {
		return Math.max(super.minHeight(), 8);
	}
	
	@Override
	public float[] sizeCatProbs() {
		return new float[]{0, 0, 1};
	}
	
	@Override
	public void paint(Level level) {
		int minBoth = Integer.min(this.width(), this.height());
		Painter.fill( level, this, Terrain.WALL );
        for (int i = 1; i < minBoth-2; i++){
            if (i%2 == 1) Painter.fill( level, this, i, Terrain.EMPTY_SP );
            else Painter.fill( level, this, i, Terrain.CHASM );
        }

		// for (int i=top + 1; i < bottom; i++) {
		// 	for (int j=left + 1; j < right; j++) {
        //         if (!patch[xyToPatchCoords(j, i)])
		// 			continue;
		// 		int cell = i * level.width() + j;
        //         if (Random.IntRange(0,3) == 2) level.map[cell] = Terrain.EMPTY_SP;
        //     }
        // }
		
		for (Door door : connected.values()) {
			door.set( Door.Type.REGULAR );
			Painter.drawInside(level, this, door, 2, Terrain.EMPTY_SP);
			Painter.drawInside(level, this, door, 4, Terrain.EMPTY_SP);
		}
		
	}
	
}

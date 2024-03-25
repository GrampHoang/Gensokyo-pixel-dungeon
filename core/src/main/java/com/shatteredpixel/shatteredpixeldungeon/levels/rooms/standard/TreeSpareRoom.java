/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2022 Evan Debenham
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

 package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard;

 import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
 import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
 import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
 import com.watabou.utils.Random;
 
 public class TreeSpareRoom extends StandardRoom {
     
     @Override
     public float[] sizeCatProbs() {
         return new float[]{1, 3, 5};
     }
     
     @Override
     public void paint(Level level) {
        //  Painter.fill( level, this, Terrain.WALL );
        //  for (Door door : connected.values()) {
        //      door.set( Door.Type.REGULAR );
        //  }
        Painter.fill(level, this, 1, Terrain.EMPTY);
        if (width() > height() || (width() == height() && Random.Int(2) == 0)) {
            for (int i = left + 2; i < right; i += 1) {
                if (i%5 == 0){  
                    for (int j = top + 3; j < top + height()- 1 ; j += 5) {
                        Painter.fill(level, i, j, 1, 1, Terrain.ALONE_TREE);
                    }
                } else if (i%5 == 1){
                    for (int j = top + 1; j < top + height()- 1 ; j += 5) {
                        Painter.fill(level, i, j, 1, 1, Terrain.ALONE_TREE);
                    }
                } else if (i%5 == 2){
                    for (int j = top + 4; j < top + height()- 1 ; j += 5) {
                        Painter.fill(level, i, j, 1, 1, Terrain.ALONE_TREE);
                    }
                } else if (i%5 == 3){
                    for (int j = top + 2; j < top + height()- 1 ; j += 5) {
                        Painter.fill(level, i, j, 1, 1, Terrain.ALONE_TREE);
                    }
                } else {
                    for (int j = top + 5; j < top + height()- 1 ; j += 5) {
                        Painter.fill(level, i, j, 1, 1, Terrain.ALONE_TREE);
                    }
                }
            }
        } else {
            for (int i = bottom + 2; i < top; i += 2) {
                if (i%5 == 0){
                    for (int j = left + 2; j < left + width()- 1 ; j += 5) {
                        Painter.fill(level, i, j, 1, 1, Terrain.ALONE_TREE);
                    }
                } else if (i%5 == 1){
                    for (int j = left; j < left + width()- 1 ; j += 5) {
                        Painter.fill(level, i, j, 1, 1, Terrain.ALONE_TREE);
                    }
                } else if (i%5 == 2){
                    for (int j = left + 3; j < left + width()- 1 ; j += 5) {
                        Painter.fill(level, i, j, 1, 1, Terrain.ALONE_TREE);
                    }
                } else if (i%5 == 3){
                    for (int j = left + 1; j < left + width()- 1 ; j += 5) {
                        Painter.fill(level, i, j, 1, 1, Terrain.ALONE_TREE);
                    }
                } else {
                    // nothing
                }
            }
        }
     }
 }
 
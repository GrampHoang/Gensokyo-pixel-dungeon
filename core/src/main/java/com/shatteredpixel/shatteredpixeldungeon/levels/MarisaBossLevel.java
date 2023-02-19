/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2021 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou.MarisaBoss;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import com.watabou.utils.Random;

public class MarisaBossLevel extends Level {

    private static final int SIZE_W = 29;
    private static final int SIZE_H = 21;

    {
        color1 = 0xeeee00;
        color2 = 0x000000;

        viewDistance = 8;
    }

    @Override
    public String tilesTex() {
        return Assets.Environment.TILES_CITY;
    }

    @Override
    public String waterTex() {
        return Assets.Environment.WATER_CITY;
    }

    @Override
    protected boolean build() {

        setSize(31, 20);
        //setup wall
        for (int i=1; i < SIZE_H-1; i++) {
            map[i*width()] = Terrain.WALL;
        }

        //set bookshelf
        for (int i=2; i < SIZE_H-1; i++) {
            for (int j=1; j < SIZE_W-1; j++) {
                if (i%2==0){
                    map[i * width() + j] = Terrain.BOOKSHELF;
                    if(Random.IntRange(0,30) == 6) map[i * width() + j] = Terrain.STATUE; //occasion statue
                }
                else{
                    map[i * width() + j] = Terrain.EMPTY;
                }
                
            }
        }
        //set empty tiles on the side
        for (int i=2; i < SIZE_H-1; i=i+2) {
            if(Random.IntRange(0,3) == 1){
                map[i * width() +  1] = Terrain.EMPTY;
                map[i * width() + 27] = Terrain.EMPTY;
            }
        }

        //make empty tile between shelf
        for (int j=2; j < SIZE_H-1; j++) {
            for (int i = 15; i < SIZE_W-1; i = i+Random.IntRange(3, 6)) {
                    map[j * width() + i] = Terrain.EMPTY;
                }
                for (int i = 15; i > 0; i = i-Random.IntRange(3, 6)) {
                    map[j * width() + i] = Terrain.EMPTY;
                }
        }

        //Middle path
        for (int i = 1; i < SIZE_H - 1; i++) {
            map[i * width() + 15] = Terrain.EMPTY_SP;
        }

        //set and decorate entrance + exit wall
        for (int i=1; i < SIZE_W-1; i++) {
            map[i + width()] = Terrain.WALL;
            map[i + 2* width()] = Terrain.BOOKSHELF;
            map[i + 18*width()] = Terrain.BOOKSHELF;
            map[i + 19*width()] = Terrain.WALL;
        }

        //set and decorate entrance + exit
        entrance = (SIZE_H - 3) * width() + 15;
        map[entrance - 2] = Terrain.WALL;
        map[entrance - 1] = Terrain.STATUE;
        map[entrance + 1] = Terrain.STATUE;
        map[entrance + 2] = Terrain.WALL;
        exit     = 2* width() + 15;
        map[exit - 2] = Terrain.WALL;
        map[exit - 1] = Terrain.STATUE;
        map[exit + 1] = Terrain.STATUE;
        map[exit + 2] = Terrain.WALL;
        map[entrance] = Terrain.ENTRANCE;

        transitions.add(new LevelTransition(this, entrance, LevelTransition.Type.REGULAR_ENTRANCE));
        return true;
    }

    @Override
    public void occupyCell( Char ch ) {
        super.occupyCell( ch );
        //If the hero is here
        // entrance still there = boss haven't appeared
        // Exit not there => Haven't triger and kill boss
        // And move away from the entrance
        if (map[entrance] == Terrain.ENTRANCE 
            && ch == Dungeon.hero
            && map[exit] != Terrain.EXIT
            && Dungeon.level.distance(ch.pos, entrance) > 2) {
            seal();
        }
    }

    @Override
    public void seal() {
        super.seal();
        map[6 * width() + 6] = Terrain.EMPTY;
        set( entrance, Terrain.EMPTY_SP );
        set( exit, Terrain.EMPTY_SP );
        GameScene.updateMap( entrance );
        GameScene.updateMap( exit );

        Dungeon.observe();

        MarisaBoss mari = new MarisaBoss();
        mari.pos = (4 * width() + 15);;
        GameScene.add(mari);

        // ReimuBoss mari = new ReimuBoss();
        // mari.pos = (4 * width() + 15);;
        // GameScene.add(mari);

    }

    @Override
    public void unseal() {
        super.unseal();
        set( entrance, Terrain.ENTRANCE );
        GameScene.updateMap( entrance );
        transitions.add(new LevelTransition(this, exit, LevelTransition.Type.REGULAR_EXIT));
        set( exit, Terrain.EXIT );
        GameScene.updateMap( exit );

        CellEmitter.get(exit-1).burst(ShadowParticle.UP, 25);
        CellEmitter.get(exit).burst(ShadowParticle.UP, 100);
        CellEmitter.get(exit+1).burst(ShadowParticle.UP, 25);

        Dungeon.observe();
    }

    @Override
    public Mob createMob() {
        return null;
    }

    @Override
    protected void createMobs() {
    }

    public Actor addRespawner() {
        return null;
    }

    @Override
    protected void createItems() {
    }

    @Override
    public int randomRespawnCell( Char ch ) {
        return entrance-width();
    }

    @Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
        bundle.put( "MARISA_ENTRANCE", entrance );
        bundle.put( "MARISA_EXIT", exit );
    }

    @Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
        entrance = bundle.getInt( "MARISA_ENTRANCE" );
        exit = bundle.getInt( "MARISA_EXIT" );
	}

}
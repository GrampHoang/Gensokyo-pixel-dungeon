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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou.CirnoBoss;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou.MeilingBoss;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou.Alice;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou.MarisaBoss;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.Bundle;


import com.watabou.utils.Random;

public class MarisaBossLevel extends Level {

    private static final int SIZE_W = 29;
    private static final int SIZE_H = 18;

    {
        color1 = 0xeeee00;
        color2 = 0x000000;

        viewDistance = 69;
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

        for (int i=1; i < SIZE_H-1; i++) {
            for (int j=1; j < SIZE_W-1; j++) {
                int o = Random.IntRange(0,20);
                if ( o < 3)      { map[i * width() + j] = Terrain.WATER;}
                else if ( o < 5) { map[i * width() + j] = Terrain.WALL;}
                else if ( o < 7) { map[i * width() + j] = Terrain.BOOKSHELF;}
                else   
                    {   if (Random.IntRange(0,3) == 0){
                            map[i * width() + j] = Terrain.EMPTY_DECO;
                            }
                        else{
                            map[i * width() + j] = Terrain.EMPTY;
                        }
                    }
            }
        }

        for (int i = 1; i < SIZE_H - 1; i++) {
            map[i * width() + 15] = Terrain.EMPTY_SP;
        }

        entrance = 16 * width() + 15;
        exit     = width() + 15;
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
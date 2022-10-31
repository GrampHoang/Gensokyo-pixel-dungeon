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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.Bundle;

import com.watabou.utils.Random;

public class CirnoBossLevel extends Level {

    private static final int SIZE = 19;

    {
        color1 = 0x4b6794;
        color2 = 0x3a4f70;

        viewDistance = 69;
    }

    @Override
    public String tilesTex() {
        return Assets.Environment.TILES_FOREST;
    }

    @Override
    public String waterTex() {
        return Assets.Environment.WATER_CLEAR;
    }

    @Override
    protected boolean build() {

        setSize(19, 19);

        for (int i=1; i < SIZE-1; i++) {
            for (int j=1; j < SIZE-1; j++) {
                map[i * width() + j] = Terrain.WATER;
                if (Random.IntRange(0,15) == 0){
                    map[i * width() + j] = Terrain.EMPTY;
                }
            }
        }

        for (int i = (SIZE/2)-1; i <= (SIZE/2) +1; i++) {
            for (int j = (SIZE/2)-1; j <= (SIZE/2) +1; j++) {
                map[i * width() + j] = Terrain.EMPTY_SP;
            }
        }

        map[4 * width() + SIZE/4] = Terrain.EMPTY_SP;
        map[4 * width() + SIZE/2] = Terrain.EMPTY_SP;
        map[4 * width() + SIZE/4*3+2] = Terrain.EMPTY_SP;

        map[((SIZE/2-2) * width()) + SIZE/2] = Terrain.EMPTY_SP;
        map[(SIZE/2 * width()) + SIZE/4] = Terrain.EMPTY_SP;
        map[(SIZE/2 * width()) + SIZE/2] = Terrain.WATER;
        map[(SIZE/2 * width()) + SIZE/4*3+2] = Terrain.EMPTY_SP;
        map[(SIZE/2+2) * width()+ SIZE/2] = Terrain.EMPTY_SP;

        map[(SIZE/4*3+2) * width() + SIZE/4] = Terrain.EMPTY_SP;
        map[(SIZE/4*3+2) * width() + SIZE/2] = Terrain.EMPTY_SP;
        map[(SIZE/4*3+2) * width() + SIZE/4*3+2] = Terrain.EMPTY_SP;

        //These 2 also determined the fact that you can't pick up frost bomb from cirno
        entrance = (SIZE/2 * width()) + SIZE/2 - 2;
        exit     = (SIZE/2 * width()) + SIZE/2 + 2;
        map[entrance] = Terrain.ENTRANCE;

        transitions.add(new LevelTransition(this, entrance, LevelTransition.Type.REGULAR_ENTRANCE));
        return true;
    }

    @Override
    public void occupyCell( Char ch ) {
        super.occupyCell( ch );

        if (map[entrance] == Terrain.ENTRANCE 
            && ch == Dungeon.hero
            && map[exit] != Terrain.EXIT
            && Dungeon.level.distance(ch.pos, entrance) >= 0) {
            seal();
        }
    }

    @Override
    public void seal() {
        super.seal();
        set( entrance, Terrain.EMPTY_SP );
        set( exit, Terrain.EMPTY_SP );
        GameScene.updateMap( entrance );
        GameScene.updateMap( exit );

        Dungeon.observe();

        CirnoBoss cirno = new CirnoBoss();
        cirno.pos = (SIZE/2)* width() + SIZE/2;
        GameScene.add( cirno );
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
        bundle.put( "CIRNO_ENTRANCE", entrance );
        bundle.put( "CIRNO_EXIT", exit );
    }

    @Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
        entrance = bundle.getInt( "CIRNO_ENTRANCE" );
        exit = bundle.getInt( "CIRNO_EXIT" );
	}
}
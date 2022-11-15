package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou.Sunny;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou.Luna;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou.Star;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Maze;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.Bundle;

import com.watabou.utils.Random;
import com.watabou.utils.Rect;

public class ForestBossLevel extends Level {

    private static final int SIZE_W = 21;
    private static final int SIZE_H = 21;

    {
		color1 = 0x48763c;
		color2 = 0x59994a;
        viewDistance = 4;
    }

    @Override
    public String tilesTex() {
        return Assets.Environment.TILES_FOREST;
    }

    @Override
    public String waterTex() {
        return Assets.Environment.WATER_CLEAR;
    }

    private static final Rect level = new Rect(0, 0, SIZE_W, SIZE_H);
    private static final Rect mid_area = new Rect(SIZE_H/2 - 1, SIZE_W/2 - 1, SIZE_H/2 + 2, SIZE_W/2 + 2);
    
    @Override
    protected boolean build() {

        setSize(SIZE_W, SIZE_H);

        Painter.fill(this, level, Terrain.WALL);
		Painter.fill(this, level, 1, Terrain.EMPTY);
        
        boolean[][] maze = Maze.generate(level, map, width(), Terrain.WALL);
        boolean[] passable = new boolean[width()*height()];

        for (int x = 0; x < maze.length; x++) {
			for (int y = 0; y < maze[0].length; y++) {
				if (maze[x][y] == Maze.FILLED) {
					Painter.fill(this, x, y, 1, 1, Terrain.WALL);
				}
				passable[x + width()*y] = maze[x][y] == Maze.EMPTY;
			}
		}

        for (int i = 1; i < SIZE_H-1; i++) {
            for (int j = 1; j < SIZE_W-1; j++) {
                if (Random.IntRange(0, 12) == 2){
                    map[i * width() + j] = Terrain.EMPTY;
                    }
                }
                
        }
        Painter.fill(this, mid_area, Terrain.WATER);

        entrance = width()+1;
        exit     = (SIZE_H - 2) * width() + SIZE_W - 2;
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
        set( entrance, Terrain.EMPTY_DECO );
        set( exit, Terrain.EMPTY_DECO );
        GameScene.updateMap( entrance );
        GameScene.updateMap( exit );

        Dungeon.observe();

        Sunny sunny = new Sunny();
        sunny.pos = (SIZE_H - 2) * width() + SIZE_W - 2;
        GameScene.add(sunny);

        Luna luna = new Luna();
        luna.pos = 1 * width() + SIZE_W - 2;
        GameScene.add(luna);
        
        Star star = new Star();
        star.pos = (SIZE_H - 2) * width() + 1;
        GameScene.add(star);

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
        bundle.put( "REMILIA_ENTRANCE", entrance );
        bundle.put( "REMILIA__EXIT", exit );
    }

    @Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
        entrance = bundle.getInt( "REMILIA_ENTRANCE" );
        exit = bundle.getInt( "REMILIA__EXIT" );
	}

}
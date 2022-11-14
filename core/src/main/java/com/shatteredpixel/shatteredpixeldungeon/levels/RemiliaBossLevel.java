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
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.Bundle;

import com.watabou.utils.Random;
import com.watabou.utils.Rect;

public class RemiliaBossLevel extends Level {

    private static final int SIZE_W = 17;
    private static final int SIZE_H = 17;

    {
        color1 = 0xeeee00;
        color2 = 0x000000;

        viewDistance = 8;
    }

    @Override
    public String tilesTex() {
        return Assets.Environment.TILES_SDM;
    }

    @Override
    public String waterTex() {
        return Assets.Environment.WATER_CITY;
    }

    private static final Rect emptyRoom = new Rect(0, 0, 5, 7);
    private static final Rect emptyRoom2 = new Rect(12, 0, 17, 7);
    private static final Rect emptyRoom3 = new Rect(0, 10, 5, 17);
    private static final Rect emptyRoom4 = new Rect(12, 10, 17, 17);

    private static final Rect midpath = new Rect(8, 1, 8, 16);
    
    @Override
    protected boolean build() {

        setSize(17, 17);

        for (int i = 1; i < SIZE_H-1; i++) {
            for (int j = 1; j < SIZE_W-1; j++) {
                    map[i * width() + j] = Terrain.EMPTY;
                }
                
        }

        for (int i = 0; i < SIZE_H-1; i= i + 2) {
            map[i * width() + 7] = Terrain.WALL_DECO;
            map[i * width() + 9] = Terrain.WALL_DECO;                
        }

        for (int i = 1; i < SIZE_W-1; i= i + 2) {
            map[8 * width() + i] = Terrain.STATUE;            
        }

        Painter.fill(this, midpath, Terrain.EMPTY_SP);

        Painter.fill(this, emptyRoom, Terrain.WALL);
		Painter.fill(this, emptyRoom, 1, Terrain.EMPTY);
        Painter.set(this, emptyRoom.right-1, emptyRoom.top+3, Terrain.DOOR);
        Painter.set(this, emptyRoom.left+2, emptyRoom.bottom-1, Terrain.DOOR);

        Painter.fill(this, emptyRoom2, Terrain.WALL);
        Painter.fill(this, emptyRoom2, 1, Terrain.EMPTY);
        Painter.set(this, emptyRoom2.left, emptyRoom2.top+3, Terrain.DOOR);
        Painter.set(this, emptyRoom2.left+2, emptyRoom2.bottom-1, Terrain.DOOR);

        Painter.fill(this, emptyRoom3, Terrain.WALL);
        Painter.fill(this, emptyRoom3, 1, Terrain.EMPTY);
        Painter.set(this, emptyRoom3.right-1, emptyRoom3.top+3, Terrain.DOOR);
        Painter.set(this, emptyRoom3.left+2, emptyRoom3.top, Terrain.DOOR);

        Painter.fill(this, emptyRoom4, Terrain.WALL);
        Painter.fill(this, emptyRoom4, 1, Terrain.EMPTY);
        Painter.set(this, emptyRoom4.left, emptyRoom4.top+3, Terrain.DOOR);
        Painter.set(this, emptyRoom4.left+2, emptyRoom4.top, Terrain.DOOR);

        entrance = 15 * width() + 8;
        exit     = 1 * width() + 8;
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

        RemiliaBoss boss = new RemiliaBoss();
        boss.pos = (3 * width() + 7);;
        GameScene.add(boss);

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
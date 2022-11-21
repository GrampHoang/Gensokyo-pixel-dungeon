package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.Bundle;

import com.watabou.utils.Random;
import com.watabou.utils.Rect;

public class YukariBossLevel extends Level {

    private static final int SIZE_W = 33;
    private static final int SIZE_H = 33;

    {
		color1 = 0x534f3e;
		color2 = 0xb9d661;

        viewDistance = 8;
    }

    @Override
    public String tilesTex() {
        return Assets.Environment.TILES_SHRINE;
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
    
	public static int[] gapPositions = new int[]{ 4 + 13*SIZE_W, 28 + 13*SIZE_W, 4 + 37*SIZE_W, 28 + 37*SIZE_W };

    @Override
    protected boolean build() {

        setSize(SIZE_W, SIZE_H);

        for (int i = 1; i < SIZE_H-2; i++) {
            for (int j = 1; j < SIZE_W-2; j++) {
                    map[i * width() + j] = Terrain.EMPTY;
                }
                
        }

        exit     = entrance + 1;
        buildEntrance();
		buildCorners();
		buildSides();
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
            // && map[exit] != Terrain.EXIT
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

        YukariBoss boss = new YukariBoss();
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
	
	private static final short n = -1; //used when a tile shouldn't be changed
	private static final short W = Terrain.WALL;
	private static final short e = Terrain.EMPTY;
	private static final short s = Terrain.EMPTY_SP;

	private static short[] side1 = {
		W, W, W, W, W, W, W, W, W, W,
		W, W, W, s, s, s, s, W, W, W,
		W, W, e, s, s, s, s, e, W, W,
		W, e, e, s, s, s, s, e, e, W,
		W, e, W, W, W, W, W, W, e, W,
		n, e, W, W, W, W, W, W, e, n,
		n, e, e, W, W, W, W, e, e, n,
		n, n, e, e, W, W, e, e, n, n,
		n, n, n, e, e, e, e, n, n, n,
		n, n, n, n, n, n, n, n, n, n,
	};

	private static short[][] sideVariants = {
		side1
};

	private void buildSides(){
		int No = 11 + width();
		int Ea = 30 + 11*width();
		int So = 1  + 11*width();
		int We = 11 + 30*width();
		short[] sideTiles = Random.oneOf(sideVariants);
		
		for(int i = 0; i < sideTiles.length; i++){
			if (i % 10 == 0 && i != 0){
				No += (width() - 10);
				Ea += (width() + 10);
				So -= (width() - 10);
				We -= (width() + 10);
			}

			if (sideTiles[i] != n) map[No] = map[Ea] = map[So] = map[We] = sideTiles[i];
			No++; Ea--; So++; We--;
		}
	}

	private static short[] corner1 = {
		W, W, W, W, W, W, W, W, W, W,
		W, s, s, s, e, e, e, W, W, W,
		W, s, s, s, W, W, e, e, W, W,
		W, s, s, s, W, W, W, e, e, W,
		W, e, W, W, W, W, W, W, e, n,
		W, e, W, W, W, W, W, n, n, n,
		W, e, e, W, W, W, n, n, n, n,
		W, W, e, e, W, n, n, n, n, n,
		W, W, W, e, e, n, n, n, n, n,
		W, W, W, W, n, n, n, n, n, n,
	};

	private static short[] corner2 = {
			W, W, W, W, W, W, W, W, W, W,
			W, s, s, s, W, W, W, W, W, W,
			W, s, s, s, e, e, e, e, e, W,
			W, s, s, s, W, W, W, W, e, e,
			W, W, e, W, W, W, W, W, W, e,
			W, W, e, W, W, W, W, n, n, n,
			W, W, e, W, W, W, n, n, n, n,
			W, W, e, W, W, n, n, n, n, n,
			W, W, e, e, W, n, n, n, n, n,
			W, W, W, e, e, n, n, n, n, n,
	};

	private static short[] corner3 = {
			W, W, W, W, W, W, W, W, W, W,
			W, s, s, s, W, W, W, W, W, W,
			W, s, s, s, e, e, e, e, W, W,
			W, s, s, s, W, W, W, e, W, W,
			W, W, e, W, W, W, W, e, W, n,
			W, W, e, W, W, W, W, e, e, n,
			W, W, e, W, W, W, n, n, n, n,
			W, W, e, e, e, e, n, n, n, n,
			W, W, W, W, W, e, n, n, n, n,
			W, W, W, W, n, n, n, n, n, n,
	};

	private static short[] corner4 = {
			W, W, W, W, W, W, W, W, W, W,
			W, s, s, s, W, W, W, W, W, W,
			W, s, s, s, e, e, e, W, W, W,
			W, s, s, s, W, W, e, W, W, W,
			W, W, e, W, W, W, e, W, W, n,
			W, W, e, W, W, W, e, e, n, n,
			W, W, e, e, e, e, e, n, n, n,
			W, W, W, W, W, e, n, n, n, n,
			W, W, W, W, W, n, n, n, n, n,
			W, W, W, W, n, n, n, n, n, n,
	};

	private static short[][] cornerVariants = {
			corner1,
			corner2,
			corner3,
			corner4
	};

	private void buildCorners(){
		int NW = 1 + 1*width();
		int NE = 30 + 1*width();
		int SE = 30 + 30*width();
		int SW = 1 + 30*width();

		short[] cornerTiles = Random.oneOf(cornerVariants);
		for(int i = 0; i < cornerTiles.length; i++){
			if (i % 10 == 0 && i != 0){
				NW += (width() - 10);
				NE += (width() + 10);
				SE -= (width() - 10);
				SW -= (width() + 10);
			}

			if (cornerTiles[i] != n) map[NW] = map[NE] = map[SE] = map[SW] = cornerTiles[i];
			NW++; NE--; SW++; SE--;
		}
	}

	private static short[] entrance1 = {
		n, n, n, n, n, n, n, n,
		n, n, n, n, n, n, n, n,
		n, n, n, n, W, e, W, W,
		n, n, n, W, W, e, W, W,
		n, n, W, W, e, e, e, e,
		n, n, e, e, e, W, W, e,
		n, n, W, W, e, W, e, e,
		n, n, W, W, e, e, e, e
	};

	private static short[] entrance2 = {
		n, n, n, n, n, n, n, n,
		n, n, n, n, n, n, n, n,
		n, n, n, n, n, e, e, e,
		n, n, n, W, e, W, W, e,
		n, n, n, e, e, e, e, e,
		n, n, e, W, e, W, W, e,
		n, n, e, W, e, W, e, e,
		n, n, e, e, e, e, e, e
	};

	private static short[] entrance3 = {
		n, n, n, n, n, n, n, n,
		n, n, n, n, n, n, n, n,
		n, n, n, n, n, n, n, n,
		n, n, n, W, W, e, W, W,
		n, n, n, W, W, e, W, W,
		n, n, n, e, e, e, e, e,
		n, n, n, W, W, e, W, e,
		n, n, n, W, W, e, e, e
	};

	private static short[] entrance4 = {
		n, n, n, n, n, n, n, n,
		n, n, n, n, n, n, n, e,
		n, n, n, n, n, n, W, e,
		n, n, n, n, n, W, W, e,
		n, n, n, n, W, W, W, e,
		n, n, n, W, W, W, W, e,
		n, n, W, W, W, W, e, e,
		n, e, e, e, e, e, e, e
	};

	private static short[][] entranceVariants = {
		entrance1,
		entrance2,
		entrance3,
		entrance4
	};

	private void buildEntrance(){
		int entrance = 16 + 16*width();

		//entrance area
		int NW = entrance - 7 - 7*width();
		int NE = entrance + 7 - 7*width();
		int SE = entrance + 7 + 7*width();
		int SW = entrance - 7 + 7*width();

		short[] entranceTiles = Random.oneOf(entranceVariants);
		for (int i = 0; i < entranceTiles.length; i++){
			if (i % 8 == 0 && i != 0){
				NW += (width() - 8);
				NE += (width() + 8);
				SE -= (width() - 8);
				SW -= (width() + 8);
			}

			if (entranceTiles[i] != n) map[NW] = map[NE] = map[SE] = map[SW] = entranceTiles[i];
			NW++; NE--; SW++; SE--;
		}

		Painter.set(this, entrance, Terrain.ENTRANCE);
		transitions.add(new LevelTransition(this, entrance, LevelTransition.Type.REGULAR_ENTRANCE));
	}

}
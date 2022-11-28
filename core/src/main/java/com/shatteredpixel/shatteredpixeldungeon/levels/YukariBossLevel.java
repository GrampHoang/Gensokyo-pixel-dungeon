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

    private static final int SIZE_W = 35;
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
    
	public static int[] gapPositions = new int[]{109,  87,  135, 
												562,        593,
												1019, 1067, 1045 };

    @Override
    protected boolean build() {

        setSize(SIZE_W, SIZE_H);

        for (int i = 1; i < SIZE_H-1; i++) {
            for (int j = 1; j < SIZE_W-1; j++) {
                    map[i * width() + j] = Terrain.EMPTY;
                }
                
        }

		for (int i = 1; i < SIZE_H-1; i++) {
            map[i * width() + 1] = Terrain.WALL;
			map[i * width() + 33] = Terrain.WALL;
        }

		boolean[] patch = Patch.generate( width, height, 0.45f, 1, true );
		for (int i = 1; i < length()-1; i++) {
			if (map[i] == Terrain.EMPTY) {
				if (patch[i]){
					map[i] = Terrain.WATER;
				}
			}
		}

        buildEntrance();
		buildCorners();
		buildSidesNS();
		buildSidesEW();
        transitions.add(new LevelTransition(this, entrance, LevelTransition.Type.REGULAR_ENTRANCE));
		entrance = 17 + 16*width();
		exit = 17 + 8*width();
		Painter.set(this, exit, Terrain.EMPTY);
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

        YukariBoss boss = new YukariBoss();
        boss.pos = exit;
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

	private static short[] sideNS1 = {
		W, W, W, W, W, W, W, W, W, W, W,
		W, W, W, W, s, s, s, W, W, W, W,
		W, e, e, e, s, s, s, e, e, e, W,
		W, e, W, W, s, s, s, W, W, e, W,
		W, e, e, W, W, W, W, W, e, e, W,
		W, W, e, e, W, W, W, e, e, W, W,
		n, W, W, e, e, e, e, e, W, W, n,
		n, n, n, n, n, n, n, n, n, n, n,
		n, n, n, n, n, n, n, n, n, n, n,
		n, n, n, n, n, n, n, n, n, n, n,
	};

	private static short[] sideEW1 = {
		W, W, W, W, W, W, n, n, n, n, 
		W, e, e, e, W, W, n, n, n, n,
		W, e, W, e, e, e, e, n, n, n,
		W, e, W, W, W, e, e, n, n, n,
		s, s, s, W, W, W, e, n, n, n,
		s, s, s, W, W, W, e, n, n, n,
		s, s, s, W, W, W, e, n, n, n,
		W, e, W, W, W, e, e, n, n, n,
		W, e, W, e, e, e, e, n, n, n,
		W, e, e, e, W, W, n, n, n, n,
		W, W, W, W, W, W, n, n, n, n, 
	};

	private static short[][] sideNSVariants = {
		sideNS1
	};

	private static short[][] sideEWVariants = {
		sideEW1
	};

	private void buildSidesNS(){
		int No = 12 + 0*width();
		int So = 12  + 32*width();
		
		
		short[] sideTiles = Random.oneOf(sideNSVariants);
		
		for(int i = 0; i < sideTiles.length; i++){
			if (i % 11 == 0 && i != 0){
				No += (width() - 11);
				So -= (width() + 11);
			}

			if (sideTiles[i] != n) map[No] = map[So] =  sideTiles[i];
			No++; So++;
		}
	}

	private void buildSidesEW(){
		int We = 1  + 11*width();
		int Ea = 34 + 11*width();
		
		short[] sideTiles = Random.oneOf(sideEWVariants);
		
		for(int i = 0; i < sideTiles.length; i++){
			if (i % 10 == 0 && i != 0){
				We += (width() - 10);
				Ea += (width() + 10);
			}

			if (sideTiles[i] != n) map[Ea] =  map[We] = sideTiles[i];
			 Ea--; We++;
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
		int NW = 2 + 1*width();
		int NE = 32 + 1*width();
		int SE = 32 + 31*width();
		int SW = 2 + 31*width();

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
		int entrance = 17 + 16*width();

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
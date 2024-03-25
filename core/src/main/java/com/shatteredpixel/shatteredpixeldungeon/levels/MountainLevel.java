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

import java.util.ArrayList;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.AyaNPC;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.shatteredpixeldungeon.effects.Ripple;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.SewerPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.AlarmTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.BlazingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ChillingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ConfusionTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.CorrosionTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.CursingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisarmingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisintegrationTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DistortionTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FlashingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FlockTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FrostTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GatewayTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GeyserTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrimTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GuardianTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.OozeTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PitfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.RockfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ShockingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.StormTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.SummoningTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.TeleportationTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ToxicTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WarpingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WeakeningTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WornDartTrap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SuikaRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.UnknownPotRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.ColorMath;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class MountainLevel extends RegularLevel {

	{
		
		color1 = 0xc43424;
		color2 = 0xf4a824;
	}

	public void playLevelMusic(){
		Music.INSTANCE.playTracks(
				new String[]{Assets.Music.SEWERS_1, Assets.Music.SEWERS_2, Assets.Music.SEWERS_2},
				new float[]{1, 1, 0.5f},
				false);
	}
	
	@Override
	protected ArrayList<Room> initRooms() {
		ArrayList<Room> initRooms = super.initRooms();
		// if(Dungeon.depth == 4) initRooms.add(new SuikaRoom());
		// if(Dungeon.depth == 1) initRooms.add(new UnknownPotRoom()); //This is a test room for Reisen's quest, but I'm gonna keep it
		return initRooms;
	}

	@Override
	protected int standardRooms(boolean forceMax) {
		if (forceMax) return 11;
		//9 to 11, average 10
		return 9+Random.chances(new float[]{1, 3, 1});
	}
	
	@Override
	protected int specialRooms(boolean forceMax) {
		if (forceMax) return 4;
		//3 to 4, average 3.5
		return 3+Random.chances(new float[]{1, 1});
	}
	
	@Override
	protected Painter painter() {
		return new SewerPainter()
				.setWater(feeling == Feeling.WATER ? 0.65f : 0.20f, 3)
				.setGrass(feeling == Feeling.GRASS ? 0.90f : 0.65f, 4)
				.setTraps(nTraps(), trapClasses(), trapChances());
	}
	
	@Override
	public String tilesTex() {
		return Assets.Environment.TILES_MOUNTAIN;
	}
	
	@Override
	public String waterTex() {
		return Assets.Environment.WATER_CLEAR;
	}
	
	@Override
	protected Class<?>[] trapClasses() {
		return new Class[]{
				FrostTrap.class, StormTrap.class, CorrosionTrap.class, BlazingTrap.class, DisintegrationTrap.class,
				RockfallTrap.class, FlashingTrap.class, GuardianTrap.class, WeakeningTrap.class,
				DisarmingTrap.class, SummoningTrap.class, WarpingTrap.class, CursingTrap.class, GrimTrap.class, DistortionTrap.class, GatewayTrap.class, GeyserTrap.class };
}

	@Override
	protected float[] trapChances() {
		return new float[]{
			4, 4, 4, 4, 4,
			2, 2, 2, 2,
			1, 4, 4, 1, 4, 1, 1, 1 };
	}
	
	@Override
	protected void createItems() {
		super.createItems();
	}
	
	@Override
	public Group addVisuals() {
		super.addVisuals();
		addMountainVisuals(this, visuals);
		return visuals;
	}
	
	public static void addMountainVisuals( Level level, Group group ) {
		for (int i=0; i < level.length(); i++) {
			if (level.map[i] == Terrain.WALL_DECO) {
				group.add( new Leaf( i ) );
			}
		}
	}
	
	@Override
	public String tileName( int tile ) {
		switch (tile) {
			case Terrain.WALL:
				return Messages.get(MountainLevel.class, "wall_name");
			case Terrain.WATER:
				return Messages.get(MountainLevel.class, "water_name");
			case Terrain.ALONE_TREE:
				return Messages.get(MountainLevel.class, "alone_tree_name");
			default:
				return super.tileName( tile );
		}
	}
	
	@Override
	public String tileDesc(int tile) {
		switch (tile) {
			case Terrain.WALL:
				return Messages.get(MountainLevel.class, "wall_desc");
			case Terrain.WALL_DECO:
				return Messages.get(MountainLevel.class, "wall_deco_desc");
			case Terrain.EMPTY_DECO:
				return Messages.get(MountainLevel.class, "empty_deco_desc");
			case Terrain.ALONE_TREE:
				return Messages.get(MountainLevel.class, "alone_tree_desc");
			default:
				return super.tileDesc( tile );
		}
	}
	
	public static class Leaf extends Emitter {
		
		private int pos;

		public static final Emitter.Factory factory = new Factory() {
			
			@Override
			public void emit( Emitter emitter, int index, float x, float y ) {
				MapleLeaftParticle p = (MapleLeaftParticle)emitter.recycle( MapleLeaftParticle.class );
				p.color( ColorMath.random( 0xc43424, 0xf4a824 ) );
				p.reset( x, y );
			}
		};
		
		public Leaf( int pos ) {
			super();
			
			this.pos = pos;
			
			PointF p = DungeonTilemap.tileCenterToWorld( pos );
			pos( p.x - 6, p.y - 8, 12, 12 );
			
			pour( factory, 0.2f );
		}
		
		@Override
		public void update() {
			if (visible = (pos < Dungeon.level.heroFOV.length && Dungeon.level.heroFOV[pos])) {
				super.update();
			}
		}
	}

    public static final class MapleLeaftParticle extends PixelParticle.Shrinking {
		
		public MapleLeaftParticle() {
			super();
			lifespan = 1f;
			color( 0xE9CDAB );
			speed.set( Random.Float( -8, 8 ), Random.Float( 3, 5) );
		}
		
		public void reset( float x, float y ) {
			revive();
			
			this.x = x;
			this.y = y;
			
			left = lifespan;
			size = Random.Float( 0.75f, 1f );
		}
		
		@Override
		public void update() {
			super.update();
		}
	}
}

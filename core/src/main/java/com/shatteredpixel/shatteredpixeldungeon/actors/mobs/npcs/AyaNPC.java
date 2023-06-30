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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MeatPie;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLevitation;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.BambooLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.ForestLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.HallsLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.SDMLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.ShrineLevel;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.AyaNPCSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class AyaNPC extends NPC {

	{
		spriteClass = AyaNPCSprite.class;
		state = WANDERING;
		flying = true; //She fly
        baseSpeed = 2f;
	}

	private int cooldown = 0;

	@Override
	protected boolean act() {
		if (Dungeon.level.heroFOV[pos]){
			Notes.add( Notes.Landmark.AYA );
		}
		cooldown--;
		if (cooldown < 0){
			int count = 20;
			int pos;
			do {
				pos = Dungeon.level.randomDestination( this );
				if (count-- <= 0) {
					break;
				}
			} while (pos == -1 || Dungeon.level.secret[pos]);
			
			if (pos == -1) {
				//Do nothing			
			} else {
				
				this.sprite.interruptMotion();
				if (Dungeon.level.heroFOV[this.pos]) {
					CellEmitter.get(this.pos).start(Speck.factory(Speck.JET), 0.2f, 3);
				}

				this.move( pos, true );
				if (this.pos == pos) this.sprite.place( pos );

				if (Dungeon.level.heroFOV[pos]) {
					this.sprite.emitter().start(Speck.factory(Speck.JET), 0.2f, 3);
				}

				Dungeon.level.occupyCell( this );
				cooldown = Random.IntRange(4,6);
			}
		}
		return super.act();
	}

	@Override
	public int defenseSkill( Char enemy ) {
		return INFINITE_EVASION;
	}
	
	@Override
	protected Char chooseEnemy() {
		return null;
	}
	
	@Override
	public void damage( int dmg, Object src ) {
	}
	
	@Override
	public void add( Buff buff ) {
	}
	
	@Override
	public boolean reset() {
		return true;
	}
	
	@Override
	public float speed() {
		float speed = super.speed()*Random.Float(0.6f, 2.6f);
		return speed;
	}

	@Override
	public boolean interact(Char c) {
		sprite.turnTo( pos, c.pos );
		
		// Sample.INSTANCE.play( Assets.Sounds.Koishi );

		if (c != Dungeon.hero){
			return super.interact(c);
		}

		// if (Quest.given){
		// 	Quest.complete();
		// 	Dungeon.level.drop( new PotionOfStrength(), pos ).sprite.drop();
		// 	GLog.p(Messages.get(AyaNPC.class, "farewell"));
		// 	die( null );
		// } else {
		// 	tell(Messages.get(AyaNPC.class, "find_me"));
		// 	this.pos = Dungeon.level.randomRespawnCell( this );
		// 	Dungeon.level.occupyCell( this );
		// 	Quest.given = true;
		// }
		switch(Statistics.ayaArea){
			case 0:
				Dungeon.level.drop( new PotionOfLevitation(), pos ).sprite.drop();
				tell(Messages.get(AyaNPC.class, "dialog1"));
				break;
			case 1:
				Dungeon.level.drop( new ScrollOfMagicMapping(), pos ).sprite.drop();
				tell(Messages.get(AyaNPC.class, "dialog2"));
				break;
			case 2:
				Dungeon.level.drop( new PotionOfLevitation(), pos ).sprite.drop();
				tell(Messages.get(AyaNPC.class, "dialog3"));
				break;
			case 3:
				Dungeon.level.drop( new MeatPie(), pos ).sprite.drop();
				tell(Messages.get(AyaNPC.class, "dialog4"));
				break;
			case 4:
				Dungeon.level.drop( new PotionOfStrength(), pos ).sprite.drop();
				tell(Messages.get(AyaNPC.class, "dialog5"));
				break;
			//Should I put ascension dialog too? Would be fun
			default:
				GLog.w("Error, something went wrong in the code!\n");
				GLog.w(Integer.toString(Statistics.ayaArea));
				break;
		}

		Quest.complete();
		die( null );
		return true;
	}

	private void tell( String text ) {
		Game.runOnRenderThread(new Callback() {
			@Override
			public void call() {
				GameScene.show( new WndQuest( AyaNPC.this, text ));
			}
		});
	}

	public static class Quest {
		
		private static boolean spawned;
		private static boolean given;
		private static int depth;
		
		public static void reset() {
			spawned = false;
			given = false;
			// Statistics.ayaArea == 0;	//Check Statistic.java
		}
		
		private static final String NODE		= "chaseAya";
		
		private static final String SPAWNED		= "ayspawned";
		private static final String GIVEN		= "aygiven";
		private static final String DEPTH		= "aydepth";
		private static final String COOLDOWN	= "cooldown";
		
		public static void storeInBundle( Bundle bundle ) {
			
			Bundle node = new Bundle();
			
			node.put( SPAWNED, spawned );
			if (spawned) {
				node.put( GIVEN, given );
				node.put( DEPTH, depth );
			}
			
			bundle.put( NODE, node );
		}
		
		public static void restoreFromBundle( Bundle bundle ) {
			
			Bundle node = bundle.getBundle( NODE );

			if (!node.isNull() && (spawned = node.getBoolean( SPAWNED ))) {

				given	= node.getBoolean( GIVEN );
				depth	= node.getInt( DEPTH );
			} else {
				reset();
			}
		}
		
		public static void spawn( ForestLevel level ) {
			if (!spawned && Statistics.ayaArea == 0  && Random.Int( 4 - Dungeon.depth ) == 0) {
				
				AyaNPC aya = new AyaNPC();
				do {
					aya.pos = level.randomRespawnCell( aya );
				} while (aya.pos == -1);
				level.mobs.add( aya );
				
				spawned = true;
				given = false;
				depth = Dungeon.depth;
			}
		}

		public static void spawn( SDMLevel level ) {
			if (!spawned && Statistics.ayaArea == 1  && Random.Int( 9 - Dungeon.depth ) == 0) {
				
				AyaNPC aya = new AyaNPC();
				do {
					aya.pos = level.randomRespawnCell( aya );
				} while (aya.pos == -1);
				level.mobs.add( aya );
				
				spawned = true;
				given = false;
				depth = Dungeon.depth;
			}
		}

		public static void spawn( ShrineLevel level ) {
			if (!spawned && Statistics.ayaArea == 2  && Random.Int( 14 - Dungeon.depth ) == 0) {
				
				AyaNPC aya = new AyaNPC();
				do {
					aya.pos = level.randomRespawnCell( aya );
				} while (aya.pos == -1);
				level.mobs.add( aya );
				
				spawned = true;
				given = false;
				depth = Dungeon.depth;
			}
		}

		public static void spawn( BambooLevel level ) {
			if (!spawned && Statistics.ayaArea == 3  && Random.Int( 19 - Dungeon.depth ) == 0) {
				
				AyaNPC aya = new AyaNPC();
				do {
					aya.pos = level.randomRespawnCell( aya );
				} while (aya.pos == -1);
				level.mobs.add( aya );
				
				spawned = true;
				given = false;
				depth = Dungeon.depth;
			}
		}

		public static void spawn( HallsLevel level ) {
			if (!spawned && Statistics.ayaArea == 3  && Random.Int( 24 - Dungeon.depth ) == 0) {
				
				AyaNPC aya = new AyaNPC();
				do {
					aya.pos = level.randomRespawnCell( aya );
				} while (aya.pos == -1);
				level.mobs.add( aya );
				
				spawned = true;
				given = false;
				depth = Dungeon.depth;
			}
		}
		public static void complete() {
			if (spawned) {
				Notes.remove( Notes.Landmark.AYA );
				Statistics.questScores[Statistics.ayaArea] += 1000;
				spawned = false;
				Statistics.ayaArea++;
			}
		}
	}
}

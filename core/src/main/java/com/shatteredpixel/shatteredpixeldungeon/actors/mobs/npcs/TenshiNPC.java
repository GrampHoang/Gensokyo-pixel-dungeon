package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import java.util.ArrayList;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.UFOSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Succubus;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Eye;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Scorpio;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RipperDemon;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.encounters.TenshiEnc;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DemonCore;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DwarfToken;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.EndlessAlcohol;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.HisouBlade;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.CityLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.ForestLevel;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ImpSprite;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndImp;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class TenshiNPC extends NPC {

	{
		spriteClass = ImpSprite.class;

		properties.add(Property.IMMOVABLE);
	}
	
	public static boolean appeared = false;

	@Override
	protected boolean act() {
		if (!Quest.given && Dungeon.level.heroFOV[pos]) {
			Notes.add( Notes.Landmark.TENSHI );
		} 
		return super.act();
	}
	
	@Override
	public int defenseSkill( Char enemy ) {
		return INFINITE_EVASION;
	}
	
	@Override
	public void damage( int dmg, Object src ) {
	}
	
	@Override
	public void add( Buff buff ) {
	}
	
	public static boolean flawless = false; //Won't bundle this cuz you will claim reward immediatly, right?

	@Override
	public boolean interact(Char c) {
		
		sprite.turnTo( pos, Dungeon.hero.pos );

		if (c != Dungeon.hero){
			return true;
		}

		//Have quest
		if (Quest.given) {
			
			// Finished
			if (Quest.completed){
				sprite.showStatus(CharSprite.POSITIVE, "Have fun~");
			// Finish now
			} else if (!Quest.completed) {
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						TenshiNPC.Quest.complete();
						//Normal finish
						if(!flawless){
							tell(Messages.get(TenshiNPC.class, "quest_3_normal"));
						// Good finish, give you the sword too
						} else {
							tell(Messages.get(TenshiNPC.class, "quest_3_good"));
							if (!Catalog.isSeen(TenshiEnc.class)) {
								Catalog.setSeen(TenshiEnc.class);
								TenshiEnc enc = new TenshiEnc();
								enc.doPickUp(Dungeon.hero, Dungeon.hero.pos);
							}
							// HisouBlade hb = new HisouBlade();
							// hb.doPickUp(Dungeon.hero, Dungeon.hero.pos);
						}
					}
				});
			// Not finish
			} else {
				tell(Messages.get(this, "quest_2"));
			}
			
		} else {
			if (Catalog.isSeen(TenshiEnc.class)) tell(Messages.get(this, "quest_1"));
			else tell(Messages.get(this, "quest_1_notimpress"));
			Quest.given = true;
			Quest.completed = false;
			Notes.add( Notes.Landmark.TENSHI );
		}

		return true;
	}
	
	private void tell( String text ) {
		Game.runOnRenderThread(new Callback() {
			@Override
			public void call() {
				GameScene.show( new WndQuest( TenshiNPC.this, text ));
			}
		});
	}
	
	
	public void flee() {
		yell( Messages.get(this, "cya", Dungeon.hero.name()) );
		destroy();
		sprite.die();
	}

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);
		bundle.put( "T_APPEARED", appeared );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);
		appeared = bundle.getBoolean("T_APPEARED");
	}

	public static class Quest {
		private static boolean spawned;
		private static boolean given;
		private static boolean completed;
		
		public static void reset() {
			spawned = false;
			given = false;
			completed = false;
		}
		
		private static final String NODE		= "tenshi_Quest";
		
		private static final String SPAWNED		= "t_spawned";
		private static final String GIVEN		= "t_given";
		private static final String COMPLETED	= "t_completed";
		// private static final String REWARD		= "reward";
		
		public static void storeInBundle( Bundle bundle ) {
			
			Bundle node = new Bundle();
			
			node.put( SPAWNED, spawned );
			
			if (spawned) {
				
				node.put( GIVEN, given );
				node.put( COMPLETED, completed );
				// node.put( REWARD, reward );
			}
			
			bundle.put( NODE, node );
		}
		
		public static void restoreFromBundle( Bundle bundle ) {

			Bundle node = bundle.getBundle( NODE );
			
			if (!node.isNull() && (spawned = node.getBoolean( SPAWNED ))) {
				
				given = node.getBoolean( GIVEN );
				completed = node.getBoolean( COMPLETED );
				// reward = (Ring)node.get( REWARD );
			}
		}
		
		public static void spawn( ForestLevel level ) {
			// TODO Dungeon.depth > 99 to disable her spawn
            // Will try to spawn her inside a library
			// Irrelevant for now, she spawn in her room
			// if (!spawned && Dungeon.depth == 20 && Random.Int( 4 - Dungeon.depth ) == 0) {
				
			// 	TenshiNPC npc = new TenshiNPC();
			// 	do {
			// 		npc.pos = level.randomRespawnCell( npc );
			// 	} while (
			// 			npc.pos == -1 ||
			// 			level.heaps.get( npc.pos ) != null ||
			// 			level.traps.get( npc.pos) != null ||
			// 			level.findMob( npc.pos ) != null ||
			// 			//Marisa doesn't move, so she cannot obstruct a passageway
			// 			!(level.passable[npc.pos + PathFinder.CIRCLE4[0]] && level.passable[npc.pos + PathFinder.CIRCLE4[2]]) ||
			// 			!(level.passable[npc.pos + PathFinder.CIRCLE4[1]] && level.passable[npc.pos + PathFinder.CIRCLE4[3]]));
			// 	level.mobs.add( npc );
			// 	spawned = true;
			// 	given = false;
			// }
		}
		
		public static void process( Mob mob ) {
			if (given && !completed && Dungeon.depth < 25) {
				// if ((mob instanceof Succubus) ||
				// 	(mob instanceof Eye) ||
				// 	(mob instanceof Scorpio)) {
				// 	Dungeon.level.drop( new DemonCore().quantity(3), mob.pos ).sprite.drop();
				// }
				if ((mob instanceof RipperDemon)) {
					Dungeon.level.drop( new DemonCore(), mob.pos ).sprite.drop();
				}
			}
		}
		
		public static void complete() {
			// reward = null;
			completed = true;

			Statistics.questScores[3] = 500;
			Notes.remove( Notes.Landmark.TENSHI );
		}
		
		public static boolean isCompleted() {
			return completed;
		}
	}
}

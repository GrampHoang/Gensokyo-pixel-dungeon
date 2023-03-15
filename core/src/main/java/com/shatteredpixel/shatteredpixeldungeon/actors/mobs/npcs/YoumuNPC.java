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
import com.shatteredpixel.shatteredpixeldungeon.items.encounters.YoumuEnc;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DemonCore;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DwarfToken;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfLullaby;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRetribution;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfPsionicBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.EndlessAlcohol;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
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

public class YoumuNPC extends NPC {

	{
		spriteClass = ImpSprite.class;

		properties.add(Property.IMMOVABLE);
	}

	@Override
	protected boolean act() {
		if (!Quest.given && Dungeon.level.heroFOV[pos]) {
			Notes.add( Notes.Landmark.YOUMU );
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

	@Override
	public boolean interact(Char c) {
		
		sprite.turnTo( pos, Dungeon.hero.pos );

		if (c != Dungeon.hero){
			return true;
		}
		//Have quest
		if (Quest.given) {
			DemonCore tokens = Dungeon.hero.belongings.getItem( DemonCore.class );
			int tokenNeed = (25 - Dungeon.depth)*2 + 4;
			// Finished
			if (Quest.completed){
				switch(Random.IntRange(1,4)){
					default:
					case 1:
						sprite.showStatus(CharSprite.POSITIVE, Messages.get(YoumuNPC.class, "quest_finished1"));
						break;
					case 2:
						sprite.showStatus(CharSprite.POSITIVE, Messages.get(YoumuNPC.class, "quest_finished2"));
						break;
					case 3:
						sprite.showStatus(CharSprite.POSITIVE, Messages.get(YoumuNPC.class, "quest_finished3"));
						break;
					case 4:
						sprite.showStatus(CharSprite.POSITIVE, Messages.get(YoumuNPC.class, "quest_finished4"));
						break;
				}
			// Finish now
			} else if (tokens != null && tokens.quantity() >= tokenNeed && !Quest.completed) {
				int tokensHave = tokens.quantity();
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						tokens.detachAll(Dungeon.hero.belongings.backpack);
						YoumuNPC.Quest.complete();
						//Normal finish
						if( tokensHave <= tokenNeed*2 ){
							// //Reward: 1 Lullaby, 1 Retribution, 2 PotHealing
							// ScrollOfLullaby lul = new ScrollOfLullaby();
							// if (!lul.quantity(1).collect()) Dungeon.level.drop(lul, Dungeon.hero.pos);
							// ScrollOfRetribution ret = new ScrollOfRetribution();
							// if (!ret.quantity(1).collect()) Dungeon.level.drop(ret, Dungeon.hero.pos);
							// PotionOfHealing poh = new PotionOfHealing();
							// if (!poh.quantity(2).collect()) Dungeon.level.drop(poh, Dungeon.hero.pos);

						// Good finish, double what she tell you to get
						} else {
							if (Catalog.isSeen(YoumuEnc.class)) tell(Messages.get(YoumuNPC.class, "quest_3_good"));
							else {
								tell(Messages.get(YoumuNPC.class, "quest_3_good_first"));
								// Catalog.setSeen(YoumuEnc.class);
								YoumuEnc enc = new YoumuEnc();
								// Dungeon.level.drop(enc, Dungeon.hero.pos );
								enc.doPickUp(Dungeon.hero, Dungeon.hero.pos);
							}
							//Reward: 1 Lullaby, 1 Retribution, 1 PsiBlast, 4 PotHealing and 1 SoU
							// ScrollOfLullaby lul = new ScrollOfLullaby();
							// if (!lul.quantity(2).collect()) Dungeon.level.drop(lul, Dungeon.hero.pos);
							// ScrollOfRetribution ret = new ScrollOfRetribution();
							// if (!ret.quantity(1).collect()) Dungeon.level.drop(ret, Dungeon.hero.pos);
							// ScrollOfPsionicBlast psi = new ScrollOfPsionicBlast();
							// if (!psi.quantity(1).collect()) Dungeon.level.drop(psi, Dungeon.hero.pos);
							// PotionOfHealing poh = new PotionOfHealing();
							// if (!poh.quantity(4).collect()) Dungeon.level.drop(poh, Dungeon.hero.pos);
							// ScrollOfUpgrade sou = new ScrollOfUpgrade();
							// if (!sou.quantity(1).collect()) Dungeon.level.drop(sou, Dungeon.hero.pos);
						}
					}
				});
			// Not finish
			} else {
				tell(Messages.get(YoumuNPC.class, "quest_2", tokenNeed));
			}
			
		} else {
			if (Catalog.isSeen(YoumuEnc.class)) tell(Messages.get(YoumuNPC.class, "quest_1"));
			else tell(Messages.get(YoumuNPC.class, "quest_1_first"));
			Quest.given = true;
			Quest.completed = false;
			Notes.add( Notes.Landmark.YOUMU );
		}

		return true;
	}
	
	private void tell( String text ) {
		Game.runOnRenderThread(new Callback() {
			@Override
			public void call() {
				GameScene.show( new WndQuest( YoumuNPC.this, text ));
			}
		});
	}
	
	
	public void flee() {
		yell( Messages.get(YoumuNPC.class, "cya", Dungeon.hero.name()) );
		destroy();
		sprite.die();
	}

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);
	}

	public static class Quest {
		private static boolean spawned;
		private static boolean given;
		private static boolean completed;
		
		// public static Ring reward;
		
		public static void reset() {
			spawned = false;
			given = false;
			completed = false;
			// reward = null;
		}
		
		private static final String NODE		= "mari_Quest";
		
		private static final String SPAWNED		= "m_spawned";
		private static final String GIVEN		= "m_given";
		private static final String COMPLETED	= "m_completed";
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
				
			// 	YoumuNPC npc = new YoumuNPC();
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
			Notes.remove( Notes.Landmark.MARISA );
		}
		
		public static boolean isCompleted() {
			return completed;
		}
	}
}

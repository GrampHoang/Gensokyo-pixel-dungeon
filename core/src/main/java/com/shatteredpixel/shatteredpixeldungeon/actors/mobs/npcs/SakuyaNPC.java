package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import java.util.ArrayList;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.UFOSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BossMercy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Succubus;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Eye;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Scorpio;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RipperDemon;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.encounters.SakuyaEnc;
import com.shatteredpixel.shatteredpixeldungeon.items.encounters.TenshiEnc;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MeatPie;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Peach;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DemonCore;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DwarfToken;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.TrashBag;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.EndlessAlcohol;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.HisouBlade;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.BambooLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.CityLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.ForestLevel;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TenshiSprite;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.TenshiBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.items.FireOath;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DwarfToken;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.EndlessAlcohol;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SakuyaSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class SakuyaNPC extends NPC {

	{
		spriteClass = SakuyaSprite.class;

		properties.add(Property.IMMOVABLE);
	}

	@Override
	protected boolean act() {
		if (!Quest.given && Dungeon.level.heroFOV[pos]) {
			Notes.add( Notes.Landmark.SAKUYA );
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
            //Trash bag count
			TrashBag tokens = Dungeon.hero.belongings.getItem( TrashBag.class );
			int tokenNeed = 12;
			// Finished
			if (Quest.completed){
				sprite.showStatus(CharSprite.POSITIVE, "Thanks");

			// Finish now
			} else if (tokens != null && tokens.quantity() >= tokenNeed && !Quest.completed) {
				int tokensHave = tokens.quantity();
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						tokens.detachAll(Dungeon.hero.belongings.backpack);
						//Normal finish
						if( tokensHave < tokenNeed*2 ){
                            //Give choice

						// Good finish, get all the trash bags
						} else {
							if (Catalog.isSeen(SakuyaEnc.class)) tell(Messages.get(SakuyaNPC.class, "quest_3_good"));
							else {
								tell(Messages.get(SakuyaNPC.class, "quest_3_good_first"));
								SakuyaEnc enc = new SakuyaEnc();
								enc.doPickUp(Dungeon.hero, Dungeon.hero.pos);
                            }
						}
						flee();
						SakuyaNPC.Quest.complete();
					}
				});
			// Not finish
			} else {
				tell(Messages.get(SakuyaNPC.class, "quest_2", tokenNeed));
			}
			
		} else {
			tell(Messages.get(SakuyaNPC.class, "quest_1"));
			Quest.given = true;
			Quest.completed = false;
			Notes.add( Notes.Landmark.SAKUYA );
		}

		return true;
	}
	
	private void tell( String text ) {
		Game.runOnRenderThread(new Callback() {
			@Override
			public void call() {
				GameScene.show( new WndQuest( SakuyaNPC.this, text ));
			}
		});
	}
	
	
	public void flee() {
		sprite.showStatus(CharSprite.POSITIVE, "Thanks");
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
		public static boolean spawned;
		public static boolean given;
		public static boolean completed;

		public static void reset() {
			spawned = false;
			// given = false;
			// completed = false;
			// impression = 3;
		}

		private static final String NODE		= "sakuya_quest";
		
		private static final String SPAWNED		= "sa_spawned";
		private static final String GIVEN		= "sa_given";
		private static final String COMPLETED	= "sa_completed";
		// private static final String REWARD		= "reward";
		
		public static void storeInBundle( Bundle bundle ) {
			
			Bundle node = new Bundle();
			
			node.put( SPAWNED, spawned );
			
			if (spawned) {
				
				node.put( GIVEN, given );
				node.put( COMPLETED, completed );
			}
			
			bundle.put( NODE, node );
		}
		
		public static void restoreFromBundle( Bundle bundle ) {

			Bundle node = bundle.getBundle( NODE );
			
			if (!node.isNull() && (spawned = node.getBoolean( SPAWNED ))) {
				
				given = node.getBoolean( GIVEN );
				completed = node.getBoolean( COMPLETED );
			}
		}
		
		public static void spawn( BambooLevel level ) {
			if (!spawned && Dungeon.depth > 5 && Random.Int( 9 - Dungeon.depth ) == 0) {
				
				SakuyaNPC npc = new SakuyaNPC();
				do {
					npc.pos = level.randomRespawnCell( npc );
				} while (
						npc.pos == -1 ||
						level.heaps.get( npc.pos ) != null ||
						level.traps.get( npc.pos) != null ||
						level.findMob( npc.pos ) != null ||
						//Marisa doesn't move, so she cannot obstruct a passageway
						!(level.passable[npc.pos + PathFinder.CIRCLE4[0]] && level.passable[npc.pos + PathFinder.CIRCLE4[2]]) ||
						!(level.passable[npc.pos + PathFinder.CIRCLE4[1]] && level.passable[npc.pos + PathFinder.CIRCLE4[3]]));
				level.mobs.add( npc );
				spawned = true;
				given = false;
			}
		}
		
		public static void complete() {
			completed = true;
			Statistics.questScores[3] = 500;
			Notes.remove( Notes.Landmark.SAKUYA );
		}
		
		public static boolean isCompleted() {
			return completed;
		}
	}


		
	public class WndSakuya extends Window {
		
		private static final int WIDTH      = 120;
		private static final int BTN_HEIGHT = 20;
		private static final int GAP        = 2;

		public WndSakuya( final SakuyaNPC sakuya) {
			
			super();
			
			IconTitle titlebar = new IconTitle();
			titlebar.icon(sakuya.sprite());
			titlebar.label( Messages.get(this, "title_reward") );
			titlebar.setRect( 0, 0, WIDTH, 0 );
			add( titlebar );
			
			RenderedTextBlock message = PixelScene.renderTextBlock( Messages.get(this, "pick_mess"), 6 );
			message.maxWidth(WIDTH);
			message.setPos(0, titlebar.bottom() + GAP);
			add( message );
			
			RedButton btnReward_knife = new RedButton( Messages.get(this, "knife") ) {
				@Override
				protected void onClick() {
					hide();

				}
				
			};
			btnReward_knife.setRect( 0, message.top() + message.height() + GAP, WIDTH, BTN_HEIGHT );
			add( btnReward_knife );

			RedButton btnReward_daggers = new RedButton( Messages.get(this, "daggers") ) {
				@Override
				protected void onClick() {
					hide();

				}
			};
			btnReward_daggers.setRect( 0, (int)btnReward_knife.bottom() + GAP, WIDTH, BTN_HEIGHT );
			add( btnReward_daggers );
			
            TimekeepersHourglass hourglass = Dungeon.hero.belongings.getItem( TimekeepersHourglass.class );
            if (hourglass != null){
                RedButton btnReward_hourglass = new RedButton( Messages.get(this, "hourglass") ) {
                    @Override
                    protected void onClick() {
                        hide();
    
                    }
                };
                btnReward_hourglass.setRect( 0, (int)btnReward_daggers.bottom() + GAP, WIDTH, BTN_HEIGHT );
                add( btnReward_hourglass );
            }
			resize( WIDTH, (int)btnReward_daggers.bottom() );
		}
	}

}

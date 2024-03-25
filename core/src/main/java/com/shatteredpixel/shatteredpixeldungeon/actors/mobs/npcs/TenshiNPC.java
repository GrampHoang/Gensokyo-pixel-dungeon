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

import java.util.ArrayList;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
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
import com.shatteredpixel.shatteredpixeldungeon.items.encounters.TenshiEnc;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MeatPie;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Peach;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DemonCore;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DwarfToken;
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
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class TenshiNPC extends NPC {

	{
		spriteClass = TenshiSprite.class;

		properties.add(Property.IMMOVABLE);
	}

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
	
	@Override
	public boolean interact(Char c) {
		
		sprite.turnTo( pos, Dungeon.hero.pos );

		if (c != Dungeon.hero){
			return true;
		}
		//TODO: FIX COMPLETE LOGIC
		// Not talk -> not given
		// Talked -> given
		// Talked then fight -> given completed? -> impression
		//Have quest
		if (Quest.given) {
			
			// Finished
			if (Quest.completed){
				switch(Quest.impression){
					default:
					case 1:
						sprite.showStatus(CharSprite.POSITIVE, "You sucks");
						break;
					case 2:
						sprite.showStatus(CharSprite.POSITIVE, "You good");
						break;
					case 3:
						sprite.showStatus(CharSprite.POSITIVE, "You strong!");
						break;
					
				}
				
			// Finish now
			} else if (!Quest.completed) {
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						TenshiNPC.Quest.complete();
						Peach peach = new Peach();
						FireOath fo = new FireOath();
						PotionOfHealing poh = new PotionOfHealing();
						switch(Quest.impression){
							default:
								tell(String.format("Illegal impression value %d", Quest.impression));
								break;
							case 1:
								// Being trash, die too soon, give you  3 peaches
								tell(Messages.get(TenshiNPC.class, "i_bad"));
								if (!peach.quantity(3).collect()) Dungeon.level.drop(peach, Dungeon.hero.pos);
								GLog.i( Messages.get(Dungeon.hero, "you_now_have", peach.name()) );
								break;
							case 2:
								// Being decent, win her normally, give you FireOath, 2 peach, 1 poh
								tell(Messages.get(TenshiNPC.class, "i_decent"));
								if (!peach.quantity(2).collect()) Dungeon.level.drop(peach, Dungeon.hero.pos);
								if (!fo.collect()) Dungeon.level.drop(fo, Dungeon.hero.pos);
								if (!poh.collect()) Dungeon.level.drop(poh, Dungeon.hero.pos);
								GLog.i( Messages.get(Dungeon.hero, "you_now_have", peach.name() + ", " + fo.name() + ", " + poh.name()));
								break;
							case 3:
							case 4:
								// Flawless fight, or survival long enough (very long), give you additional sword
								if (!Catalog.isSeen(TenshiEnc.class)) {
									if (Quest.impression == 3) tell(Messages.get(TenshiNPC.class, "i_good_first"));
									else tell(Messages.get(TenshiNPC.class, "i_good_first_survive"));
									TenshiEnc enc = new TenshiEnc();
									enc.doPickUp(Dungeon.hero, Dungeon.hero.pos);
								} else {
									if (Quest.impression == 3) tell(Messages.get(TenshiNPC.class, "i_good"));
									else tell(Messages.get(TenshiNPC.class, "i_good_survive"));
									if (!fo.collect()) Dungeon.level.drop(fo, Dungeon.hero.pos);
									if (!poh.collect()) Dungeon.level.drop(poh, Dungeon.hero.pos);
									HisouBlade hb = new HisouBlade();
									hb.identify();
									if (!hb.collect()) Dungeon.level.drop(hb, Dungeon.hero.pos);
									GLog.i( Messages.get(Dungeon.hero, "you_now_have", hb.name() + ", " + fo.name() + ", " + poh.name()));
								}
								break;
						}
						GLog.p("You got new items!");
						flee();
					}
				});
			// Not finish
			}
		} else {
			Quest.completed = false;
			Notes.add( Notes.Landmark.TENSHI );
			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					GameScene.show( new WndTenshi( TenshiNPC.this) );
				}
			});
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
		switch(Quest.impression){
			default:
			case 1:
				sprite.showStatus(CharSprite.POSITIVE, "You sucks");
				break;
			case 2:
				sprite.showStatus(CharSprite.POSITIVE, "You good");
				break;
			case 3:
				sprite.showStatus(CharSprite.POSITIVE, "You strong!");
				break;
			
		}
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
		public static int impression = 3;

		public static void reset() {
			spawned = false;
			// given = false;
			// completed = false;
			impression = 3;
		}
		
		public static void setImpression(int i){
			impression = i;
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
				node.put("IMPRESS", impression);
				// node.put( REWARD, reward );
			}
			
			bundle.put( NODE, node );
		}
		
		public static void restoreFromBundle( Bundle bundle ) {

			Bundle node = bundle.getBundle( NODE );
			
			if (!node.isNull() && (spawned = node.getBoolean( SPAWNED ))) {
				
				given = node.getBoolean( GIVEN );
				completed = node.getBoolean( COMPLETED );
				impression = node.getInt("IMPRESS");
				// reward = (Ring)node.get( REWARD );
			}
		}
		
		public static void spawn( BambooLevel level ) {
			if (!spawned && Dungeon.depth > 16 && Random.Int( 20 - Dungeon.depth ) == 0) {
				
				TenshiNPC npc = new TenshiNPC();
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
			Statistics.questScores[3] += 4000;
			Notes.remove( Notes.Landmark.TENSHI );
		}
		
		public static boolean isCompleted() {
			return completed;
		}
	}


		
	public class WndTenshi extends Window {
		
		private static final int WIDTH      = 120;
		private static final int BTN_HEIGHT = 20;
		private static final int GAP        = 2;

		public WndTenshi( final TenshiNPC tenshi) {
			
			super();
			
			IconTitle titlebar = new IconTitle();
			titlebar.icon(tenshi.sprite());
			titlebar.label( Messages.get(this, "duel") );
			titlebar.setRect( 0, 0, WIDTH, 0 );
			add( titlebar );
			
			RenderedTextBlock message = PixelScene.renderTextBlock( Messages.get(this, "message"), 6 );
			message.maxWidth(WIDTH);
			message.setPos(0, titlebar.bottom() + GAP);
			add( message );
			
			RedButton btnReward = new RedButton( Messages.get(this, "fight") ) {
				@Override
				protected void onClick() {
					TenshiNPC.Quest.given = true;
					hide();
					Buff.affect(Dungeon.hero, BossMercy.class).set(Dungeon.hero);
					
					InterlevelScene.curTransition = new LevelTransition();
					InterlevelScene.mode = InterlevelScene.Mode.NEWTELEPORT;
					InterlevelScene.curTransition.destDepth = Dungeon.depth;
					InterlevelScene.curTransition.destBranch = 9;
					Game.switchScene(InterlevelScene.class);
					
				}
				
			};
			btnReward.setRect( 0, message.top() + message.height() + GAP, WIDTH, BTN_HEIGHT );
			add( btnReward );

			RedButton btnReward_special = new RedButton( Messages.get(this, "no") ) {
				@Override
				protected void onClick() {
					hide();
					GLog.w("You refused");
					TenshiNPC.Quest.given = false;
				}
			};
			btnReward_special.setRect( 0, (int)btnReward.bottom() + GAP, WIDTH, BTN_HEIGHT );
			add( btnReward_special );
			
			resize( WIDTH, (int)btnReward_special.bottom() );
		}
	}

}

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
import com.shatteredpixel.shatteredpixeldungeon.items.encounters.ReisenEnc;
import com.shatteredpixel.shatteredpixeldungeon.items.encounters.TenshiEnc;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.food.FrozenCarpaccio;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MeatPie;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Peach;
import com.shatteredpixel.shatteredpixeldungeon.items.food.SmallRation;
import com.shatteredpixel.shatteredpixeldungeon.items.food.StewedMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DemonCore;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DwarfToken;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.UnknownPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfLullaby;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.EndlessAlcohol;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.GluttonyFan;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.GluttonyYuyukoFan;
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
// import com.shatteredpixel.shatteredpixeldungeon.sprites.ReisenNPCSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ReisenSprite;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.ShrineLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.TenshiBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.UnknownPotRoom;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.items.FireOath;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
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

public class ReisenNPC extends NPC {

	{
		spriteClass = ReisenSprite.class;

		properties.add(Property.IMMOVABLE);
	}

	public int count = 10; //Count turn that give pot since reset

	@Override
	protected boolean act() {
		if (!Quest.given && Dungeon.level.heroFOV[pos]) {
			Notes.add( Notes.Landmark.REISEN );
		}
		count++;
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

		if(Quest.given == false){
			Quest.given = true;
			count = 0;
		}
		

		Game.runOnRenderThread(new Callback() {
			@Override
			public void call() {
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						GameScene.show( new WndReisen( ReisenNPC.this) );
					}
				});
			}
		});
		
		return true;
	}
	
	// private void tell( String text ) {
	// 	Game.runOnRenderThread(new Callback() {
	// 		@Override
	// 		public void call() {
	// 			GameScene.show( new WndQuest( ReisenNPC.this, text ));
	// 		}
	// 	});
	// }
	
	
	public void flee() {
		destroy();
		sprite.die();
	}

	@Override
	public void storeInBundle( Bundle bundle ) {
		bundle.put( "COUNT", count ); 
		super.storeInBundle(bundle);
		
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		count = bundle.getInt("COUNT");
		super.restoreFromBundle(bundle);
	}

	public static class Quest {
		public static boolean spawned;
		public static boolean given;
		public static boolean completed;
		public static int impression;

		public static void reset() {
			spawned = false;
			// given = false;
			// completed = false;
			// impression = 3;
		}
		
		public static void setImpression(int i){
			impression = i;
		}
		private static final String NODE		= "reisenQuest";
		
		private static final String SPAWNED		= "re_spawned";
		private static final String GIVEN		= "re_given";
		private static final String COMPLETED	= "re_completed";
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
		
		private static boolean questRoomSpawned;

		public static void spawn( BambooLevel level ) {
			if (questRoomSpawned) {
				questRoomSpawned = false;
				ReisenNPC npc = new ReisenNPC();
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
		
		public static ArrayList<Room> spawnRoom( ArrayList<Room> rooms) {
			questRoomSpawned = false;
			if (!spawned && Dungeon.depth > 15 && Random.Int( 20 - Dungeon.depth ) == 0) {
				questRoomSpawned = true;
				rooms.add(new UnknownPotRoom());
			}
			return rooms;
		}

		public static void complete() {
			completed = true;
			Statistics.questScores[3] += 4000;
			Notes.remove( Notes.Landmark.REISEN );
		}
		
		public static boolean isCompleted() {
			return completed;
		}
	}

	public class WndReisen extends Window {
		
		private static final int WIDTH      = 120;
		private static final int BTN_HEIGHT = 20;
		private static final int GAP        = 2;

		public WndReisen( final ReisenNPC reisen) {
			
			super();
			
			IconTitle titlebar = new IconTitle();
			titlebar.icon(reisen.sprite());
			titlebar.label( Messages.get(this, "title") );
			titlebar.setRect( 0, 0, WIDTH, 0 );
			add( titlebar );
			
		
			RenderedTextBlock message = PixelScene.renderTextBlock( Messages.get(this, "mess") , 6 );
			message.maxWidth(WIDTH);
			message.setPos(0, titlebar.bottom() + GAP);
			add( message );

			RedButton btnGive = new RedButton( Messages.get(this, "give_pot") ) {
				@Override
				protected void onClick() {
                    Item pot = Dungeon.hero.belongings.getItem( UnknownPotion.class);
					if (pot != null){
                        sprite.showStatus(CharSprite.POSITIVE, "Thanks");
						if(count < 10 && !Catalog.isSeen(ReisenEnc.class)){
							ReisenEnc enc = new ReisenEnc();
							enc.doPickUp(Dungeon.hero, Dungeon.hero.pos);
						}
                    } else {
                        sprite.showStatus(CharSprite.POSITIVE, "Where is it?");
                    }
					hide();
				}
				
			};
			btnGive.setRect( 0, message.top() + message.height() + GAP, WIDTH, BTN_HEIGHT );
			add( btnGive );

			RedButton btnNo = new RedButton( Messages.get(this, "no") ) {
				@Override
				protected void onClick() {
					hide();
				}
			};
			btnNo.setRect( 0, (int)btnGive.bottom() + GAP, WIDTH, BTN_HEIGHT );
			add( btnNo );
			
			resize( WIDTH, (int)btnNo.bottom() );
		}
	}

}

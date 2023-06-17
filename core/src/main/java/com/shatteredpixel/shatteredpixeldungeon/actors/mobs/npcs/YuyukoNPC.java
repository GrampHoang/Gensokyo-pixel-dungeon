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

public class YuyukoNPC extends NPC {

	{
		spriteClass = TenshiSprite.class;

		properties.add(Property.IMMOVABLE);
	}

	@Override
	protected boolean act() {
		if (!Quest.given && Dungeon.level.heroFOV[pos]) {
			Notes.add( Notes.Landmark.YUYUKO );
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
		if (Quest.given) {
			
			// Finished
			if (Quest.completed){
				switch(Quest.impression){
					default:
					case 1:
						sprite.showStatus(CharSprite.POSITIVE, "I'm hungry");
						break;
					case 2:
						sprite.showStatus(CharSprite.POSITIVE, "I'm still hungry");
						break;
					case 3:
						sprite.showStatus(CharSprite.POSITIVE, "I'm good");
						break;
                    case 4:
						sprite.showStatus(CharSprite.POSITIVE, "I'm full");
						break;
					
				}
			// Finish now
			} else if (!Quest.completed) {
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						Game.runOnRenderThread(new Callback() {
                            @Override
                            public void call() {
                                GameScene.show( new WndYuyuko( YuyukoNPC.this) );
                            }
                        });
					}
				});
			}
		} else {
			Quest.given = true;
			Quest.completed = false;
            Quest.setImpression(0);
			Notes.add( Notes.Landmark.YUYUKO );
		}

		return true;
	}
	
	private void tell( String text ) {
		Game.runOnRenderThread(new Callback() {
			@Override
			public void call() {
				GameScene.show( new WndQuest( YuyukoNPC.this, text ));
			}
		});
	}
	
	
	public void flee() {
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
				
				YuyukoNPC npc = new YuyukoNPC();
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
			Notes.remove( Notes.Landmark.YUYUKO );
		}
		
		public static boolean isCompleted() {
			return completed;
		}
	}

	public class WndYuyuko extends Window {
		
		private static final int WIDTH      = 120;
		private static final int BTN_HEIGHT = 20;
		private static final int GAP        = 2;

		public WndYuyuko( final YuyukoNPC tenshi) {
			
			super();
			
			IconTitle titlebar = new IconTitle();
			titlebar.icon(tenshi.sprite());
			titlebar.label( Messages.get(this, "title") );
			titlebar.setRect( 0, 0, WIDTH, 0 );
			add( titlebar );
			
			String hungString = "";
			String wantString = "";
			String rationName = "Small Ration";
			String rationName3= "Small Ration";

			Item ration = Dungeon.hero.belongings.getItem( SmallRation.class);
			if (ration != null){
				rationName = ration.name();
				if (ration.quantity() > 3) rationName3 = ration.name();
			}
			ration = Dungeon.hero.belongings.getItem( FrozenCarpaccio.class);
			if (ration != null){
				rationName = ration.name();
				if (ration.quantity() > 3) rationName3 = ration.name();
			}
			ration = Dungeon.hero.belongings.getItem( StewedMeat.class);
			if (ration != null){
				rationName = ration.name();
				if (ration.quantity() > 3) rationName3 = ration.name();
			}
			switch(Quest.impression){
				default:
                case 0: //Hungry, need any meat
					hungString = Messages.get(this, "hungry");
					wantString = Messages.get(this, "want_snack", rationName);
					break;
				case 1:
					hungString = Messages.get(this, "still_hungry");
					wantString = Messages.get(this, "want_ration", rationName3);
					break;
				case 2:
					hungString = Messages.get(this, "ok");
					wantString = Messages.get(this, "want_pie");
					break;
				case 3:
					hungString = Messages.get(this, "good");
					wantString = Messages.get(this, "want_3_pies");
					break;
            }

			RenderedTextBlock message = PixelScene.renderTextBlock( hungString, 6 );
			message.maxWidth(WIDTH);
			message.setPos(0, titlebar.bottom() + GAP);
			add( message );

			RedButton btnReward = new RedButton( wantString ) {
				@Override
				protected void onClick() {
					switch(Quest.impression){
						default:
						case 0: //Hungry, need any meat
							Item ration = Dungeon.hero.belongings.getItem( SmallRation.class);
							if (ration == null){
								ration = Dungeon.hero.belongings.getItem( FrozenCarpaccio.class );
								if (ration == null){
									ration = Dungeon.hero.belongings.getItem( StewedMeat.class );
								}
							}
							if (ration!=null){
								ration.detach(Dungeon.hero.belongings.backpack);
								sprite.showStatus(CharSprite.POSITIVE, "Chomp");
								ScrollOfLullaby sol = new ScrollOfLullaby();
								if (!sol.quantity(1).collect()) Dungeon.level.drop(sol, Dungeon.hero.pos);
								Quest.setImpression(1);
							} else {
								GLog.w(Messages.get(this, "no_food"));
							}
							break;
						case 1:
							Food bigration = Dungeon.hero.belongings.getItem( Food.class );
							if (bigration != null){
								bigration.detach(Dungeon.hero.belongings.backpack);
								sprite.showStatus(CharSprite.POSITIVE, "Om nom");
								StoneOfEnchantment soe = new StoneOfEnchantment();
								if (!soe.quantity(2).collect()) Dungeon.level.drop(soe, Dungeon.hero.pos);
								Quest.setImpression(2);
							} else {
								GLog.w(Messages.get(this, "no_food"));
							}
							break;
						case 2:
							MeatPie pie = Dungeon.hero.belongings.getItem( MeatPie.class );
							if (pie!=null){
								pie.detach(Dungeon.hero.belongings.backpack);
								sprite.showStatus(CharSprite.POSITIVE, "Om nom nom");
								GluttonyFan gfan = new GluttonyFan();
								gfan.identify();
								if (!gfan.collect()) Dungeon.level.drop(gfan, Dungeon.hero.pos);
								Quest.setImpression(3);
							} else {
								GLog.w(Messages.get(this, "no_food"));
							}
							break;
						case 3:
							MeatPie pie3 = Dungeon.hero.belongings.getItem( MeatPie.class );
							if (pie3 != null && pie3.quantity() >= 3){
								pie3.detach(Dungeon.hero.belongings.backpack);
								pie3.detach(Dungeon.hero.belongings.backpack);
								pie3.detach(Dungeon.hero.belongings.backpack);
								sprite.showStatus(CharSprite.POSITIVE, "*agressive eating noise*");
								GluttonyYuyukoFan gyyfan = new GluttonyYuyukoFan();
								gyyfan.identify();
								gyyfan.upgrade();
								gyyfan.upgrade();
								gyyfan.enchant(Weapon.Enchantment.random());
								if (!gyyfan.collect()) Dungeon.level.drop(gyyfan, Dungeon.hero.pos);
								Quest.setImpression(4);
								Quest.complete();
								flee();
							} else {
								GLog.w(Messages.get(this, "no_food"));
							}
							break;
					}
					hide();
				}
				
			};
			btnReward.setRect( 0, message.top() + message.height() + GAP, WIDTH, BTN_HEIGHT );
			add( btnReward );

			RedButton btnReward_special = new RedButton( Messages.get(this, "no") ) {
				@Override
				protected void onClick() {
					hide();
				}
			};
			btnReward_special.setRect( 0, (int)btnReward.bottom() + GAP, WIDTH, BTN_HEIGHT );
			add( btnReward_special );
			
			resize( WIDTH, (int)btnReward_special.bottom() );
		}
	}

}

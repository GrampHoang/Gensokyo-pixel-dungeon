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
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.encounters.SakuyaEnc;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.TrashBag;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.SakuyaKnife;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingKnife;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.SDMLevel;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
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
			int tokens = (Dungeon.hero.buff(SakuyaTrashSpawner.class) != null) ? Dungeon.hero.buff(SakuyaTrashSpawner.class).count : 0;
			// Finished
			if (Quest.completed){
				sprite.showStatus(CharSprite.POSITIVE, "Thanks");
			// Finish now good
			} else if (tokens  >= 30 && !Quest.completed) {
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						if (Catalog.isSeen(SakuyaEnc.class)) tell(Messages.get(SakuyaNPC.class, "quest_3_good"));
						else {
							tell(Messages.get(SakuyaNPC.class, "quest_3_good_first"));
							SakuyaEnc enc = new SakuyaEnc();
							enc.doPickUp(Dungeon.hero, Dungeon.hero.pos);
						}
						Game.runOnRenderThread(new Callback() {
							@Override
							public void call() {
								GameScene.show( new WndSakuya( SakuyaNPC.this, true) );
							}
						});
					}
				});
			// Finish now normal
			} else if (tokens  >= 12 && !Quest.completed) {
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						tell(Messages.get(SakuyaNPC.class, "quest_3_normal"));
						Game.runOnRenderThread(new Callback() {
							@Override
							public void call() {
								GameScene.show( new WndSakuya( SakuyaNPC.this, false) );
							}
						});
					}
				});
			// Not finish
			} else {
				tell(Messages.get(SakuyaNPC.class, "quest_2"));
			}
			
		} else {
			Buff.affect(Dungeon.hero, SakuyaTrashSpawner.class);
			tell(Messages.get(SakuyaNPC.class, "quest_1"));
			Quest.given = true;
			Quest.completed = false;
			Quest.spawnable = true;
			Quest.spawnTrash();
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
		public static boolean spawnable;
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
		public static void spawnTrash(){
			for(int i = 0; i < 5; i++){
				Dungeon.level.drop( new TrashBag(), Dungeon.level.randomRespawnCell( Dungeon.hero ) );
			}
		}

		public static void spawn( SDMLevel level ) {
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

		public WndSakuya( final SakuyaNPC sakuya, boolean excel) {
			
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
			
			RedButton btnReward_knife = new RedButton( excel ? Messages.get(this, "knife_excel") : Messages.get(this, "knife") ) {
				@Override
				protected void onClick() {
					SakuyaKnife sk = new SakuyaKnife();
					sk.identify();
					if (excel){
						sk.upgrade();
						sk.enchant(Weapon.Enchantment.random());
					}
					if (!sk.collect()) Dungeon.level.drop(sk, Dungeon.hero.pos);
					takeReward(SakuyaNPC.this);
				}
				
			};
			btnReward_knife.setRect( 0, message.top() + message.height() + GAP, WIDTH, BTN_HEIGHT );
			add( btnReward_knife );

			RedButton btnReward_daggers = new RedButton( Messages.get(this, "daggers") ) {
				@Override
				protected void onClick() {
					ThrowingKnife tk = new ThrowingKnife();
					if (excel)  if (!tk.quantity(4).collect()) Dungeon.level.drop(tk, Dungeon.hero.pos);
					else 		if (!tk.quantity(8).collect()) Dungeon.level.drop(tk, Dungeon.hero.pos);
					takeReward(SakuyaNPC.this);
					
				}
			};
			btnReward_daggers.setRect( 0, (int)btnReward_knife.bottom() + GAP, WIDTH, BTN_HEIGHT );
			add( btnReward_daggers );
			
            TimekeepersHourglass hourglass = Dungeon.hero.belongings.getItem( TimekeepersHourglass.class );
            if (hourglass != null && excel){
                RedButton btnReward_hourglass = new RedButton( Messages.get(this, "hourglass") ) {
                    @Override
                    protected void onClick() {
						while(hourglass.level() < 10){
							hourglass.upgrade();
						}
						takeReward(SakuyaNPC.this);
                    }
                };
                btnReward_hourglass.setRect( 0, (int)btnReward_daggers.bottom() + GAP, WIDTH, BTN_HEIGHT );
                add( btnReward_hourglass );
            }
			resize( WIDTH, (int)btnReward_daggers.bottom() );
		}

		private void takeReward( SakuyaNPC maid) {
			hide();
			maid.flee();
			SakuyaNPC.Quest.complete();
		}
	}

	public static class SakuyaTrashSpawner extends Buff {

		public int count = 0;
		{
			revivePersists = true;
		}

		public void pickUp() {
			count++;
			
			if (count <= 24) {	// + 5 starting trash, this should be enough
				GLog.p(Messages.get(SakuyaNPC.class,"quest_prog", count));
			} else {
				GLog.p(Messages.get(SakuyaNPC.class,"quest_prog_special", count));
			}
			if (count <= 30) {	// + 5 starting trash, this should be enough
				Dungeon.level.drop( new TrashBag(), Dungeon.level.randomRespawnCell( Dungeon.hero ) );
			}
			if (count == 12){
				GLog.p(Messages.get(SakuyaNPC.class,"quest_enough"));
				YoumuNPC.Quest.complete();
			}
		}

		@Override
		public boolean act() {
			spend(TICK);
			return true;
		}

		private static String COUNT = "CounT";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put( COUNT, count);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			count = bundle.getInt(COUNT);
		}
	}
}

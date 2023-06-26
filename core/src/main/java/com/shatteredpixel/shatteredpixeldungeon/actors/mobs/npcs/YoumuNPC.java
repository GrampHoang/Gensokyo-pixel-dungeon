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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wraith;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Peach;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.YoumuBlade1;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.YoumuBlade2;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.ShrineLevel;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ImpSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.YoumuNPCSprite;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou.Yuyuko;
import com.shatteredpixel.shatteredpixeldungeon.items.FireOath;
import com.shatteredpixel.shatteredpixeldungeon.items.encounters.YoumuEnc;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class YoumuNPC extends NPC {

	{
		spriteClass = YoumuNPCSprite.class;

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
			
			// Finished now 
			int count = Dungeon.hero.buff(YoumuGhostSpawner.class).killCount;
			boolean isSpawn = Dungeon.hero.buff(YoumuGhostSpawner.class).yuyuSpawn;
			boolean isKill  = Dungeon.hero.buff(YoumuGhostSpawner.class).yuyuKill;
			//Spawned Yuyuko
			if (count >= 10 && isSpawn){
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						YoumuNPC.Quest.complete();
						if(isKill){
							//Beat yuyuko
							if(Catalog.isSeen(YoumuEnc.class)) {
								tell(Messages.get(YoumuNPC.class, "quest_excel"));
							} else {
								YoumuEnc enc = new YoumuEnc();
								enc.doPickUp(Dungeon.hero, Dungeon.hero.pos);
								tell(Messages.get(YoumuNPC.class, "quest_excel_first"));
					 		}
							Dungeon.hero.buff(YoumuGhostSpawner.class).dispel();
							YoumuBlade2 blade = new YoumuBlade2();
							if (!blade.collect()) Dungeon.level.drop(blade, Dungeon.hero.pos);
							GLog.p(Messages.get(NPC.class,"newitems"));
							flee();
							//Hasn't beat her up yet
						} else{
							tell(Messages.get(YoumuNPC.class, "beatyuyuforme"));
						}
						
					}
				});
			// Not spawn Yuyuko/Finish Normal quest
			} else if (count >= 10 && !isSpawn){
				YoumuNPC.Quest.complete();
				Dungeon.hero.buff(YoumuGhostSpawner.class).dispel();
				tell(Messages.get(YoumuNPC.class, "quest"));
				YoumuBlade1 blade = new YoumuBlade1();
				if (!blade.collect()) Dungeon.level.drop(blade, Dungeon.hero.pos);
				GLog.p(Messages.get(NPC.class,"newitems"));
				flee();
			// Not finish
			} else {
				sprite.showStatus(CharSprite.POSITIVE, String.format("%s more", 10 - Dungeon.hero.buff(YoumuGhostSpawner.class).killCount));
			}
		} else {
			Quest.completed = false;
			Notes.add( Notes.Landmark.YOUMU );
			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					GameScene.show( new WndYoumu( YoumuNPC.this) );
				}
			});
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
		sprite.showStatus(CharSprite.POSITIVE, "Thank you!");
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
		}
		
		private static final String NODE		= "youmu_Quest";
		
		private static final String SPAWNED		= "y_spawned";
		private static final String GIVEN		= "y_given";
		private static final String COMPLETED	= "y_completed";

		public static void storeInBundle( Bundle bundle ) {
			
			Bundle node = new Bundle();
			
			node.put( SPAWNED, spawned );
			//This is the reason why debug doesn't work bro, only natural spawn affect this
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

		public static void process( Mob mob ) {
			if (given && !completed) {
				Dungeon.hero.buff(YoumuGhostSpawner.class).kill();
			}
		}
		

		public static void spawn( ShrineLevel level ) {
			if (!spawned && Dungeon.depth > 10 && Random.Int( 14 - Dungeon.depth ) == 0) {
				
				YoumuNPC npc = new YoumuNPC();
				do {
					npc.pos = level.randomRespawnCell( npc );
				} while (
						npc.pos == -1 ||
						level.heaps.get( npc.pos ) != null ||
						level.traps.get( npc.pos) != null ||
						level.findMob( npc.pos ) != null ||
						!(level.passable[npc.pos + PathFinder.CIRCLE4[0]] && level.passable[npc.pos + PathFinder.CIRCLE4[2]]) ||
						!(level.passable[npc.pos + PathFinder.CIRCLE4[1]] && level.passable[npc.pos + PathFinder.CIRCLE4[3]]));
				level.mobs.add( npc );
				spawned = true;
				given = false;
			}
		}

		public static void complete(){
			Statistics.questScores[3] = 500;
			Notes.remove( Notes.Landmark.YOUMU );
			Dungeon.level.unseal();
			completed = true;
		}
	}


		
	public class WndYoumu extends Window {
		
		private static final int WIDTH      = 120;
		private static final int BTN_HEIGHT = 20;
		private static final int GAP        = 2;

		public WndYoumu( final YoumuNPC youmu) {
			
			super();
			
			IconTitle titlebar = new IconTitle();
			titlebar.icon(youmu.sprite());
			titlebar.label( Messages.get(this, "hunt") );
			titlebar.setRect( 0, 0, WIDTH, 0 );
			add( titlebar );
			
			RenderedTextBlock message = PixelScene.renderTextBlock( Messages.get(this, "message"), 6 );
			message.maxWidth(WIDTH);
			message.setPos(0, titlebar.bottom() + GAP);
			add( message );
			
			RedButton btnReward = new RedButton( Messages.get(this, "help") ) {
				@Override
				protected void onClick() {
					Dungeon.level.seal();
					YoumuNPC.Quest.given = true;
					Buff.affect(Dungeon.hero, YoumuGhostSpawner.class);
					hide();
				}
				
			};
			btnReward.setRect( 0, message.top() + message.height() + GAP, WIDTH, BTN_HEIGHT );
			add( btnReward );

			RedButton btnReward_special = new RedButton( Messages.get(this, "no") ) {
				@Override
				protected void onClick() {
					hide();
					GLog.w("You refused");
					YoumuNPC.Quest.given = false;
				}
			};
			btnReward_special.setRect( 0, (int)btnReward.bottom() + GAP, WIDTH, BTN_HEIGHT );
			add( btnReward_special );
			
			resize( WIDTH, (int)btnReward_special.bottom() );
		}
	}

	public static class YoumuGhostSpawner extends Buff {

		public int killCount = 0;
		int spawnPower = 0;
		int spawnCount = 1;
		boolean yuyuSpawn = false;
		boolean yuyuKill  = false;
		{
			//not cleansed by reviving, but does check to ensure the dust is still present
			revivePersists = true;
		}

		public void yuyuKilled(){
			yuyuKill = true;
		}

		public void kill() {
			killCount++;
			if (killCount < 18) GLog.p(Messages.get(YoumuNPC.class,"quest_prog",killCount));
			else GLog.p(Messages.get(YoumuNPC.class,"quest_prog2",killCount));

			if (killCount == 10){
				GLog.p(Messages.get(YoumuNPC.class,"quest_enough"));
				// Dungeon.level.unseal();
				// YoumuNPC.Quest.complete(); //Should not complete yet
			}
		}

		@Override
		public boolean act() {
			spawnPower++;
			int wraiths = 1; //we include the wraith we're trying to spawn
			for (Mob mob : Dungeon.level.mobs){
				if (mob instanceof Wraith){
					wraiths++;
				}
			}

			int powerNeeded = Math.min(10, wraiths*3);	//Reduce this so that wraith spawn more

			if (powerNeeded <= spawnPower){
				spawnPower -= powerNeeded;
				int pos = 0;
				//FIXME this seems like old bad code (why not more checks at least?) but corpse dust may be balanced around it
				int tries = 20;
				do{
					pos = Random.Int(Dungeon.level.length());
					tries --;
				} while (tries > 0 && (!Dungeon.level.heroFOV[pos] || Dungeon.level.solid[pos] || Actor.findChar( pos ) != null));
				if (tries > 0) {
					Wraith.spawnAt(pos);
					spawnCount++;
					Sample.INSTANCE.play(Assets.Sounds.CURSED);
				}
			}

			if (killCount > 29 && !yuyuSpawn){
				//Spawn Yuyuko
				yuyuSpawn = true;
				Yuyuko yu = new Yuyuko();
				yu.pos = Dungeon.level.randomRespawnCell(yu);
				yu.state = yu.HUNTING;
				GLog.n(Messages.get(YoumuNPC.class,"spawn_yuyu"));
				Dungeon.level.seal();
				GameScene.add(yu);
			}
			spend(TICK);
			return true;
		}

		public void dispel(){
			detach();
			for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])){
				if (mob instanceof Wraith){
					mob.die(null);
				}
			}
		}

		private static String SPAWNPOWER = "spawnpower";
		private static String SPAWNCOUNT = "spawncount";
		private static String YUYUS = "yuyuspawn";
		private static String YUYUK = "yuyukilled";
		private static String KILLCOUNT = "killcount";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put( SPAWNPOWER, spawnPower );
			bundle.put( SPAWNCOUNT, spawnCount );
			bundle.put( YUYUS, yuyuSpawn);
			bundle.put( YUYUK, yuyuKill);
			bundle.put( KILLCOUNT, killCount);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			spawnPower = bundle.getInt( SPAWNPOWER );
			spawnCount = bundle.getInt( SPAWNCOUNT );
			yuyuSpawn = bundle.getBoolean(YUYUS);
			yuyuKill  = bundle.getBoolean(YUYUK);
			killCount = bundle.getInt(KILLCOUNT);
		}
	}
}

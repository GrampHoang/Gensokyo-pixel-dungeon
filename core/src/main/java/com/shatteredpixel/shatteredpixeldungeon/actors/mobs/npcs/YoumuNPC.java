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
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.ShrineLevel;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.YoumuSprite;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou.Yuyuko;
import com.shatteredpixel.shatteredpixeldungeon.items.FireOath;
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
		spriteClass = YoumuSprite.class;

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
			if (Quest.completed){
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						YoumuNPC.Quest.complete();
						Peach peach = new Peach();
						FireOath fo = new FireOath();
						PotionOfHealing poh = new PotionOfHealing();
						tell(String.format("Illegal impression value %d"));
						GLog.p("You got new items!");
						flee();
					}
				});
			// Not finish
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
		public static int count;
		
		public static void reset() {
			spawned = false;
			given = false;
			completed = false;
			count = 0;
		}
		
		private static final String NODE		= "youmu_Quest";
		
		private static final String SPAWNED		= "y_spawned";
		private static final String GIVEN		= "y_given";
		private static final String COMPLETED	= "y_completed";
		private static final String COUNT		= "y_count";

		public static void storeInBundle( Bundle bundle ) {
			
			Bundle node = new Bundle();
			
			node.put( SPAWNED, spawned );
			
			if (spawned) {
				node.put(COUNT, count);
				node.put( GIVEN, given );
				node.put( COMPLETED, completed );
			}
			
			bundle.put( NODE, node );
		}
		
		public static void restoreFromBundle( Bundle bundle ) {

			Bundle node = bundle.getBundle( NODE );
			
			if (!node.isNull() && (spawned = node.getBoolean( SPAWNED ))) {
				count = node.getInt( COUNT );
				given = node.getBoolean( GIVEN );
				completed = node.getBoolean( COMPLETED );
			}
		}
		
		public static void process( Mob mob ) {
			if (spawned && given && !completed) {
				count++;
			}
			GLog.p("Youmu quest: %s/10",count);
			if (count > 9){
				complete();
				GLog.p("You have kill enough Wraiths!");
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
		
		public static void complete() {
			Dungeon.level.unseal();
			completed = true;
			Statistics.questScores[3] = 500;
			Notes.remove( Notes.Landmark.YOUMU );
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

		int spawnPower = 0;
		int spawnCount = 1;
		{
			//not cleansed by reviving, but does check to ensure the dust is still present
			revivePersists = true;
		}

		@Override
		public boolean act() {
			if (target instanceof Hero && YoumuNPC.Quest.given){
				spawnPower = 0;
				spend(TICK);
				return true;
			}

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

			if (spawnCount > 29){
				//Spawn Yuyuko
				Yuyuko yu = new Yuyuko();
				yu.pos = Dungeon.level.randomRespawnCell(yu);
				GLog.n("Something dangerous just appeared!");
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

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put( SPAWNPOWER, spawnPower );
			bundle.put( SPAWNCOUNT, spawnCount );
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			spawnPower = bundle.getInt( SPAWNPOWER );
			spawnCount = bundle.getInt( SPAWNCOUNT );
		}
	}
}

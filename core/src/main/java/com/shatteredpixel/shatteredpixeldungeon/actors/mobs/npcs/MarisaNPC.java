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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RipperDemon;
import com.shatteredpixel.shatteredpixeldungeon.items.encounters.MarisaEnc;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DemonCore;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfLullaby;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRetribution;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfPsionicBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.ForestLevel;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MarisaBossSprite;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class MarisaNPC extends NPC {

	{
		spriteClass = MarisaBossSprite.class;

		properties.add(Property.IMMOVABLE);
	}

	@Override
	protected boolean act() {
		if (!Quest.given && Dungeon.level.heroFOV[pos]) {
			Notes.add( Notes.Landmark.MARISA );
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
	
	private ArrayList<Wand> wandls = new ArrayList<>();

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
						sprite.showStatus(CharSprite.POSITIVE, Messages.get(MarisaNPC.class, "quest_finished1"));
						break;
					case 2:
						sprite.showStatus(CharSprite.POSITIVE, Messages.get(MarisaNPC.class, "quest_finished2"));
						break;
					case 3:
						sprite.showStatus(CharSprite.POSITIVE, Messages.get(MarisaNPC.class, "quest_finished3"));
						break;
					case 4:
						sprite.showStatus(CharSprite.POSITIVE, Messages.get(MarisaNPC.class, "quest_finished4"));
						break;
				}
			// Finish now
			} else if (tokens != null && tokens.quantity() >= tokenNeed && !Quest.completed) {
				int tokensHave = tokens.quantity();
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						tokens.detachAll(Dungeon.hero.belongings.backpack);
						//Normal finish
						if( tokensHave <= tokenNeed*2 ){
							wandls = Dungeon.hero.belongings.getAllItems( Wand.class );
							boolean highwand = false;
							for (Wand wand : wandls) if (wand.trueLevel() > 12) {highwand = true; break;}
							//If no high level wand or seen before, no teach
							if (highwand != true || Catalog.isSeen(MarisaEnc.class)) tell(Messages.get(MarisaNPC.class, "quest_3_normal", tokensHave));
							// Teach if have >12 level wand
							else {
								tell(Messages.get(MarisaNPC.class, "quest_3_normal_wand", tokensHave));
								// Catalog.setSeen(MarisaEnc.class);
								MarisaEnc enc = new MarisaEnc();
								// Dungeon.level.drop(enc, Dungeon.hero.pos );
								enc.doPickUp(Dungeon.hero, Dungeon.hero.pos);
							}
							//Reward: 1 Lullaby, 1 Retribution, 2 PotHealing
							ScrollOfLullaby lul = new ScrollOfLullaby();
							if (!lul.quantity(1).collect()) Dungeon.level.drop(lul, Dungeon.hero.pos);
							ScrollOfRetribution ret = new ScrollOfRetribution();
							if (!ret.quantity(1).collect()) Dungeon.level.drop(ret, Dungeon.hero.pos);
							PotionOfHealing poh = new PotionOfHealing();
							if (!poh.quantity(2).collect()) Dungeon.level.drop(poh, Dungeon.hero.pos);
						// Good finish, double what she tell you to get
						} else {
							if (Catalog.isSeen(MarisaEnc.class)) tell(Messages.get(MarisaNPC.class, "quest_3_good"));
							else {
								tell(Messages.get(MarisaNPC.class, "quest_3_good_first"));
								// Catalog.setSeen(MarisaEnc.class);
								MarisaEnc enc = new MarisaEnc();
								// Dungeon.level.drop(enc, Dungeon.hero.pos );
								enc.doPickUp(Dungeon.hero, Dungeon.hero.pos);
							}
							//Reward: 1 Lullaby, 1 Retribution, 1 PsiBlast, 4 PotHealing and 1 SoU
							ScrollOfLullaby lul = new ScrollOfLullaby();
							if (!lul.quantity(2).collect()) Dungeon.level.drop(lul, Dungeon.hero.pos);
							ScrollOfRetribution ret = new ScrollOfRetribution();
							if (!ret.quantity(1).collect()) Dungeon.level.drop(ret, Dungeon.hero.pos);
							ScrollOfPsionicBlast psi = new ScrollOfPsionicBlast();
							if (!psi.quantity(1).collect()) Dungeon.level.drop(psi, Dungeon.hero.pos);
							PotionOfHealing poh = new PotionOfHealing();
							if (!poh.quantity(4).collect()) Dungeon.level.drop(poh, Dungeon.hero.pos);
							ScrollOfUpgrade sou = new ScrollOfUpgrade();
							if (!sou.quantity(1).collect()) Dungeon.level.drop(sou, Dungeon.hero.pos);
						}
						flee();
						MarisaNPC.Quest.complete();
					}
				});
			// Not finish
			} else {
				tell(Messages.get(MarisaNPC.class, "quest_2", tokenNeed));
			}
			
		} else {
			if (Catalog.isSeen(MarisaEnc.class)) tell(Messages.get(MarisaNPC.class, "quest_1"));
			else tell(Messages.get(MarisaNPC.class, "quest_1_first"));
			Quest.given = true;
			Quest.completed = false;
			Notes.add( Notes.Landmark.MARISA );
		}

		return true;
	}
	
	private void tell( String text ) {
		Game.runOnRenderThread(new Callback() {
			@Override
			public void call() {
				GameScene.show( new WndQuest( MarisaNPC.this, text ));
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
		private static boolean spawned;
		private static boolean given;
		private static boolean completed;
		
		public static boolean spawned(){
			return spawned;
		}

		public static void spawning(){
			spawned = true;
		}
		
		public static void reset() {
			spawned = false;
			// given = false;
			// completed = false;
		}
		
		private static final String NODE		= "mari_Quest";
		
		private static final String SPAWNED		= "m_spawned";
		private static final String GIVEN		= "m_given";
		private static final String COMPLETED	= "m_completed";
		
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
		
		public static void spawn( ForestLevel level ) {
			// Irrelevant for now, she spawn in her room
		}
		
		public static void process( Mob mob ) {
			if (given && !completed && Dungeon.depth < 25) {
				if ((mob instanceof RipperDemon)) {
					Dungeon.level.drop( new DemonCore(), mob.pos ).sprite.drop();
				}
			}
		}
		
		public static void complete() {
			completed = true;

			Statistics.questScores[4] += 4000;
			Notes.remove( Notes.Landmark.MARISA );
		}
		
		public static boolean isCompleted() {
			return completed;
		}
	}
}

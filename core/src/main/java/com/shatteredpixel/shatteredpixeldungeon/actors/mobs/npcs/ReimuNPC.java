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
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.SDMLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ImpSprite;
// import com.shatteredpixel.shatteredpixeldungeon.sprites.ReimuNPCSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class ReimuNPC extends NPC {

	{
		spriteClass = ImpSprite.class;
		properties.add(Property.IMMOVABLE);
	}

	@Override
	protected boolean act() {
		if (Dungeon.level.heroFOV[pos]){
			Notes.add( Notes.Landmark.REIMU );
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
		sprite.turnTo( pos, c.pos );
		
		// Sample.INSTANCE.play( Assets.Sounds.Koishi );

		if (c != Dungeon.hero){
			return super.interact(c);
		}

        switch(checkOtherQuest()){
            case 0:
                tell(Messages.get(ReimuNPC.class, "help_few"));
                // Do nothing
                break;
            case 1:
                tell(Messages.get(ReimuNPC.class, "hekp_some"));
                // Give small reward
                break;
            case 2:
                tell(Messages.get(ReimuNPC.class, "help_all"));
                // Give big reward, Enc and Unlock different final boss
                break;
            default:
                break;
        }
		if (Quest.given){
			Quest.complete();
			Dungeon.level.drop( new ScrollOfUpgrade(), pos ).sprite.drop();
			GLog.p(Messages.get(ReimuNPC.class, "farewell"));
			die( null );
		} else {
			tell(Messages.get(ReimuNPC.class, "find_me"));
			this.pos = Dungeon.level.randomRespawnCell( this );
			Dungeon.level.occupyCell( this );
			Quest.given = true;
		}
		return true;
	}

    private int checkOtherQuest(){
        // Check if player have helped other NPCs or not
        if (Statistics.questScores[0] <= 1000 && Statistics.questScores[1] <= 2000 &&
            Statistics.questScores[2] <= 3000 && Statistics.questScores[3] <= 4000){
            return 0;

        // Help some
        } else if ( Statistics.questScores[0] <= 2000 && Statistics.questScores[1] <= 4000 &&
                    Statistics.questScores[2] <= 6000 && Statistics.questScores[3] <= 8000){
            return 1;
        //help all
        } else {
           return 2;
        }
    }

	private void tell( String text ) {
		Game.runOnRenderThread(new Callback() {
			@Override
			public void call() {
				GameScene.show( new WndQuest( ReimuNPC.this, text ));
			}
		});
	}

	public static class Quest {
		
		private static boolean spawned;
		private static boolean given;
		private static int depth;
		
		public static void reset() {
			spawned = false;
		}
		
		private static final String NODE		= "sadKoishi";
		
		private static final String SPAWNED		= "kspawned";
		private static final String GIVEN		= "kgiven";
		private static final String DEPTH		= "kdepth";
		
		public static void storeInBundle( Bundle bundle ) {
			
			Bundle node = new Bundle();
			
			node.put( SPAWNED, spawned );
			
			if (spawned) {
				node.put( GIVEN, given );
				node.put( DEPTH, depth );
			}
			
			bundle.put( NODE, node );
		}
		
		public static void restoreFromBundle( Bundle bundle ) {
			
			Bundle node = bundle.getBundle( NODE );

			if (!node.isNull() && (spawned = node.getBoolean( SPAWNED ))) {

				given	= node.getBoolean( GIVEN );
				depth	= node.getInt( DEPTH );
			} else {
				reset();
			}
		}
		
		public static void spawn( SDMLevel level ) {
			if (!spawned && Dungeon.depth > 5 && Random.Int( 9 - Dungeon.depth ) == 0) {
				
				ReimuNPC Koishi = new ReimuNPC();
				do {
					Koishi.pos = level.randomRespawnCell( Koishi );
				} while (Koishi.pos == -1);
				level.mobs.add( Koishi );
				
				spawned = true;
				given = false;
				depth = Dungeon.depth;
			}
		}

		
		public static void complete() {
			if (spawned && given) {
				Notes.remove( Notes.Landmark.KOISHI );
				Statistics.questScores[1] += 1000;
			}
		}
	}
}

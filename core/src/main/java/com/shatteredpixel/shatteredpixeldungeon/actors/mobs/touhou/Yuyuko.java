/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2022 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.YoumuNPC;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.Chains;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.YuyukoSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Yuyuko extends Mob {

	{
		spriteClass = YuyukoSprite.class;

		HP = HT = 60;
		defenseSkill = 10;

		EXP = 0;
		maxLvl = 14;

		properties.add(Property.UNDEAD);
		
		HUNTING = new Hunting();
	}
    private static int LASER_LENGTH = 4;
	@Override
	public int damageRoll() {
		return Random.NormalIntRange(2, 6);
	}

    private boolean charging = false;
    private boolean direction = false;  //false = +, true = X, if Lunatic it would be * shape or horsemove shape
    private boolean skillUsed = false;

	private void charge(){
        int[] dirs;
		if(isLunatic()) dirs = direction ? PathFinder.NEIGHBOURS8 : PathFinder.NEIGHBOURS_HORSEMOVES;
        else            dirs = direction ? PathFinder.NEIGHBOURS4 : PathFinder.NEIGHBOURS4_CORNERS  ;
        for(int i : dirs){
            Ballistica rand = new Ballistica(this.pos, this.pos+i, Ballistica.STOP_SOLID);
            for (int p : rand.subPath(0, LASER_LENGTH)){
                sprite.parent.add(new TargetedCell(p, 0xA020F0));
            }
        }
	}

	private void release(){
		int[] dirs;
		if(isLunatic()) dirs = direction ? PathFinder.NEIGHBOURS8 : PathFinder.NEIGHBOURS_HORSEMOVES;
        else            dirs = direction ? PathFinder.NEIGHBOURS4 : PathFinder.NEIGHBOURS4_CORNERS  ;
        for(int i : dirs){
            Ballistica rand = new Ballistica(this.pos, this.pos+i, Ballistica.STOP_SOLID);
            int endpoint = 69;
            for (int p : rand.subPath(0, LASER_LENGTH)){
                Char ch = Actor.findChar(p);
                if(ch != null && ch.alignment != this.alignment){
                    ch.damage(8, this);
                }
                endpoint = p;
            }
            sprite.parent.add(new Beam.YoumuSlash(sprite.center(), DungeonTilemap.raisedTileCenterToWorld(endpoint)));
        }
        direction = !direction;
	}

	@Override
	public int attackSkill( Char target ) {
		return 12;
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 4);
	}

    @Override
    public void die( Object cause ) {
        Dungeon.level.unseal();
        YoumuNPC.Quest.complete();
        super.die( cause );
        //GameScene.bossSlain();
    }
    
	private final String CHARGING = "charging";
    private final String DIR = "direction";
    private final String USED = "skillUsed";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CHARGING, charging);
        bundle.put(DIR, direction);
        bundle.put(USED, skillUsed);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
        charging  = bundle.getBoolean(CHARGING);
		direction = bundle.getBoolean(DIR);
        skillUsed = bundle.getBoolean(USED);
	}

	private class Hunting extends Mob.Hunting{
		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			enemySeen = enemyInFOV;
            //We want her to shoot laser before she move, but start charging after she move so the targeted cell stay consistent
			if (charging){
                release();
                charging = false;
                skillUsed = true;
            }
			boolean act = super.act( enemyInFOV, justAlerted );
            if (!charging && enemyInFOV && !skillUsed) {
                charge();
                charging = true;
            }
            skillUsed = false;
            return act;
		}
	}
}

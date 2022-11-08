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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.*;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.*;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.SusGold;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TewiSprite;
import com.shatteredpixel.shatteredpixeldungeon.levels.CityLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public class Tewi extends Mob {
	
	{
		spriteClass = TewiSprite.class;
		
		HP = HT = 80;
		defenseSkill = 20;
		
		EXP = 10;
		maxLvl = 20;

		loot = Gold.class;
		lootChance = 0.2f;
		
		properties.add(Property.UNDEAD);
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 16, 22 );
	}

	@Override
	public int attackSkill( Char target ) {
		return 24;
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 4);
	}
	
	@Override
	public void die(Object cause) {
        for(int i : PathFinder.NEIGHBOURS8){
            if(Random.IntRange(0,2) == 1){
                //33% chance to drop fake gold
                Dungeon.level.drop( new SusGold(), pos+i ).sprite.drop();
                Char ch = Actor.findChar(this.pos + i);
                if(Random.IntRange(0,1) == 1){
                    //Then another lower chance to place trap under fake gold
                    // Hopefully won't break the game
                    Trap trap = ((Trap)Reflection.newInstance(Random.element(traps)));
                    Dungeon.level.setTrap(trap, this.pos+i);
                    Dungeon.level.map[trap.pos] = trap.visible ? Terrain.TRAP : Terrain.SECRET_TRAP;
                    trap.reveal(); 
                }
            }
        }
		super.die(cause);
	}

	protected float[] trapChances() {
        if (isLunatic()){
            return new float[]{
				1, 1, 1, 1, 1,
				2, 2, 0, 2,
                4, 0, 4, 4, 4, 4, 4, 4 };
        }
		return new float[]{
				4, 4, 4, 4, 4,
				2, 2, 0, 2,
				1, 0, 1, 1, 1, 1, 1, 1 };
	}
    
    protected Class[] traps = new Class[]{
        FrostTrap.class, StormTrap.class, CorrosionTrap.class, BlazingTrap.class, DisintegrationTrap.class,
        RockfallTrap.class, FlashingTrap.class, GuardianTrap.class, WeakeningTrap.class,
        DisarmingTrap.class, SummoningTrap.class, WarpingTrap.class, CursingTrap.class, PitfallTrap.class, DistortionTrap.class, GatewayTrap.class, GeyserTrap.class
    };
}

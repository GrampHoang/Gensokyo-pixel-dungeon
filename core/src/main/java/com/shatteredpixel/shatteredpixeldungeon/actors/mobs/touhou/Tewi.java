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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.*;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.SusGold;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TewiSprite;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

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
	
	// Messing around with Terrain flag lead nowhere so bear with me
	private boolean trap_able(int ter){
		if (ter == Terrain.EMPTY || ter == Terrain.EMPTY_DECO 
			|| ter == Terrain.GRASS || ter == Terrain.EMBERS || ter == Terrain.HIGH_GRASS 
			|| ter == Terrain.FURROWED_GRASS || ter == Terrain.WATER ||ter == Terrain.INACTIVE_TRAP){
				return true;
			}
		return false;
	}

	private boolean dropGold_able(int ter){
		if(trap_able(ter) || ter == Terrain.CHASM || ter == Terrain.EMPTY_WELL || ter == Terrain.OPEN_DOOR){
			return true;
		}
		return false;
	}
	@Override
	public void die(Object cause) {
        for(int i : PathFinder.NEIGHBOURS8){
			int ter = Dungeon.level.map[this.pos + i];
            if((Random.IntRange(0, 3) == 1) && dropGold_able(ter)){
                //25% chance to drop fake gold per tile
                Dungeon.level.drop( new SusGold(), pos+i ).sprite.drop();
                if((Random.IntRange(0,1) == 1 || isLunatic()) && trap_able(ter)){
                    // Hopefully won't break the game
                    Trap trap = ((Trap)Reflection.newInstance(Random.element(traps)));
                    Dungeon.level.setTrap(trap, this.pos+i);
					Dungeon.level.map[trap.pos] = Terrain.TRAP;
					trap.reveal(); 
                }
            }
        }
		super.die(cause);
	}

	protected float[] trapChances() {
        if (isLunatic()){
            return new float[]{
				0, 1, 1, 0, 0,
				2, 2, 0, 2,
                4, 0, 4, 4, 4, 4, 4, 4 };
        }
		return new float[]{
				0, 4, 4, 0, 0,
				4, 2, 0, 4,
				1, 0, 1, 1, 1, 1, 1, 1 };
	}
    
    protected Class[] traps = new Class[]{
        FrostTrap.class, StormTrap.class, CorrosionTrap.class, BlazingTrap.class, DisintegrationTrap.class,
        RockfallTrap.class, FlashingTrap.class, GuardianTrap.class, WeakeningTrap.class,
        DisarmingTrap.class, SummoningTrap.class, WarpingTrap.class, CursingTrap.class, PitfallTrap.class, DistortionTrap.class, GatewayTrap.class, GeyserTrap.class
    };
}

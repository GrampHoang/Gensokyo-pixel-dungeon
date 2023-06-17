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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ChenSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Chen extends Mob {
	{
		spriteClass = ChenSprite.class;
		HP = HT = 45;
		defenseSkill = 15;
		EXP = 8;
		maxLvl = 16;
		loot = Gold.class;
		lootChance = 0.5f;
	}

	private int ROLL_CD = 20;
	private int roll_cd = ROLL_CD;
	private boolean rolling = false;
	private int enemy_pos = this.pos; //just to be safe

	private static final String ROLL_COOLDOWN = "roll_cooldown";
	private static final String ROLLING = "rolling";
	private static final String ENEMY_POS = "enemy_pos";
	
	@Override	
	public int damageRoll() {
		if (Dungeon.level.water[pos]){
			return Random.NormalIntRange(6, 12);
		}
		return Random.NormalIntRange(12, 24);
	}

	@Override
	public int attackSkill(Char target) {
		if (Dungeon.level.water[pos]){
			return 16;
		}
		return 20;
	}
	
	@Override
	public int drRoll() {
		if (Dungeon.level.water[pos]){
			return Random.NormalIntRange(0, 6);
		}
		return Random.NormalIntRange(4, 12);
	}

    @Override
	protected boolean act() {
		if(isLunatic()){
			if(rolling == true){
				rolling = false;
				roll_cd --;
				spend(TICK);
				return roll(enemy_pos);
			} else if (roll_cd <= 1 && enemySeen && rolling == false){
				if(enemy != null){
					enemy_pos = ready(enemy.pos);	
				} else {
					enemy_pos = Dungeon.hero.pos;
				}
				spend(TICK);
				if (isLunatic() && Random.IntRange(1,3) == 2){
					roll_cd = 2;
				}else{
					roll_cd = ROLL_CD;
				}
				rolling = true;
				return true;
			
			} else if(Dungeon.level.water[this.pos]){
				roll_cd--;
				//Chance to reduce even further if on water
				if(Random.IntRange(0,1) == 1){
					roll_cd--;
				}
			}
			roll_cd--;
		}
		return super.act();
	}

	// @Override
	// public void die(Object cause) {
	// 	super.die(cause);
	// }

	private int ready(int target){
		((ChenSprite)sprite).spinning();
		// CellEmitter.center(this.pos).burst(RainbowParticle.BURST, 20);
        Ballistica b = new Ballistica(this.pos, target, Ballistica.STOP_SOLID);
		//Make sure she won't roll into pit
		while(Dungeon.level.pit[b.collisionPos] && b.collisionPos != this.pos){
			b.collisionPos = b.path.get(b.path.indexOf(b.collisionPos) - 1);
		}
        for (int p : b.subPath(0, Dungeon.level.distance(this.pos, b.collisionPos))){
            sprite.parent.add(new TargetedCell(p, 0xFF0000));
        }
        GLog.w("Chen is spining!");
		return b.collisionPos;
	}

	private boolean roll(int stopCell) {
		//push char
		Char cha = Actor.findChar(stopCell);
		int push_pos = this.pos;
		if (cha != null && cha != this){
			for (int i : PathFinder.NEIGHBOURS8){
				if (Actor.findChar(stopCell + i) == null && Dungeon.level.passable[stopCell + i]){
					push_pos = stopCell+i;
					break;
				}
			}
			Actor.addDelayed(new Pushing(cha, cha.pos, push_pos), 0);
			// ch.moveSprite(ch.pos, push_pos);
			cha.move(push_pos);
			Dungeon.level.occupyCell(cha);
		}
		//roll
        sprite.parent.add(new Beam.DeathRay(sprite.center(), DungeonTilemap.raisedTileCenterToWorld(stopCell)));
		Ballistica b = new Ballistica(this.pos, stopCell, Ballistica.STOP_SOLID);
		for (int p : b.subPath(0, Dungeon.level.distance(this.pos, stopCell))){
            Char ch = Actor.findChar(p);
			if (ch != null && !(ch instanceof Chen)){
				ch.damage(18, this);
			}
        }
		//move
		this.move( stopCell);
        this.moveSprite(this.pos, stopCell);
        return true;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( ROLL_COOLDOWN, roll_cd );
		bundle.put( ENEMY_POS, enemy_pos );
		bundle.put( ROLLING, rolling );
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		roll_cd = bundle.getInt( ROLL_COOLDOWN );
		enemy_pos = bundle.getInt( ENEMY_POS );
		rolling = bundle.getBoolean( ROLLING );
	}
}

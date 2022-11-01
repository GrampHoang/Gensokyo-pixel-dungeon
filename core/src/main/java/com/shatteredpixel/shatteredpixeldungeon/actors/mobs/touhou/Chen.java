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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.*;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.AliceSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Chen extends Mob {
	{
		spriteClass = AliceSprite.class;
		HP = HT = 45;
		defenseSkill = 15;
		EXP = 8;
		maxLvl = 16;
		loot = Gold.class;
		lootChance = 0.5f;
	}

	private int ROLL_CD = 20;
	private int roll_cd = ROLL_CD;

	private static final String ROLL_COOLDOWN = "roll_cooldown";


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
		return Random.NormalIntRange(0, 10);
	}

    // @Override
	// public float speed() {
	// 	return super.speed();
	// }

	// @Override
	// public int defenseProc( Char enemy, int damage ) {		
	// 	return super.defenseProc(enemy, damage);
	// }

    // @Override
	// protected boolean canAttack( Char enemy ) {
	// 	Ballistica attack = new Ballistica( pos, enemy.pos, Ballistica.WONT_STOP);
	// 	return !Dungeon.level.adjacent(pos, enemy.pos) && attack.collisionPos == enemy.pos;
	// }

	// @Override
	// public int attackProc(Char hero, int damage) {
	// 	damage = super.attackProc(enemy, damage);
	// 	if (hero instanceof Hero) {
	// 		Buff.prolong(enemy, Chill.class, 0.2f);
	// 		return damage;
	// 	}
	// 	return damage;
	// }

    @Override
	protected boolean act() {
		if(isLunatic()){
			if (roll_cd <= 2 && enemySeen){
				int enemy_pos = ready(enemy.pos);
				spend(TICK);
				if (isLunatic() && Random.IntRange(1,5) == 2){
					roll_cd = 1;
				}else{
					roll_cd = ROLL_CD;
				}
				return roll(enemy_pos);
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
		// CellEmitter.center(this.pos).burst(RainbowParticle.BURST, 20);
        Ballistica b = new Ballistica(this.pos, target, Ballistica.STOP_SOLID);
		while(Dungeon.level.pit[b.collisionPos]){
			b.collisionPos = b.path.get(b.path.indexOf(b.collisionPos) - 1);
		}
        for (int p : b.subPath(0, Dungeon.level.distance(this.pos, b.collisionPos))){
            sprite.parent.add(new TargetedCell(p, 0xFF0000));
        }
        GLog.w("Chen is spining!");
		return b.collisionPos;
	}

	private boolean roll(int stopCell) {
        sprite.parent.add(new Beam.DeathRay(sprite.center(), DungeonTilemap.raisedTileCenterToWorld(stopCell)));
        this.move( stopCell);
        this.moveSprite(this.pos, stopCell);
		Ballistica b = new Ballistica(this.pos, stopCell, Ballistica.STOP_SOLID);
		for (int p : b.subPath(0, Dungeon.level.distance(this.pos, stopCell))){
            Char ch = Actor.findChar(p);
			if (ch != null && !(ch instanceof Chen)){
				ch.damage(10, this);
				ch.move(b.path.get(Dungeon.level.distance(this.pos, stopCell) - 1));
				ch.sprite.move(this.pos, b.path.get(Dungeon.level.distance(this.pos, stopCell) - 1));
			}
        }
        return true;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( ROLL_COOLDOWN, roll_cd );
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		roll_cd = bundle.getInt( ROLL_COOLDOWN );
	}
}

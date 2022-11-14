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
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CirnoSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Cirno extends Mob {
	{
		spriteClass = CirnoSprite.class;
		HP = HT = 26;
		defenseSkill = 6;
		EXP = 3;
		maxLvl = 10;
        loot = PotionOfFrost.class;
		lootChance = 0.3f;

		
	}


	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 3, 6 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 12;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(2, 4);
	}

	@Override
	protected boolean getCloser( int target ) {
		if (state == HUNTING && (Dungeon.isChallenged(Challenges.LUNATIC))) {
			return enemySeen && getFurther( target );
		} else {
			return super.getCloser( target );
		}
	}

	@Override
	protected boolean canAttack( Char enemy ) {
		if (Dungeon.isChallenged(Challenges.LUNATIC)){
			Ballistica attack = new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE);
			return (!Dungeon.level.adjacent( pos, enemy.pos ) && attack.collisionPos == enemy.pos && Dungeon.level.distance(this.pos, enemy.pos) < 4);
		}
		return super.canAttack(enemy);
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		damage = super.attackProc(enemy, damage);
		if (Dungeon.level.distance(this.pos, enemy.pos) > 1){
			Buff.affect(enemy, Chill.class, 0.5f);
			damage = damage/2;
		}
		Buff.affect(enemy, Chill.class, 0.5f);
		return damage;
	}

    @Override
	protected boolean act() {
		return super.act();
	}

	@Override
	public void die(Object cause) {
        if (this.flying || !Dungeon.level.pit[this.pos]) {
            for (int i : PathFinder.NEIGHBOURS9) {
                if (!Dungeon.level.solid[this.pos + i]) {
                    GameScene.add(Blob.seed(this.pos + i, 2, Freezing.class));
                }
            }
        }
		super.die(cause);
	}
}

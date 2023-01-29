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
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RanSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Ran extends Mob {
	{
		spriteClass = RanSprite.class;
		HP = HT = 35;
		defenseSkill = 15;
		
		EXP = 8;
		maxLvl = 16;

        loot = Generator.Category.WAND;
		lootChance = 0.03f; //initially, see lootChance()
	}

	private boolean charging = false;

	@Override	
	public int damageRoll() {
		if (Dungeon.level.water[pos]){
			return Random.NormalIntRange(9, 18);
		}
		return Random.NormalIntRange(15, 24);
	}
	
	@Override
	public int attackSkill( Char target ) {
		if (Dungeon.level.water[pos]){
			return 16;
		}
		return 26;
	}
	
	@Override
	public int drRoll() {
		if (Dungeon.level.water[pos]){
			return Random.NormalIntRange(2, 10);
		}
		return Random.NormalIntRange(5, 15);
	}
	
	@Override
	protected boolean canAttack( Char enemy ) {
		if (new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos && charging == false){
			charging = true;
			this.sprite.add(CharSprite.State.CHARGING);
			return false;
		} else{
		return new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
		}
	}

	@Override
	public float lootChance() {
		//each drop makes future drops 1/3 as likely
		// so loot chance looks like: 1/33, 1/100, 1/300, 1/900, etc.
		return super.lootChance() * (float)Math.pow(1/3f, Dungeon.LimitedDrops.SHAMAN_WAND.count);
	}

	@Override
	public Item createLoot() {
		Dungeon.LimitedDrops.SHAMAN_WAND.count++;
		return super.createLoot();
	}

	protected boolean doAttack(Char enemy ) {
		charging = false;
		if(Dungeon.level.water[this.pos] == true && Random.IntRange(0, 1) == 1) Dungeon.level.map[this.pos] = Terrain.EMPTY;
		this.sprite.remove(CharSprite.State.CHARGING);
		CellEmitter.get(enemy.pos).burst(SmokeParticle.FACTORY, 4);
		Buff.prolong( enemy, Hex.class, 12f );
		Buff.prolong( enemy, Vulnerable.class, 12f );
		Dungeon.hero.sprite.parent.add(new Beam.Gust(this.sprite.center(), enemy.sprite.center()));
		// if (Dungeon.level.adjacent( pos, enemy.pos )) {
			
			// return super.doAttack( enemy );
			
		// } else {
			
		// 	if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
		// 		sprite.zap( enemy.pos );
		// 		return false;
		// 	} else {
		// 		zap();
		// 		return true;
		// 	}
		// }
		return super.doAttack( enemy );
	}
	
	@Override
	public void die(Object cause) {
        this.sprite.remove(CharSprite.State.CHARGING);
		super.die(cause);
	}

	//used so resistances can differentiate between melee and magical attacks
	public static class EarthenBolt{}
	
	private void zap() {
		spend( 1f );
		
		if (hit( this, enemy, true )) {
			
			if (Random.Int( 2 ) == 0) {
				Buff.prolong( enemy, Hex.class, Hex.DURATION );
				if(isLunatic()){
					Buff.prolong( enemy, Degrade.class, 12f );
			}
				if (enemy == Dungeon.hero) Sample.INSTANCE.play( Assets.Sounds.DEBUFF );
			}
			
			int dmg = Random.NormalIntRange( 16, 32 );
			if (Dungeon.level.water[pos]){
				dmg = Random.NormalIntRange( 8, 16 );
			}
			dmg = Math.round(dmg * AscensionChallenge.statModifier(this));
			enemy.damage( dmg, new EarthenBolt() );
			
			if (!enemy.isAlive() && enemy == Dungeon.hero) {
				Badges.validateDeathFromEnemyMagic();
				Dungeon.fail( getClass() );
				GLog.n( Messages.get(this, "bolt_kill") );
			}
		} else {
			enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
		}
	}
	
	
	public void onZapComplete() {
		zap();
		next();
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put("CHARGING", charging);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		charging = bundle.getBoolean("CHARGING");
	}

}

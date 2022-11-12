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
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hakutaku;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.*;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ChenSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.KeineSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.Camera;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Keine extends Mob {

	{
		spriteClass = KeineSprite.class;
		HP = HT = 60;
		defenseSkill = 20;
		
		EXP = 10;
		maxLvl = 20;

        loot = Generator.Category.SCROLL;
		lootChance = 1/7f;
	}

	public boolean hakutaku(){
		return this.buff(Hakutaku.class) != null;
	}

	@Override
	public float speed() {
		float quick = hakutaku() ? 1.3f : 1f;
		return super.speed() * quick;
	}

	@Override	
	public int damageRoll() {
		if (!hakutaku()){
			return Random.NormalIntRange(18, 23);
		}
		return Random.NormalIntRange(25, 30);
	}
	
	@Override
	public int attackSkill( Char target ) {
		if (!hakutaku()){
			return 22;
		}
		return 30;
	}
	
	@Override
	public int drRoll() {
		if (!hakutaku()){
			return Random.NormalIntRange(0, 5);
		}
		return Random.NormalIntRange(5, 10);
	}
	
	@Override
	protected boolean canAttack( Char enemy ) {
		if (!hakutaku()){
            return new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
        } else{
			return super.canAttack(enemy);
		}
	}

	protected boolean doAttack(Char enemy ) {
		if (Dungeon.level.adjacent( pos, enemy.pos ) || hakutaku()) {
	
			return super.doAttack( enemy );
			
		} else {
			
			if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
				sprite.zap( enemy.pos );
				return false;
			} else {
				zap();
				return true;
			}
		}
	}
	
	//used so resistances can differentiate between melee and magical attacks
	public static class KeineBolt{}
	
	private void zap() {
		spend( 1f );
		
		if (hit( this, enemy, true )) {
			//only 20%
			if (Random.Int( 5 ) == 0 || isLunatic()) {
				BArray.setFalse(Dungeon.level.visited);
			    BArray.setFalse(Dungeon.level.mapped);
				GameScene.updateFog(); //just in case hero wasn't moved
				Dungeon.observe();
                Buff.affect(this, Hakutaku.class, Hakutaku.DURATION);
                Camera.main.shake( 2f, 1f );
			}
			if (enemy == Dungeon.hero) {Sample.INSTANCE.play( Assets.Sounds.DEBUFF );}
			
			int dmg = Random.NormalIntRange( 20, 30 );
			dmg = Math.round(dmg * AscensionChallenge.statModifier(this));
			enemy.damage( dmg, new KeineBolt() );
			
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
	public void die(Object cause) {
        if (hakutaku()){
            this.sprite.remove(CharSprite.State.BURSTING_POWER);
        }
		super.die(cause);
	}

}

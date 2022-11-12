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
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;

import javax.swing.plaf.TreeUI;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.*;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.KaguyaSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Kaguya extends Mob {

	private boolean revived = false;

	{
		spriteClass = KaguyaSprite.class;
		HP = HT = 60;
		defenseSkill = 20;
		
		EXP = 10;
		maxLvl = 20;

        loot = Generator.Category.SCROLL;
		lootChance = 1/7f;
	}

	@Override	
	public int damageRoll() {
		return Random.NormalIntRange(25, 30);
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 30;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(5, 10);
	}
	
	@Override
	protected boolean canAttack( Char enemy ) {
        return new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
	}

	protected boolean doAttack(Char enemy ) {
		if (Dungeon.level.adjacent( pos, enemy.pos )) {
	
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
	public static class EirinBolt{}
	
	private void zap() {
		spend( 1f );
		
		if (hit( this, enemy, true )) {
			int effect = Random.Int(70);
			if (effect > 25) {
                Buff.prolong(enemy, Blindness.class, 3f);
                Buff.prolong(this, Blindness.class, 8f);
			//She will simply scare you with some sound effect lol. Will Tewi fit this better? Maybe
            //Can be written better
            }else if(effect > 20){
                Sample.INSTANCE.play( Assets.Sounds.DEBUFF );
				Sample.INSTANCE.play( Assets.Sounds.CURSED );
            }else if(effect > 15){
                Sample.INSTANCE.play( Assets.Sounds.SHATTER );
				Sample.INSTANCE.play( Assets.Sounds.GAS );
            }else if(effect > 10){
				GameScene.flash( (int)(0xFF*0.2f) << 16 );
                Sample.INSTANCE.play( Assets.Sounds.HEALTH_CRITICAL );
            }else if(effect > 5){
				GameScene.flash( (int)(0xFF*0.1f) << 16 );
                Sample.INSTANCE.play( Assets.Sounds.HEALTH_WARN );
            } else {
                for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
                    if (mob.alignment == this.alignment && mob instanceof Eirin) {
                        mob.beckon( this.pos );
                    }
                }
                GLog.w("EIIIRRIIIIIN");
                this.sprite.centerEmitter().start( Speck.factory( Speck.SCREAM ), 0.3f, 3 );
            }
			if (enemy == Dungeon.hero) {Sample.INSTANCE.play( Assets.Sounds.DEBUFF );}
			
			int dmg = Random.NormalIntRange( 20, 30 );
			dmg = Math.round(dmg * AscensionChallenge.statModifier(this));
			enemy.damage( dmg, new EirinBolt() );
			
			if (!enemy.isAlive() && enemy == Dungeon.hero) {
				Badges.validateDeathFromEnemyMagic();
				Dungeon.fail( getClass() );
				GLog.n( Messages.get(this, "bolt_kill") );
			}
		} else {
			enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
		}
	}
	
	@Override
	public void die(Object cause) {
		if (isLunatic() && !revived){
			Buff.affect(this, Paralysis.class, 5f);
			Buff.affect(this, Terror.class, 10f);
			((KaguyaSprite)this.sprite).crumple();
			this.HP = this.HT;
			revived = true;
		} else {
		super.die(cause);
		}
	}

	public void onZapComplete() {
		zap();
		next();
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
	}

}

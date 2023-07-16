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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.*;

import java.util.ArrayList;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave.BlastWave;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.KagerouSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class Kagerou extends Mob {
	
	{
		spriteClass = KagerouSprite.class;
		
		HP = HT = 70;
		defenseSkill = 30;
		
		EXP = 11;
		maxLvl = 21;
		
		loot = new Food();
		lootChance = 0.083f;
	}
	
	private int stack = 0;
	private int cooldown = Random.IntRange(3,6);
	private int skillState = 0;
	private int targetPos = -1;
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 12, 25 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 30;
	}
	
	@Override
	public float attackDelay() {
		return super.attackDelay();
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 2);
	}
	
	@Override
	protected boolean act() {
		switch(skillState){
			case 0:
			default:
			if (enemySeen == true) cooldown--;
				if (cooldown < 0){
					skillState = 1;
				}
				return super.act();
				// break;
			case 1:	// Stop and warn
				if (enemySeen == false){
					skillState = 0;
					return super.act();
				} else {
					KomachiBlessing.setRandom(this);
					skillState = Random.IntRange(2,3);
					warnAt(enemy.pos, skillState);
					spend(TICK);
					return true;
				}
				// break;
			case 2:	// howl
				howl();
				spend(TICK);
				skillState = 0;
				cooldown = Random.IntRange(6,9);
				if (isLunatic() && Random.IntRange(1,10) > 6) cooldown = 0;
				KomachiBlessing.tryDetach(this);
				return true;
				// break;
			case 3:	// dash
				dashTo(targetPos);
				spend(TICK);
				skillState = 0;
				cooldown = Random.IntRange(6,9);
				if (isLunatic() && Random.IntRange(1,10) > 4) cooldown = 0;
				KomachiBlessing.tryDetach(this);
				return true;
				// break;
			
				
		}
		// return super.act();
		// if (buff(Thirst.class) == null && skillState == HUNTING) {
		// 	stack = 0;
		// 	Buff.affect( this, Thirst.class );
		// }
		
		// return super.act();
	}
	
	// @Override
	// public void move( int step, boolean travelling) {
	// 	if (travelling && buff(Thirst.class) != null && enemySeen) {
	// 		stack++;
	// 		if (isLunatic()){
	// 			stack++;
	// 		}
	// 	}
	// 	super.move( step, travelling);
	// }
	
	// @Override
	// public float speed() {
	// 	return super.speed() * (float)Math.pow(1.05, stack);
	// }


	@Override
	public int attackProc(Char enemy, int damage) {
		damage = super.attackProc(enemy, damage);
		Buff.affect(enemy, Bleeding.class).set(damageRoll()/6);
		return damage;
	}

	private void warnAt(int warnPos, int skillType){
		if (skillType == 2){
			sprite.add(CharSprite.State.CHARGING);
		} else {
			sprite.add(CharSprite.State.BURSTING_POWER);
			Ballistica attack = new Ballistica( this.pos, warnPos, Ballistica.STOP_TARGET);
			for (int p : attack.subPath(0, Dungeon.level.distance(this.pos, attack.collisionPos))){
				sprite.parent.add(new TargetedCell(p, 0xFF0000));
			}
		}
		targetPos = warnPos;
		Dungeon.hero.interrupt();
	}

	private void dashTo(int toPos){
		sprite.remove(CharSprite.State.BURSTING_POWER);
		Ballistica attack = new Ballistica( this.pos, toPos, Ballistica.STOP_TARGET);
		PointF from = DungeonTilemap.tileCenterToWorld(this.pos);
		PointF to = DungeonTilemap.tileCenterToWorld(toPos);
		Dungeon.hero.sprite.parent.add(new Beam.Claw(from, to));
		from.x += 2;
		to.x += 2;
		Dungeon.hero.sprite.parent.add(new Beam.Claw(from, to));
		from.x -= 4;
		to.x -= 4;
		Dungeon.hero.sprite.parent.add(new Beam.Claw(from, to));
		for (int p : attack.subPath(0, Dungeon.level.distance(this.pos, attack.collisionPos))){
			sprite.parent.add(new TargetedCell(p, 0xFF0000));
		}
		BlastWave.blast(toPos);
		Char pushChar = Actor.findChar(toPos);
		if (pushChar != null){
			// WandOfBlastWave.throwChar(pushChar, attack, 1, false, true, Kagerou.class);
			if (attack.path.size() > attack.dist+1 && pushChar.pos == attack.collisionPos) {
				Ballistica trajectory = new Ballistica(pushChar.pos, attack.path.get(attack.dist + 1), Ballistica.MAGIC_BOLT);
				WandOfBlastWave.throwChar(pushChar, trajectory, 1, false, true, Kagerou.class);
			}
		}
		moveSprite(this.pos, toPos);
		move(toPos);
		Dungeon.level.occupyCell(Kagerou.this);
		Dungeon.hero.interrupt();
	}

	private void howl(){
		sprite.remove(CharSprite.State.CHARGING);
		this.sprite.centerEmitter().start( Speck.factory( Speck.SCREAM ), 0.3f, 3 );
		Sample.INSTANCE.play( Assets.Sounds.CHALLENGE );
		for (int i : PathFinder.NEIGHBOURS48){
			Char ch = Actor.findChar(i + this.pos);
			if (ch != null){
				if(ch instanceof Hero || ch.alignment == Alignment.ALLY){
					float DUR = Dungeon.level.distance(this.pos, ch.pos);
					DUR = 24f/(DUR*DUR);
					Buff.affect(ch, Weakness.class, DUR);
					Buff.affect(ch, Hex.class, DUR);
					Buff.affect(ch, Silence.class, DUR/2);
					Buff.affect(ch, Degrade.class, DUR/2);
					Buff.affect(ch, Blindness.class, DUR/2);
				}
			}
		}
	}

	

	private static String STACK = "stack";
	private static String COOLDOWN = "cooldown";
	private static String TARGET = "targetpos";
	private static String SKILLSTATE = "skillState";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( STACK, stack );
		bundle.put( COOLDOWN, cooldown );
		bundle.put( TARGET, targetPos );
		bundle.put( SKILLSTATE, stack );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		stack = bundle.getInt( STACK );
		cooldown = bundle.getInt( COOLDOWN );
		skillState = bundle.getInt(SKILLSTATE);
		target = bundle.getInt(TARGET);
	}
	
	public static class Thirst extends Buff {
		
		{
			type = buffType.POSITIVE;
			announced = true;
		}

		@Override
		public int icon() {	
			return BuffIndicator.AMOK;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(0f, 0f, 0f);
		}

		@Override
		public String toString() {
			return Messages.get(this, "name");
		}
		
		@Override
		public String desc() {
			return Messages.get(this, "desc");
		}
	}
}

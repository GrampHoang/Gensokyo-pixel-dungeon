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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.RainbowParticle;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class PatchouliBook extends WeaponWithSP {

	{
		image = ItemSpriteSheet.WOOD_STICK;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1.3f;

		tier = 3;
		DLY = 1;

		ACC = 99;
        RCH = 6;    //extra reach
		chargeGain = 10;
	}

	@Override
	public int max(int lvl) {
		return  6 +    //6 base,
				lvl*2; //+2 per levl instead of 3
	}
	@Override
	public boolean canReach( Char owner, int target){
		// if (Dungeon.level.distance( owner.pos, target ) > reachFactor(owner)){
		// 	return false;
		// } else {
		// 	boolean[] passable = BArray.not(Dungeon.level.solid, null);
		// 	for (Char ch : Actor.chars()) {
		// 		if (ch != owner) passable[ch.pos] = false;
		// 	}
			
		// 	PathFinder.buildDistanceMap(target, passable, reachFactor(owner));
			
		// 	return PathFinder.distance[owner.pos] <= reachFactor(owner);
		// }
		return true;
	}

    @Override
	public int proc(Char attacker, Char defender, int damage) {
        defender.damage(damage, this);
		Ballistica bolt = new Ballistica(attacker.pos, defender.pos, Ballistica.PROJECTILE);
		MagicMissile.boltFromChar( attacker.sprite.parent,
				MagicMissile.MAGIC_MISSILE,
				attacker.sprite,
				bolt.collisionPos,
				new Callback() {
					@Override
					public void call() {
						magicAttack(attacker, defender);
					}
				} );
		Sample.INSTANCE.play( Assets.Sounds.ZAP );
		damage = 0;
		return super.proc(attacker, defender, damage);
	}

	public void magicAttack(Char attacker, Char defender) {
		defender.damage(max(), curUser);
	}

    @Override
	protected boolean useSkill(){
		GameScene.selectCell(targeter);
        return true;
	}

	private CellSelector.Listener targeter = new CellSelector.Listener() {

		@Override
		public void onSelect(Integer cell) {
			if (cell != Dungeon.hero.pos){
				Ballistica aim = new Ballistica(Dungeon.hero.pos, cell, Ballistica.STOP_SOLID);
				for (int i : aim.subPath(1, Math.min(6, Dungeon.level.distance(Dungeon.hero.pos, aim.collisionPos) - 1))){
					Char ch = Actor.findChar(i);
					if (ch != null && !(ch instanceof Hero)){
						Buff.prolong( ch, Paralysis.class, 1);
						ch.damage(Random.Int(max()/3, max()), Dungeon.hero);
					}
					Sample.INSTANCE.play(Assets.Sounds.ROCKS);
					CellEmitter.get( i ).start( Speck.factory( Speck.ROCK ), 0.1f, 7 );
					CellEmitter.get( i ).start(RainbowParticle.BURST, 0.2f, 10);
				}
				Dungeon.hero.sprite.attack(cell);
			} else {
				for (int i : PathFinder.NEIGHBOURS8){
					Char ch = Actor.findChar(i + Dungeon.hero.pos);
					if (ch != null){
						Buff.prolong( ch, Paralysis.class, 1);
						ch.damage(Random.Int(max()/3, max()), Dungeon.hero);
					}
					Sample.INSTANCE.play(Assets.Sounds.ROCKS);
					CellEmitter.get( i + Dungeon.hero.pos ).start( Speck.factory( Speck.ROCK ), 0.1f, 7 );
					CellEmitter.get( i + Dungeon.hero.pos ).start(RainbowParticle.BURST, 0.2f, 10);
				}
				Dungeon.hero.sprite.attack(cell+1);
			}
		}

		@Override
		public String prompt() {
			return Messages.get(this, "prompt");
		}

	};
}

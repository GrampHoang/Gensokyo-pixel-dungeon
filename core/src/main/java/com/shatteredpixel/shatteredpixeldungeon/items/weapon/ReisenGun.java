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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Madden;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.RevealedArea;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.NaturesPower;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfFuror;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfSharpshooting;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class ReisenGun extends Weapon {
	
	public static final String AC_SHOOT		= "SHOOT";
	
	{
		image = ItemSpriteSheet.REISENGUN;
		
		defaultAction = AC_SHOOT;
		usesTargeting = true;
		
		unique = true;
		bones = false;
	}
	
	public boolean sniperSpecial = false;
	public float sniperSpecialBonusDamage = 0f;
	
	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove(AC_EQUIP);
		actions.add(AC_SHOOT);
		return actions;
	}
	
	@Override
	public void execute(Hero hero, String action) {
		
		super.execute(hero, action);
		
		if (action.equals(AC_SHOOT)) {
			
			curUser = hero;
			curItem = this;
			GameScene.selectCell( shooter );
			
		}
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		float proc_mul = 1f;
		int stack = 0;
		if (Dungeon.hero.subClass == HeroSubClass.MOONRABBIT){
			float multiplier = 1f;

			if (defender.buff(Madden.class) == null){
				Buff.prolong( defender, Madden.class, Madden.DURATION).madstack++;
				stack = 1;
			}
			else{
				Madden madden = defender.buff(Madden.class);
				Buff.prolong( defender, Madden.class, Madden.DURATION );
				multiplier = (float) (Math.pow(1.2f, madden.madstack));
				damage = Math.round(damage * multiplier);
				if (madden.madstack < 5){
					madden.madstack++;
					stack = madden.madstack;
				}
			}
		}
		proc_mul += 0.05f*stack;

		if (Dungeon.hero.hasTalent(Talent.HEADSHOT)){
			if(Random.Int(0,9) < Dungeon.hero.pointsInTalent(Talent.HEADSHOT)*proc_mul){
				Buff.prolong(defender, Paralysis.class, 1f);
			}
		}


		if (Dungeon.hero.hasTalent(Talent.INSANITY_INDUCE)){
			if(Random.Int(0,9) < Dungeon.hero.pointsInTalent(Talent.INSANITY_INDUCE)*proc_mul){
				Buff.affect(defender, Amok.class, 3f);
			}
		}
		if (Dungeon.hero.hasTalent(Talent.CHARM_GAZE)){
			if(Random.Int(0,9) < Dungeon.hero.pointsInTalent(Talent.CHARM_GAZE)*proc_mul){
				Buff.affect(defender, Charm.class, 3f);
			}
		}
		if (Dungeon.hero.hasTalent(Talent.LEG_SHOT)){
			if(Random.Int(0,99) < (1 + 2 * Dungeon.hero.pointsInTalent(Talent.LEG_SHOT))*stack*proc_mul){
				Buff.affect(defender, Cripple.class, 5f);
			}
		}
		if (Dungeon.hero.hasTalent(Talent.HEART_PIERCE)){
			if(Random.Int(0,99) < (0.5f + 0.5f*Dungeon.hero.pointsInTalent(Talent.HEART_PIERCE))*stack*proc_mul){
				if(defender.properties().contains(Char.Property.BOSS) || defender.properties().contains(Char.Property.MINIBOSS)){
					Buff.affect(defender, Bleeding.class).set(8f);
				}
				else{
					defender.HP = 1;
				}
			}
		}
		if (Dungeon.hero.hasTalent(Talent.ILLUSION_SEEKER)){
			float chance = 5 * stack;
			if(Random.Int(0,99) < chance && Dungeon.hero.pointsInTalent(Talent.ILLUSION_SEEKER) > 0){
				Buff.affect(defender, Burning.class).reignite(defender, 5f);
			}
			if(Random.Int(0,99) < chance && Dungeon.hero.pointsInTalent(Talent.ILLUSION_SEEKER) > 1){
				Buff.affect(defender, Poison.class).set(5);
			}
			if(Random.Int(0,99) < chance && Dungeon.hero.pointsInTalent(Talent.ILLUSION_SEEKER) > 2){
				Buff.affect(defender, Ooze.class).set(10f);
			}
		}

		return super.proc(attacker, defender, damage);
	}

	@Override
	public String info() {
		String info = desc();
		
		info += "\n\n" + Messages.get( ReisenGun.class, "stats",
				Math.round(augment.damageFactor(min())),
				Math.round(augment.damageFactor(max())),
				STRReq());
		
		if (STRReq() > Dungeon.hero.STR()) {
			info += " " + Messages.get(Weapon.class, "too_heavy");
		} else if (Dungeon.hero.STR() > STRReq()){
			info += " " + Messages.get(Weapon.class, "excess_str", Dungeon.hero.STR() - STRReq());
		}
		
		switch (augment) {
			case SPEED:
				info += "\n\n" + Messages.get(Weapon.class, "faster");
				break;
			case DAMAGE:
				info += "\n\n" + Messages.get(Weapon.class, "stronger");
				break;
			case NONE:
		}
		
		if (enchantment != null && (cursedKnown || !enchantment.curse())){
			info += "\n\n" + Messages.get(Weapon.class, "enchanted", enchantment.name());
			info += " " + Messages.get(enchantment, "desc");
		}
		
		if (cursed && isEquipped( Dungeon.hero )) {
			info += "\n\n" + Messages.get(Weapon.class, "cursed_worn");
		} else if (cursedKnown && cursed) {
			info += "\n\n" + Messages.get(Weapon.class, "cursed");
		} else if (!isIdentified() && cursedKnown){
			info += "\n\n" + Messages.get(Weapon.class, "not_cursed");
		}
		
		info += "\n\n" + Messages.get(MissileWeapon.class, "distance");
		
		return info;
	}
	
	@Override
	public int STRReq(int lvl) {
		return STRReq(1, lvl); //tier 1
	}
	
	@Override
	public int min(int lvl) {
		int dmg = 1 + Dungeon.hero.lvl/5
				+ RingOfSharpshooting.levelDamageBonus(Dungeon.hero)
				+ (curseInfusionBonus ? 1 + Dungeon.hero.lvl/30 : 0);
		return Math.max(0, dmg);
	}
	
	@Override
	public int max(int lvl) {
		int dmg = 6 + (int)(Dungeon.hero.lvl/2.5f)
				+ 2*RingOfSharpshooting.levelDamageBonus(Dungeon.hero)
				+ (curseInfusionBonus ? 2 + Dungeon.hero.lvl/15 : 0);
		return Math.max(0, dmg);
	}

	@Override
	public int targetingPos(Hero user, int dst) {
		return knockArrow().targetingPos(user, dst);
	}
	
	private int targetPos;
	
	@Override
	public int damageRoll(Char owner) {
		int damage = augment.damageFactor(super.damageRoll(owner));
		
		if (owner instanceof Hero) {
			int exStr = ((Hero)owner).STR() - STRReq();
			if (exStr > 0) {
				damage += Random.IntRange( 0, exStr );
			}
		}

		if (sniperSpecial){
			damage = Math.round(damage * (1f + sniperSpecialBonusDamage));

			switch (augment){
				case NONE:
					damage = Math.round(damage * 0.667f);
					break;
				case SPEED:
					damage = Math.round(damage * 0.5f);
					break;
				case DAMAGE:
					//as distance increases so does damage, capping at 3x:
					//1.20x|1.35x|1.52x|1.71x|1.92x|2.16x|2.43x|2.74x|3.00x
					int distance = Dungeon.level.distance(owner.pos, targetPos) - 1;
					float multiplier = Math.min(3f, 1.2f * (float)Math.pow(1.125f, distance));
					damage = Math.round(damage * multiplier);
					break;
			}
		}
		
		return damage;
	}
	
	@Override
	protected float baseDelay(Char owner) {
		if (sniperSpecial){
			switch (augment){
				case NONE: default:
					return 0f;
				case SPEED:
					return 1f * RingOfFuror.attackSpeedMultiplier(owner);
				case DAMAGE:
					return 2f * RingOfFuror.attackSpeedMultiplier(owner);
			}
		} else{
			return super.baseDelay(owner);
		}
	}

	@Override
	protected float speedMultiplier(Char owner) {
		float speed = super.speedMultiplier(owner);
		if (owner.buff(NaturesPower.naturesPowerTracker.class) != null){
			// +33% speed to +50% speed, depending on talent points
			speed += ((8 + ((Hero)owner).pointsInTalent(Talent.GROWING_POWER)) / 24f);
		}
		return speed;
	}

	@Override
	public int level() {
		int level = Dungeon.hero == null ? 0 : Dungeon.hero.lvl/5;
		if (curseInfusionBonus) level += 1 + level/6;
		return level;
	}

	@Override
	public int buffedLvl() {
		//level isn't affected by buffs/debuffs
		return level();
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	public Bullet knockArrow(){
		return new Bullet();
	}
	
	public class Bullet extends MissileWeapon {
		
		{
			image = ItemSpriteSheet.BULLET;

			hitSound = Assets.Sounds.ZAP;
		}

		@Override
		public Emitter emitter() {
			return super.emitter();
		}

		@Override
		public int damageRoll(Char owner) {
			return ReisenGun.this.damageRoll(owner);
		}
		
		@Override
		public boolean hasEnchant(Class<? extends Enchantment> type, Char owner) {
			return ReisenGun.this.hasEnchant(type, owner);
		}
		
		@Override
		public int proc(Char attacker, Char defender, int damage) {
			return ReisenGun.this.proc(attacker, defender, damage);
		}
		
		@Override
		public float delayFactor(Char user) {
			if (Dungeon.hero.pointsInTalent(Talent.QUICKDRAW) > Random.Int(0,9)){
				return 0;
			}
			return ReisenGun.this.delayFactor(user);
		}
		
		@Override
		public float accuracyFactor(Char owner) {
			if (sniperSpecial && ReisenGun.this.augment == Augment.DAMAGE){
				return Float.POSITIVE_INFINITY;
			} else {
				return super.accuracyFactor(owner);
			}
		}
		
		@Override
		public int STRReq(int lvl) {
			return ReisenGun.this.STRReq(lvl);
		}

		@Override
		protected void onThrow( int cell ) {
			Char enemy = Actor.findChar( cell );
			if (enemy == null || enemy == curUser) {
				parent = null;
				Splash.at( cell, 0xCC99FFFF, 1 );
			} else {
				if (!curUser.shoot( enemy, this )) {
					Splash.at(cell, 0xCC99FFFF, 1);
				}
				if (sniperSpecial && ReisenGun.this.augment != Augment.SPEED) sniperSpecial = false;
			}
		}

		@Override
		public void throwSound() {
			Sample.INSTANCE.play( Assets.Sounds.ATK_SPIRITBOW, 1, Random.Float(0.87f, 1.15f) );
		}

		int flurryCount = -1;
		
		@Override
		public void cast(final Hero user, final int dst) {
			final int cell = throwPos( user, dst );
			ReisenGun.this.targetPos = cell;
			if (sniperSpecial && ReisenGun.this.augment == Augment.SPEED){
				if (flurryCount == -1) flurryCount = 3;
				
				final Char enemy = Actor.findChar( cell );
				
				if (enemy == null){
					user.spendAndNext(castDelay(user, dst));
					sniperSpecial = false;
					flurryCount = -1;
					return;
				}
				QuickSlotButton.target(enemy);
				
				final boolean last = flurryCount == 1;
				
				user.busy();
				
				throwSound();
				
				((MissileSprite) user.sprite.parent.recycle(MissileSprite.class)).
						reset(user.sprite,
								cell,
								this,
								new Callback() {
									@Override
									public void call() {
										if (enemy.isAlive()) {
											curUser = user;
											onThrow(cell);
										}
										
										if (last) {
											user.spendAndNext(castDelay(user, dst));
											sniperSpecial = false;
											flurryCount = -1;
										}
									}
								});
				
				user.sprite.zap(cell, new Callback() {
					@Override
					public void call() {
						flurryCount--;
						if (flurryCount > 0){
							cast(user, dst);
						}
					}
				});
				
			} else {

				if (user.hasTalent(Talent.SCOUT_SHOT)
						&& user.buff(Talent.ScoutingShotCooldown.class) == null){
					int shotPos = throwPos(user, dst);
					if (Actor.findChar(shotPos) == null) {
						RevealedArea a = Buff.affect(user, RevealedArea.class, 5 * user.pointsInTalent(Talent.SCOUT_SHOT));
						a.depth = Dungeon.depth;
						a.pos = shotPos;
						Buff.affect(user, Talent.ScoutingShotCooldown.class, 20f);
					}
				}

				super.cast(user, dst);
			}
		}
	}
	
	private CellSelector.Listener shooter = new CellSelector.Listener() {
		@Override
		public void onSelect( Integer target ) {
			if (target != null) {
				knockArrow().cast(curUser, target);
			}
		}
		@Override
		public String prompt() {
			return Messages.get(ReisenGun.class, "prompt");
		}
	};
}

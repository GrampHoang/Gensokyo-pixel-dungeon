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

import java.io.IOException;
import java.util.ArrayList;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GoldenMimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Sheep;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.EnergyCrystal;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.UnknownPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.CursedWand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WoodStick;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.CursingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FlockTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ShockingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.SummoningTrap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ConfusionGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ParalyticGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Regrowth;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Degrade;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Levitation;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silence;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.FairySprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.TargetHealthIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Hina extends Mob {
	{	
		spriteClass = FairySprite.Red.class;
		HP = HT = 135;
		defenseSkill = 26;
		EXP = 14;
		maxLvl = 30;

		loot = Generator.Category.SCROLL;
		lootChance = 0.3f;
	}

	private int curseCount = 0;


	@Override
	public int damageRoll() {
		return Random.NormalIntRange(35, 45);
	}

	@Override
	public int attackSkill(Char target) {
		return 37;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(4, 18);
	}

	@Override
	public void die(Object cause) {
		super.die(cause);
	}

    @Override
	public int attackProc(Char enemy, int damage) {
		//Trigger curse efefect here
		if (enemy instanceof Hero){
			KindOfWeapon item = Dungeon.hero.belongings.weapon();
			Weapon weap = (Weapon) item;
			Armor armor = Dungeon.hero.belongings.armor();
			if(weap != null & Random.Int(10) < 14){
				if (weap.cursed){
					weap.enchant(null);
					curseCount++;
				} else {
					weap.enchant(Weapon.Enchantment.randomCurse());
					EquipableItem.equipCursed(Dungeon.hero);
				}
			}
			if (armor != null && Random.Int(10) < 14){
				if (armor.cursed){
					armor.inscribe(null);
					curseCount++;
				} else{
					armor.inscribe(Armor.Glyph.randomCurse());
					EquipableItem.equipCursed(Dungeon.hero);
				}
			}
			if (curseCount > 5){
				curseCount = 0;
				cursedEffect(this, enemy.pos);
			}
		}
		return super.attackProc(enemy, damage);
	}


	private static final String CURSE_COUNT     = "cursecount";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( CURSE_COUNT, curseCount );
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		curseCount = bundle.getInt( CURSE_COUNT );
	}


	private static float COMMON_CHANCE = 0.59f;
	private static float UNCOMMON_CHANCE = 0.3f;
	private static float RARE_CHANCE = 0.1f;
	private static float VERY_RARE_CHANCE = 0.01f;

	public static boolean cursedEffect(final Char user, final int targetPos){
		switch (Random.chances(new float[]{COMMON_CHANCE, UNCOMMON_CHANCE, RARE_CHANCE, VERY_RARE_CHANCE})){
			case 0: default:
				return commonEffect(user, targetPos);
			case 1:
				return uncommonEffect(user, targetPos);
			case 2:
				return rareEffect(user, targetPos);
			case 3:
				return veryRareEffect(user, targetPos);
		}
	}

	private static boolean commonEffect( final Char user, final int targetPos){
		switch(Random.Int(4)){

			//anti-entropy
			case 0: default:
				Char target = Actor.findChar(targetPos);
				if (Random.Int(2) == 0) {
					if (target != null) Buff.affect(target, Burning.class).reignite(target);
					Buff.affect(user, Frost.class, Frost.DURATION);
				} else {
					Buff.affect(user, Burning.class).reignite(user);
					if (target != null) Buff.affect(target, Frost.class, Frost.DURATION);
				}
				return true;

			//spawns some regrowth
			case 1:
				GameScene.add( Blob.seed(targetPos, 30, Regrowth.class));
				 
				return true;

			//random teleportation
			case 2:
				if(Random.Int(2) == 0) {
					if (user != null && !user.properties().contains(Char.Property.IMMOVABLE)) {
						ScrollOfTeleportation.teleportChar(user);
					} else {
						return cursedEffect(user, targetPos);
					}
				} else {
					Char ch = Actor.findChar( targetPos );
					if (ch != null && !ch.properties().contains(Char.Property.IMMOVABLE)) {
						ScrollOfTeleportation.teleportChar(ch);
					} else {
						return cursedEffect(user, targetPos);
					}
				}
				return true;

			//random gas at location
			case 3:
				Sample.INSTANCE.play( Assets.Sounds.GAS );
				 
				switch (Random.Int(3)) {
					case 0: default:
						GameScene.add( Blob.seed( targetPos, 800, ConfusionGas.class ) );
						return true;
					case 1:
						GameScene.add( Blob.seed( targetPos, 500, ToxicGas.class ) );
						return true;
					case 2:
						GameScene.add( Blob.seed( targetPos, 200, ParalyticGas.class ) );
						return true;
				}
		}

	}

	private static boolean uncommonEffect( final Char user, final int targetPos){
		switch(Random.Int(4)){

			//Random plant
			case 0: default:
				int pos = targetPos;

				if (Dungeon.level.map[pos] != Terrain.ALCHEMY
						&& !Dungeon.level.pit[pos]
						&& Dungeon.level.traps.get(pos) == null
						&& !Dungeon.isChallenged(Challenges.NO_HERBALISM)) {
					Dungeon.level.plant((Plant.Seed) Generator.randomUsingDefaults(Generator.Category.SEED), pos);
				} else {
					return cursedEffect(user, targetPos);
				}

				return true;

			//Bomb explosion
			case 1:
				new Bomb().explode(user.pos);
			//Bomb explosion
			case 2:
				new Bomb().explode(targetPos);
				return true;

			//shock and recharge
			case 3:
				new ShockingTrap().set( user.pos ).activate();
				Buff.prolong(user, Recharging.class, Recharging.DURATION);
				ScrollOfRecharging.charge(user);
				SpellSprite.show(user, SpellSprite.CHARGE);
				return true;
		}

	}

	private static boolean rareEffect( final Char user, final int targetPos){
		switch(Random.Int(4)){

			//sheep transformation
			case 0: default:

				Char ch = Actor.findChar( targetPos );
				if (ch != null && !(ch instanceof Hero)
						&& !ch.properties().contains(Char.Property.BOSS)
						&& !ch.properties().contains(Char.Property.MINIBOSS)){
					Sheep sheep = new Sheep();
					sheep.lifespan = 10;
					sheep.pos = ch.pos;
					ch.destroy();
					ch.sprite.killAndErase();
					Dungeon.level.mobs.remove(ch);
					TargetHealthIndicator.instance.target(null);
					GameScene.add(sheep);
					CellEmitter.get(sheep.pos).burst(Speck.factory(Speck.WOOL), 4);
					Sample.INSTANCE.play(Assets.Sounds.PUFF);
					Sample.INSTANCE.play(Assets.Sounds.SHEEP);
				} else {
					return cursedEffect(user, targetPos);
				}
				return true;

			//curses!
			case 1:
				Char cha = Actor.findChar( targetPos );
				if (cha instanceof Hero) {
					CursingTrap.curse( (Hero) cha );
				} else {
					return cursedEffect(user, targetPos);
				}
				return true;

			//inter-level teleportation
			case 2:
				ScrollOfTeleportation.teleportChar(user);
				new SummoningTrap().set( targetPos ).activate();
				return true;

			//summon monsters
			case 3:
				new SummoningTrap().set( targetPos ).activate();
				return true;
		}
	}

	private static boolean veryRareEffect( final Char user, final int targetPos){
		switch(Random.Int(4)){

			//great forest fire!
			case 0: default:
				for (int i = 0; i < Dungeon.level.length(); i++){
					GameScene.add( Blob.seed(i, 15, Regrowth.class));
				}
				do {
					//Nothing
				} while (Random.Int(5) != 0);
				new Flare(8, 32).color(0xFFFF66, true).show(user.sprite, 2f);
				Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
				GLog.p(Messages.get(CursedWand.class, "grass"));
				GLog.w(Messages.get(CursedWand.class, "fire"));
				return true;

			//golden mimic
			case 1:

				Char ch = Actor.findChar(targetPos);
				int spawnCell = targetPos;
				if (ch != null){
					ArrayList<Integer> candidates = new ArrayList<Integer>();
					for (int n : PathFinder.NEIGHBOURS8) {
						int cell = targetPos + n;
						if (Dungeon.level.passable[cell] && Actor.findChar( cell ) == null) {
							candidates.add( cell );
						}
					}
					if (!candidates.isEmpty()){
						spawnCell = Random.element(candidates);
					} else {
						return cursedEffect(user, targetPos);
					}
				}

				Mimic mimic = Mimic.spawnAt(spawnCell, new ArrayList<Item>(), GoldenMimic.class);
				mimic.stopHiding();
				mimic.alignment = Char.Alignment.ENEMY;
				Item reward;
				do {
					reward = new ScrollOfRemoveCurse();
				} while (reward.level() < 1);
				//play vfx/sfx manually as mimic isn't in the scene yet
				Sample.INSTANCE.play(Assets.Sounds.MIMIC, 1, 0.85f);
				CellEmitter.get(mimic.pos).burst(Speck.factory(Speck.STAR), 10);
				mimic.items.clear();
				mimic.items.add(reward);
				GameScene.add(mimic);
				return true;

			//crashes the game, yes, really.
			case 2:
				
				try {
					Dungeon.saveAll();
					if(Messages.lang() != Languages.ENGLISH){
						//Don't bother doing this joke to none-english speakers, I doubt it would translate.
						return cursedEffect(user, targetPos);
					} else {
						GameScene.show(
								new WndOptions(Icons.get(Icons.WARNING),
										"CURSED OVERFLOW",
										"Hina will visit you at 3am",
										"run",
										"exit the game",
										"no") {
									
									@Override
									protected void onSelect(int index) {
										Game.instance.finish();
									}
									
									@Override
									public void onBackPressed() {
										//do nothing
									}
								}
						);
						return false;
					}
				} catch(IOException e){
					ShatteredPixelDungeon.reportException(e);
					//maybe don't kill the game if the save failed.
					return cursedEffect(user, targetPos);
				}

			// HEavy debuff for everyone
			case 3:
				for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
					if (Dungeon.level.distance(mob.pos, user.pos) < 8) {
						Buff.affect(mob, Blindness.class, 50f);
						Buff.affect(mob, Cripple.class, 50f);
						Buff.affect(mob, Levitation.class, 50f);
						Buff.affect(mob, Vertigo.class, 50f);
					}
				}

				if (Dungeon.level.distance(Dungeon.hero.pos, user.pos) < 8) {
						Buff.affect(Dungeon.hero, Blindness.class, 50f);
						Buff.affect(Dungeon.hero, Cripple.class, 50f);
						Buff.affect(Dungeon.hero, Levitation.class, 50f);
						Buff.affect(Dungeon.hero, Vertigo.class, 50f);
					}
				return true;
		}
	}


}

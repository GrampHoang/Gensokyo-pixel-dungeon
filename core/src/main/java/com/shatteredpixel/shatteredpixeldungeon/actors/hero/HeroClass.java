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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.QuickSlot;
import com.shatteredpixel.shatteredpixeldungeon.UFOSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSight;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.NaturesPower;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.SpiritHawk;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.SpectralBlades;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.WildMagic;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.WarpBeacon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.ElementalBlast;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.DeathMark;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.ShadowClone;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.SmokeBomb;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.HeroicLeap;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.Shockwave;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.Endure;
import com.shatteredpixel.shatteredpixeldungeon.items.Amulet;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.KingsCrown;
import com.shatteredpixel.shatteredpixeldungeon.items.YinYang;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Waterskin;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClothArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.PlateArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.UnstableSpellbook;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.KoishiHat;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.VelvetPouch;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.*;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.*;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfFlock;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfDeepSleep;
import com.shatteredpixel.shatteredpixeldungeon.items.TengusMask;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.*;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.EndlessAlcohol;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Hakkero;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfHakkero;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.ReisenGun;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.ReisenHand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.RemiliaSpear;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dagger;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.DebugSword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gloves;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gohei;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Knife;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.KoiKnife;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MarisaStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.PatchouliBook;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WornShortsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.YoumuBlade3;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greataxe;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.OfudaHandheld;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingKnife;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingStone;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Unstable;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.DeviceCompat;

public enum HeroClass {

	WARRIOR( HeroSubClass.BERSERKER, HeroSubClass.GLADIATOR ),
	MAGE( HeroSubClass.BATTLEMAGE, HeroSubClass.WARLOCK ),
	ROGUE( HeroSubClass.ASSASSIN, HeroSubClass.FREERUNNER ),
	HUNTRESS( HeroSubClass.SNIPER, HeroSubClass.WARDEN ),
	REIMU(HeroSubClass.EXTERMINATOR, HeroSubClass.MAIDEN),
	MARISA( HeroSubClass.MAGICIAN, HeroSubClass.THIEF ),
	SAKUYA( HeroSubClass.HUNTER, HeroSubClass.MAID ),
	KOISHI( HeroSubClass.IMAGINARY, HeroSubClass.SATORI),
	REISEN( HeroSubClass.MOONRABBIT, HeroSubClass.REFUGEE );

	private HeroSubClass[] subClasses;

	HeroClass( HeroSubClass...subClasses ) {
		this.subClasses = subClasses;
	}

	public void initHero( Hero hero ) {

	// {
		// YoumuBlade3 we = new YoumuBlade3();
		// we.level(21);
		// we.collect();

		// hero.lvl = 99;
		// hero.HT = 99*5;
		

		// ScrollOfDebug sod = new ScrollOfDebug();
		// sod.quantity(69).collect();

		// Buff.affect(hero, MagicalSight.class, 9999f);
		// RingOfMight ring = new RingOfMight();
		// ring.level(99);
		// ring.doEquip(hero);

		// RingOfTenacity ring2 = new RingOfTenacity();
		// ring2.level(99);	
		// ring2.doEquip(hero);

		// UnstableSpellbook usb = new UnstableSpellbook();
		// usb.level(10);
		// usb.collect();

		// PatchouliBook pb = new PatchouliBook();
		// pb.level(21);
		// pb.collect();

		// PotionOfHealing.heal(hero);

		// PlateArmor plate = new PlateArmor();
		// plate.level(200);
		// plate.collect();

		// PotionOfHealing poe = new PotionOfHealing();
		// poe.quantity(30).collect();


		
	// 	TengusMask tengu = new TengusMask();
	// 	tengu.quantity(2).collect();
	// 	KingsCrown crown = new KingsCrown();
	// 	crown.quantity(2).collect();
		
	// 	PotionOfDivineInspiration podi = new PotionOfDivineInspiration();
	// 	podi.quantity(6).collect();

	// 	ScrollOfUpgrade sou = new ScrollOfUpgrade();
	// 	sou.quantity(15).collect();

	// 	Dungeon.energy = 69;

	// 	ScrollOfMagicMapping somm = new ScrollOfMagicMapping();
	// 	somm.quantity(10).collect();
	// 	StoneOfFlock sof = new StoneOfFlock();
	// 	sof.quantity(30).collect();

	// 	RingOfWealth row = new RingOfWealth();
	// 	row.upgrade(15).collect();
	// 	PotionOfLiquidFlame pol = new PotionOfLiquidFlame();
	// 	pol.quantity(30).collect();
	// 	PotionOfToxicGas pot = new PotionOfToxicGas();
	// 	pot.quantity(30).collect();
	// 	PotionOfParalyticGas popg = new PotionOfParalyticGas();
	// 	popg.quantity(30).collect();
	// 	PotionOfFrost pof = new PotionOfFrost();
	// 	pof.quantity(30).collect();
	// }
		
	
		hero.heroClass = this;
		Talent.initClassTalents(hero);

		Item i = new ClothArmor().identify();
		if (!Challenges.isItemBlocked(i)) hero.belongings.armor = (ClothArmor)i;

		i = new Food();
		if (!Challenges.isItemBlocked(i)) i.collect();

		new VelvetPouch().collect();
		Dungeon.LimitedDrops.VELVET_POUCH.drop();

		Waterskin waterskin = new Waterskin();
		waterskin.collect();

		new ScrollOfIdentify().identify();

		switch (this) {
			case WARRIOR:
				initWarrior( hero );
				break;

			case MAGE:
				initMage( hero );
				break;

			case ROGUE:
				initRogue( hero );
				break;

			case HUNTRESS:
				initHuntress( hero );
				break;
			
			case REIMU:
				initReimu( hero);
				break;

			case MARISA:
				initMarisa( hero );
				break;

			case SAKUYA:
				initSakuya( hero );
				break;

			case KOISHI:
				initKoishi( hero );
				break;

			case REISEN:
				initReisen( hero);
				break;
		}

		for (int s = 0; s < QuickSlot.SIZE; s++){
			if (Dungeon.quickslot.getItem(s) == null){
				Dungeon.quickslot.setSlot(s, waterskin);
				break;
			}
		}

		if(UFOSettings.red_HP()){
			hero.HT+=2;
			hero.HP+=2;
		}

		if(UFOSettings.red_Gold()){
			Dungeon.gold += 50;
		}

		if(UFOSettings.red_Item()){
			Dungeon.energy += 3;
		}

		if(UFOSettings.blue_HP()){
			hero.HT+=3;
			hero.HP+=3;
		}

		if (UFOSettings.blue_Gold()){
			Dungeon.gold += 50;
		}

		if (UFOSettings.blue_Item()){
			i = new Food();
			i.collect();
		}

		if (UFOSettings.green_Item()){
			i = new StoneOfDeepSleep();
			i.quantity(2).collect();
		}

		if (UFOSettings.green_Gold()){
			Dungeon.gold += 100;
		}
	}

	public Badges.Badge masteryBadge() {
		switch (this) {
			case WARRIOR:
				return Badges.Badge.MASTERY_WARRIOR;
			case MAGE:
				return Badges.Badge.MASTERY_MAGE;
			case ROGUE:
				return Badges.Badge.MASTERY_ROGUE;
			case HUNTRESS:
				return Badges.Badge.MASTERY_HUNTRESS;
			default:
		}
		return null;
	}

	private static void initWarrior( Hero hero ) {
		(hero.belongings.weapon = new WornShortsword()).identify();
		ThrowingStone stones = new ThrowingStone();
		stones.quantity(3).collect();
		Dungeon.quickslot.setSlot(0, stones);

		if (hero.belongings.armor != null){
			hero.belongings.armor.affixSeal(new BrokenSeal());
		}

		new PotionOfHealing().identify();
		new ScrollOfRage().identify();
	}

	private static void initMage( Hero hero ) {
		MagesStaff staff;

		staff = new MagesStaff(new WandOfMagicMissile());

		(hero.belongings.weapon = staff).identify();
		hero.belongings.weapon.activate(hero);

		Dungeon.quickslot.setSlot(0, staff);

		new ScrollOfUpgrade().identify();
		new PotionOfLiquidFlame().identify();
	}

	private static void initRogue( Hero hero ) {
		(hero.belongings.weapon = new Dagger()).identify();

		CloakOfShadows cloak = new CloakOfShadows();
		(hero.belongings.artifact = cloak).identify();
		hero.belongings.artifact.activate( hero );

		ThrowingKnife knives = new ThrowingKnife();
		knives.quantity(3).collect();

		Dungeon.quickslot.setSlot(0, cloak);
		Dungeon.quickslot.setSlot(1, knives);

		new ScrollOfMagicMapping().identify();
		new PotionOfInvisibility().identify();
	}

	private static void initHuntress( Hero hero ) {

		(hero.belongings.weapon = new Gloves()).identify();
		SpiritBow bow = new SpiritBow();
		bow.identify().collect();

		Dungeon.quickslot.setSlot(0, bow);

		new PotionOfMindVision().identify();
		new ScrollOfLullaby().identify();
	}

	private static void initReimu( Hero hero ) {
		
		Gohei wss = new Gohei();
		wss.identify();
		hero.belongings.weapon = wss;

		if (hero.belongings.weapon != null){
			((Weapon)hero.belongings.weapon).affixSeal(new YinYang());
		}

		OfudaHandheld ofu = new OfudaHandheld();
		ofu.quantity(10).collect();

		Dungeon.quickslot.setSlot(0, ofu);

		new PotionOfStrength().identify();
		new ScrollOfUpgrade().identify();
	}

	private static void initMarisa( Hero hero ) {
		
		Hakkero h = new Hakkero();
		h.identify().collect();
		

		MarisaStaff staff = new MarisaStaff(new WandOfHakkero());
		(hero.belongings.weapon = staff).identify();
		hero.belongings.weapon.activate(hero);

		Dungeon.quickslot.setSlot(0, staff);
		Dungeon.quickslot.setSlot(1, h);

		new ScrollOfRetribution().identify();
		new PotionOfLevitation().identify();
	}

	private static void initSakuya( Hero hero ) {

		Amulet amulet = new Amulet();
		amulet.collect();

		(hero.belongings.weapon = new RemiliaSpear()).identify();
		hero.belongings.weapon.upgrade(9);
		Dungeon.quickslot.setSlot(2, hero.belongings.weapon);

		TimekeepersHourglass hourglass = new TimekeepersHourglass();
		(hero.belongings.artifact = hourglass).identify();
		hero.belongings.artifact.activate( hero );

		ThrowingKnife knives = new ThrowingKnife();
		Unstable unstable = new Unstable();
		((Weapon)knives).enchant((Weapon.Enchantment)unstable);
		knives.upgrade();
		knives.quantity(3).collect();

		Dungeon.quickslot.setSlot(0, hourglass);
		Dungeon.quickslot.setSlot(1, knives);

		new ScrollOfTeleportation().identify();
		new PotionOfHaste().identify();

				// ScrollOfDebug sod = new ScrollOfDebug();
				// sod.quantity(69).collect();
				// // Dungeon.quickslot.setSlot(2, sod);
				
				// // DebugSword we = new DebugSword();
				// // we.level(1);
				// // hero.belongings.weapon = we;

				
				// Buff.affect(hero, MindVision.class, 9999f);
				// Buff.affect(hero, MagicalSight.class, 9999f);
				// RingOfMight ring = new RingOfMight();
				// ring.level(25);
				// ring.doEquip(hero);

				// RingOfTenacity ring2 = new RingOfTenacity();
				// ring2.level(99);	
				// ring2.doEquip(hero);

				// PotionOfHealing.heal(hero);

				// ReisenGun rg = new ReisenGun();
				// rg.collect();

				// PotionOfHealing poh = new PotionOfHealing();
				// poh.quantity(99).collect();
				// Dungeon.quickslot.setSlot(4, poh);

				// ScrollOfMagicMapping som = new ScrollOfMagicMapping();
				// som.quantity(99).collect();
				// Dungeon.quickslot.setSlot(5, som);

				// PotionOfExperience poe = new PotionOfExperience();
				// poe.quantity(13).collect();
				// Dungeon.quickslot.setSlot(3, poe);

				// EndlessAlcohol ea = new EndlessAlcohol();
				// ea.quantity(99).collect();
				// Dungeon.quickslot.setSlot(1, ea);
	}
	
	private static void initKoishi( Hero hero ) {
		(hero.belongings.weapon = new KoiKnife()).identify();

		KoishiHat hat = new KoishiHat();
		(hero.belongings.artifact = hat).identify();
		hero.belongings.artifact.activate( hero );

		new ScrollOfRetribution().identify();
		new PotionOfMindVision().identify();
	}

	private static void initReisen( Hero hero ) {
		(hero.belongings.weapon = new ReisenHand()).identify();
		ReisenGun gun = new ReisenGun();
		gun.identify().collect();

		Dungeon.quickslot.setSlot(0, gun);

		new ScrollOfRage().identify();
		new ScrollOfLullaby().identify();
	}

	public String title() {
		return Messages.get(HeroClass.class, name());
	}

	public String desc(){
		return Messages.get(HeroClass.class, name()+"_desc");
	}

	public HeroSubClass[] subClasses() {
		return subClasses;
	}

	public ArmorAbility[] armorAbilities(){
		switch (this) {
			case WARRIOR:
				return new ArmorAbility[]{new HeroicLeap(), new Shockwave(), new Endure()};
			case MAGE:
				return new ArmorAbility[]{new ElementalBlast(), new WildMagic(), new WarpBeacon()};
			case ROGUE:
				return new ArmorAbility[]{new SmokeBomb(), new DeathMark(), new ShadowClone()};
			case HUNTRESS:
				return new ArmorAbility[]{new SpectralBlades(), new NaturesPower(), new SpiritHawk()};
			case REIMU:
				return new ArmorAbility[]{new SmokeBomb(), new HeroicLeap(), new WarpBeacon()};
			case SAKUYA:
				return new ArmorAbility[]{new SmokeBomb(), new HeroicLeap(), new WarpBeacon()};
			case MARISA:
				return new ArmorAbility[]{new SmokeBomb(), new HeroicLeap(), new WarpBeacon()};
			case KOISHI:
				return new ArmorAbility[]{new SmokeBomb(), new HeroicLeap(), new WarpBeacon()};
			default:
				return new ArmorAbility[]{new SmokeBomb(), new HeroicLeap(), new WarpBeacon()};
		}
	}

	public String spritesheet() {
		switch (this) {
			case WARRIOR: default:
				return Assets.Sprites.WARRIOR;
			case MAGE:
				return Assets.Sprites.MAGE;
			case ROGUE:
				return Assets.Sprites.ROGUE;
			case HUNTRESS:
				return Assets.Sprites.HUNTRESS;
			case REIMU:
				return Assets.Sprites.REIMU;
			case SAKUYA:
				return Assets.Sprites.KOISHI;
			case MARISA:
				return Assets.Sprites.MARISA;
			case REISEN:
				return Assets.Sprites.REISEN;
			case KOISHI:
				return Assets.Sprites.KOISHI;
		}
	}

	public String splashArt(){
		switch (this) {
			case WARRIOR: default:
				return Assets.Splashes.WARRIOR;
			case MAGE:
				return Assets.Splashes.MAGE;
			case ROGUE:
				return Assets.Splashes.ROGUE;
			case HUNTRESS:
				return Assets.Splashes.HUNTRESS;
			case REIMU:
				return Assets.Splashes.REIMU;
			case SAKUYA:
				return Assets.Splashes.SAKUYA;
			case MARISA:
				return Assets.Splashes.MARISA;
			case REISEN:
				return Assets.Splashes.REISEN;
			case KOISHI:
				return Assets.Splashes.KOISHI;
		}
	}
	
	public String[] perks() {
		switch (this) {
			case WARRIOR: default:
				return new String[]{
						Messages.get(HeroClass.class, "warrior_perk1"),
						Messages.get(HeroClass.class, "warrior_perk2"),
						Messages.get(HeroClass.class, "warrior_perk3"),
						Messages.get(HeroClass.class, "warrior_perk4"),
						Messages.get(HeroClass.class, "warrior_perk5"),
				};
			case MAGE:
				return new String[]{
						Messages.get(HeroClass.class, "mage_perk1"),
						Messages.get(HeroClass.class, "mage_perk2"),
						Messages.get(HeroClass.class, "mage_perk3"),
						Messages.get(HeroClass.class, "mage_perk4"),
						Messages.get(HeroClass.class, "mage_perk5"),
				};
			case ROGUE:
				return new String[]{
						Messages.get(HeroClass.class, "rogue_perk1"),
						Messages.get(HeroClass.class, "rogue_perk2"),
						Messages.get(HeroClass.class, "rogue_perk3"),
						Messages.get(HeroClass.class, "rogue_perk4"),
						Messages.get(HeroClass.class, "rogue_perk5"),
				};
			case REIMU:
				return new String[]{
						Messages.get(HeroClass.class, "reimu_perk1"),
						Messages.get(HeroClass.class, "reimu_perk2"),
						Messages.get(HeroClass.class, "reimu_perk3"),
						Messages.get(HeroClass.class, "reimu_perk4"),
						Messages.get(HeroClass.class, "reimu_perk5"),
				};
			case HUNTRESS:
				return new String[]{
						Messages.get(HeroClass.class, "huntress_perk1"),
						Messages.get(HeroClass.class, "huntress_perk2"),
						Messages.get(HeroClass.class, "huntress_perk3"),
						Messages.get(HeroClass.class, "huntress_perk4"),
						Messages.get(HeroClass.class, "huntress_perk5"),
				};
			case SAKUYA:
				return new String[]{
						Messages.get(HeroClass.class, "sakuya_perk1"),
						Messages.get(HeroClass.class, "sakuya_perk2"),
						Messages.get(HeroClass.class, "sakuya_perk3"),
						Messages.get(HeroClass.class, "sakuya_perk4"),
						Messages.get(HeroClass.class, "sakuya_perk5"),
				};
			case MARISA:
				return new String[]{
						Messages.get(HeroClass.class, "marisa_perk1"),
						Messages.get(HeroClass.class, "marisa_perk2"),
						Messages.get(HeroClass.class, "marisa_perk3"),
						Messages.get(HeroClass.class, "marisa_perk4"),
						Messages.get(HeroClass.class, "marisa_perk5"),
				};
			case REISEN:
				return new String[]{
						Messages.get(HeroClass.class, "reisen_perk1"),
						Messages.get(HeroClass.class, "reisen_perk2"),
						Messages.get(HeroClass.class, "reisen_perk3"),
						Messages.get(HeroClass.class, "reisen_perk4"),
						Messages.get(HeroClass.class, "reisen_perk5"),
				};
		}
	}
	
	public boolean isUnlocked(){
		//always unlock on debug builds
		if (DeviceCompat.isDebug()) return true;
		
		switch (this){
			case WARRIOR: default:
				return true;
			case MAGE:
				return Badges.isUnlocked(Badges.Badge.UNLOCK_MAGE);
			case ROGUE:
				return Badges.isUnlocked(Badges.Badge.UNLOCK_ROGUE);
			case HUNTRESS:
				return Badges.isUnlocked(Badges.Badge.UNLOCK_HUNTRESS);
		}
	}
	
	public String unlockMsg() {
		switch (this){
			case WARRIOR: default:
				return "";
			case MAGE:
				return Messages.get(HeroClass.class, "mage_unlock");
			case ROGUE:
				return Messages.get(HeroClass.class, "rogue_unlock");
			case HUNTRESS:
				return Messages.get(HeroClass.class, "huntress_unlock");
		}
	}

}

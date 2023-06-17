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

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Enchanting;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PurpleParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Brimstone;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster;
import com.shatteredpixel.shatteredpixeldungeon.items.encounters.TenshiEnc;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.ReisenGun;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon.Enchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blazing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.HisouBlade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.HisouBladeAwakened;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class FireOath extends Item {
	
	private static final float TIME_TO_INSCRIBE = 2;
	
	private static final String AC_INSCRIBE = "INSCRIBE";
	private static final String AC_AWAKEN = "AWAKEN";
	{
		image = ItemSpriteSheet.STYLUS;
		
		stackable = true;

		defaultAction = AC_INSCRIBE;

		bones = true;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return new ItemSprite.Glowing( 0xff1111, 0.5f );
	}

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_INSCRIBE );

		if(Catalog.isSeen(TenshiEnc.class)){
			HisouBlade blade = Dungeon.hero.belongings.getItem( HisouBlade.class);
			if (blade != null)	actions.add( AC_AWAKEN );
		}
		return actions;
	}
	
	@Override
	public void execute( Hero hero, String action ) {

		super.execute( hero, action );

		if (action.equals(AC_INSCRIBE)) {
			curUser = hero;
			GameScene.selectItem( itemSelector );
			
		} else if (action.equals(AC_AWAKEN)) {
			curUser = hero;
			GameScene.selectItem( hisouSelector );
		}
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}
	
	private void inscribe( Armor armor ) {

		if (!armor.isIdentified() ){
			GLog.w( Messages.get(this, "identify"));
			return;
		} else if (armor.cursed || armor.hasCurseGlyph()){
			GLog.w( Messages.get(this, "cursed"));
			return;
		}
		
		detach(curUser.belongings.backpack);

		GLog.w( Messages.get(this, "inscribed"));
        Brimstone brim = new Brimstone();
		armor.inscribe((Armor.Glyph)brim);
		
		curUser.sprite.operate(curUser.pos);
		curUser.sprite.centerEmitter().start(PurpleParticle.BURST, 0.05f, 10);
		Enchanting.show(curUser, armor);
		Sample.INSTANCE.play(Assets.Sounds.BURNING);
		
		curUser.spend(TIME_TO_INSCRIBE);
		curUser.busy();
	}

    private void enchant( Weapon weapon ) {

		if (!weapon.isIdentified() ){
			GLog.w( Messages.get(this, "identify"));
			return;
		} else if (weapon.cursed || weapon.hasCurseEnchant()){
			GLog.w( Messages.get(this, "cursed"));
			return;
		}
		
		detach(curUser.belongings.backpack);

		GLog.w( Messages.get(this, "inscribed"));
        Blazing blaze = new Blazing();
		weapon.enchant((Weapon.Enchantment)blaze);
		
		curUser.sprite.operate(curUser.pos);
		curUser.sprite.centerEmitter().start(PurpleParticle.BURST, 0.05f, 10);
		Enchanting.show(curUser, weapon);
		Sample.INSTANCE.play(Assets.Sounds.BURNING);
		
		curUser.spend(TIME_TO_INSCRIBE);
		curUser.busy();
	}

	@Override
	public int value() {
		return 100 * quantity;
	}

	private final WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {

		@Override
		public String textPrompt() {
			return Messages.get(Stylus.class, "prompt");
		}

		@Override
		public Class<?extends Bag> preferredBag(){
			return Belongings.Backpack.class;
		}

		@Override
		public boolean itemSelectable(Item item) {
			return (item instanceof MeleeWeapon || item instanceof SpiritBow || item instanceof ReisenGun || item instanceof Armor);
		}

		@Override
		public void onSelect( Item item ) {
            if (item instanceof Weapon) {
                FireOath.this.enchant( (Weapon)item );
            } else if (item instanceof Armor) {
                FireOath.this.inscribe( (Armor)item );
            } else{
				return;
			}
		}
	};

	private final WndBag.ItemSelector hisouSelector = new WndBag.ItemSelector() {

		@Override
		public String textPrompt() {
			return Messages.get(Stylus.class, "prompt");
		}

		@Override
		public Class<?extends Bag> preferredBag(){
			return Belongings.Backpack.class;
		}

		@Override
		public boolean itemSelectable(Item item) {
			return (item instanceof HisouBlade && item.levelKnown);
		}

		@Override
		public void onSelect( Item item ) {
            HisouBladeAwakened hba = new HisouBladeAwakened();
			//Upgrade lvl
			hba.level(item.trueLevel());
			//Transfer Yinyang if Reimu
			YinYang yy = ((MeleeWeapon)item).checkYinYang();
			if (yy != null){
				hba.affixSeal(yy);
			}
			//Transfer Enchant
			if (((MeleeWeapon)item).getEnchant() != null){
				Enchantment ench = ((MeleeWeapon)item).getEnchant();
				hba.enchant(ench);
			}
			//Tranfer Augment
			hba.augment = ((Weapon)item).augment;

			hba.identify();
			
			if (item.isEquipped( Dungeon.hero )) {
				((EquipableItem)item).doUnequip( Dungeon.hero, false );
				item.detach( Dungeon.hero.belongings.backpack );
				Dungeon.hero.belongings.weapon = hba;
			} else {
				item.detach( Dungeon.hero.belongings.backpack );
				hba.collect();
			}
			detach(curUser.belongings.backpack);
		}
	};
}

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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Thief;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dread;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ConfusionGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLevitation;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MarisaSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Marisa extends Mob {

	{
		spriteClass = MarisaSprite.class;
		
		HP = HT = 20;
		defenseSkill = 12;
		
		EXP = 5;
		maxLvl = 11;
		
		flying = true;
        WANDERING = new Wandering();
		FLEEING = new Fleeing();

		loot = new PotionOfLevitation();
		lootChance = 0.2f;
		if (isLunatic()){
			immunities.add(ConfusionGas.class);
		}

	}
	
    private static final String ITEM = "item";
    public Item item;

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 4, 12 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 16;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 3);
	}
	
	@Override
	public int attackProc( Char enemy, int damage ) {
		damage = super.attackProc( enemy, damage );
		
		if (alignment == Alignment.ENEMY && item == null
				&& enemy instanceof Hero && steal( (Hero)enemy )) {
			if(isLunatic()){
				Buff.affect(this, Stamina.class, 10f);
				Sample.INSTANCE.play( Assets.Sounds.SHATTER );
				Sample.INSTANCE.play( Assets.Sounds.GAS );
				GameScene.add( Blob.seed( this.pos, 15*Dungeon.depth, ConfusionGas.class ) );
			}
			state = FLEEING;
		}

		return damage;
	}

    @Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( ITEM, item );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		item = (Item)bundle.get( ITEM );
	}

    @Override
	public int defenseProc(Char enemy, int damage) {
        if ((HP > HT/2) && (HP-damage < HT/2)){
            state = FLEEING;
        }
		return super.defenseProc(enemy, damage);
	}

	protected boolean steal( Hero hero ) {

		Item toSteal = hero.belongings.randomUnequipped();

		if (toSteal != null && !toSteal.unique && toSteal.level() < 1 ) {

			GLog.w( Messages.get(Thief.class, "stole", toSteal.name()) );
			if (!toSteal.stackable) {
				Dungeon.quickslot.convertToPlaceholder(toSteal);
			}
			Item.updateQuickslot();

			item = toSteal.detach( hero.belongings.backpack );
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void rollToDropLoot() {
		if (item != null) {
			Dungeon.level.drop( item, pos ).sprite.drop();
			item = null;
		}
		super.rollToDropLoot();
	}
	
    private class Wandering extends Mob.Wandering {
		
		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {
			super.act(enemyInFOV, justAlerted);
			
			//if an enemy is just noticed and the thief posses an item, run, don't fight.
			if (state == HUNTING && item != null){
				state = FLEEING;
			}
			
			return true;
		}
	}

	private class Fleeing extends Mob.Fleeing {
		@Override
		protected void nowhereToRun() {
			if (buff( Terror.class ) == null
					&& buff( Dread.class ) == null
					&& buffs( AllyBuff.class ).isEmpty() ) {
				if (enemySeen) {
					sprite.showStatus(CharSprite.NEGATIVE, Messages.get(Mob.class, "rage"));
					state = HUNTING;
				} else {
					state = WANDERING;
				}
			} else {
				super.nowhereToRun();
			}
		}
	}
}

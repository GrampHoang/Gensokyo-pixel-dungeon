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
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
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
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MarisaSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
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
	private static final String COUNT = "count";
    public Item item;
	public int count = 0;	//Drop item after count reach 10
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
			Buff.affect(this, Haste.class, 4f);
			state = FLEEING;
			if(isLunatic()){
				int maxDist = 0;
				Ballistica dashReal = new Ballistica( this.pos, Dungeon.hero.pos, Ballistica.STOP_SOLID);
				for (int i : PathFinder.NEIGHBOURS8){
					Ballistica dashTry = new Ballistica( this.pos, this.pos+i, Ballistica.STOP_SOLID);
					if (maxDist < Dungeon.level.distance(this.pos, dashTry.collisionPos)){
						maxDist = Dungeon.level.distance(this.pos, dashTry.collisionPos);
						dashReal = dashTry;
					}
				}

				for (int i : dashReal.subPath(1, Dungeon.level.distance(Dungeon.hero.pos, dashReal.collisionPos))) {
					CellEmitter.get(i).start(Speck.factory(Speck.JET), 0.05f, 10);
					
					Char ch = Actor.findChar(i);
					if (ch != null) {
						Buff.affect(ch, Vertigo.class, 3f);					   
					}
				}

				moveSprite(this.pos, dashReal.collisionPos);
				move(dashReal.collisionPos);
				Dungeon.level.occupyCell(Marisa.this);
			}
		}

		return damage;
	}

    @Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( ITEM, item );
		bundle.put(COUNT, count);
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		item = (Item)bundle.get( ITEM );
		count = bundle.getInt(COUNT);
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

			GLog.w( Messages.get(Marisa.class, "stole", toSteal.name()) );
			if (!toSteal.stackable) {
				Dungeon.quickslot.convertToPlaceholder(toSteal);
			}
			Item.updateQuickslot();

			item = toSteal.detach( hero.belongings.backpack );
			count = 0;
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
			count = 0;
		}
		super.rollToDropLoot();
	}
	
	@Override
	public String description() {
		String desc = super.description();

		if (item != null) {
			desc += Messages.get(Marisa.class, "carries", item.name() );
		}

		return desc;
	}

    private class Wandering extends Mob.Wandering {
		
		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {
			super.act(enemyInFOV, justAlerted);
			if (state == WANDERING && item != null){
				count++;
			}
			if (count >= 20 && item != null) {
				Dungeon.level.drop( item, pos ).sprite.drop();
				item = null;
				count = 0;
			}
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

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			super.act(enemyInFOV, justAlerted);
			if (state == FLEEING && item != null){
				count++;
				count++;
			}
			if (count >= 20 && item != null) {
				Dungeon.level.drop( item, pos ).sprite.drop();
				item = null;
				count = 0;
				state = WANDERING;
			}
			return true;
		}

	}
}

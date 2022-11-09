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

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.MasterThievesArmband;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfDivineInspiration;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfMetamorphosis;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class SusGold extends Item {

	private static final String TXT_VALUE	= "%+d";
	
	{
		image = ItemSpriteSheet.GOLD;
		stackable = false;
	}
	
	public SusGold() {
		this( 1 );
	}
	
	public SusGold( int value ) {
		this.quantity = value;
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		return new ArrayList<>();
	}
	
	@Override
	public boolean doPickUp(Hero hero, int pos) {
		
		//Effect goes here
        int effect = Random.IntRange(1, 100);
        if(effect < 40){
			if(Dungeon.gold >= 300){
				Dungeon.gold -= 300;
			} else {
				Dungeon.gold = 0;
				Buff.affect(Dungeon.hero, Poison.class).set(4f);
			}
			if(!(Dungeon.isChallenged(Challenges.LUNATIC))){
				GLog.n( Messages.get(this.getClass(), "warn") );
			}
        } else if(effect < 45){
            Buff.affect(Dungeon.hero, Vertigo.class, 5f);
        } else if(effect < 50){
            Buff.affect(Dungeon.hero, Blindness.class, 5f);
        } else if(effect < 55){
            Buff.affect(Dungeon.hero, Cripple.class, 5f);
        } else if(effect < 60){
            Buff.affect(Dungeon.hero, Slow.class, 2f);
        } else if(effect == 69){
            GLog.p( Messages.get(this.getClass(), "niiiicee") );
            PotionOfDivineInspiration podi = new PotionOfDivineInspiration();
		    podi.quantity(1).collect();
        } else if(effect == 96){
            GLog.p( Messages.get(this.getClass(), "niiceeee") );
            ScrollOfMetamorphosis som = new ScrollOfMetamorphosis();
		    som.quantity(1).collect();
        }   else {
            //nothing happen
        }
		GameScene.pickUp( this, pos );
		hero.spendAndNext( TIME_TO_PICK_UP );
		
		Sample.INSTANCE.play( Assets.Sounds.GOLD, 1, 1, Random.Float( 0.9f, 1.1f ) );
		updateQuickslot();
		
		return true;
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}
	
	@Override
	public Item random() {
		quantity = Random.IntRange( 30 + Dungeon.depth * 10, 60 + Dungeon.depth * 20 );
		return this;
	}

}

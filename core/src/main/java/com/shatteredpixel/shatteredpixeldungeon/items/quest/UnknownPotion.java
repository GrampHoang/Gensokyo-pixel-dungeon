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

package com.shatteredpixel.shatteredpixeldungeon.items.quest;

import java.util.ArrayList;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Degrade;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Doom;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Drowsy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FireImbue;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Healing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hourai;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Levitation;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silence;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.WellFed;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class UnknownPotion extends Item {

	{
		image = ItemSpriteSheet.POTION_HOLDER;
	}
    
    public static final String AC_DRINK = "DRINK";
    private static final float TIME_TO_DRINK = 1f;

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_DRINK );
		return actions;
	}

    @Override
	public void execute( final Hero hero, String action ) {

		super.execute( hero, action );
		if (action.equals( AC_DRINK )) {
			if(hero.buff(Silence.class) != null){
				GLog.w( Messages.get(Potion.class, "silence") );
				return;
			}
			else {
                GameScene.show(
                    new WndOptions(new ItemSprite(this),
                            Messages.get(UnknownPotion.class, "harmful"),
                            Messages.get(UnknownPotion.class, "sure_drink"),
                            Messages.get(Potion.class, "yes"), Messages.get(Potion.class, "no") ) {
                        @Override
                        protected void onSelect(int index) {
                            if (index == 0) {
                                drink( hero );
                            }
                        }
                    }
                );
            }
		}
	}

	public void drink( Hero hero ){
		hero.spend(TIME_TO_DRINK);
        int i = Random.IntRange(0, 10000);
        if        (i < 10){
			// Instant death
			hero.die(null);
			GLog.n(Messages.get(UnknownPotion.class, "eff1"));
        } else if (i < 45){
			//Horrible debuff
			Buff.affect(hero, Slow.class, 100f);
			Buff.affect(hero, Degrade.class, 100f);
			Buff.affect(hero, Silence.class, 100f);
			Buff.affect(hero, Cripple.class, 100f);
			Buff.affect(hero, Levitation.class, 100f);
			for (Plant.Seed seeds : Dungeon.hero.belongings.getAllItems( Plant.Seed.class )){
				seeds.detachAll( Dungeon.hero.belongings.backpack );
			}
			GLog.w(Messages.get(UnknownPotion.class, "eff2"));
		} else if (i < 80){
			// Perma doom
			Buff.affect(hero, Doom.class);
			GLog.w(Messages.get(UnknownPotion.class, "eff3"));
        } else if (i < 99){
			// +1 strength
			hero.STR++;
			hero.sprite.showStatus( CharSprite.POSITIVE, Messages.get(PotionOfStrength.class, "msg_1") );
			GLog.w(Messages.get(UnknownPotion.class, "eff4"));
        } else {    //Jackpot, 1%
			GLog.p(Messages.get(UnknownPotion.class, "eff5"));
			Buff.affect(hero, Invisibility.class, 25f);
			hero.busy();
			Dungeon.hero.sprite.operateWithTime(0.25f, new Callback() {
				@Override
				public void call() {
					// Dungeon.hero.next();
					GLog.n(Messages.get(UnknownPotion.class, "eff5_2"));
					// Dungeon.hero.next();
					Dungeon.hero.spend(50f);
					Dungeon.hero.sprite.operateWithTime(2f, new Callback() {
						@Override
						public void call() {
							GLog.p(Messages.get(UnknownPotion.class, "jackpot"));
							Buff.affect(hero, Hourai.class);

							Dungeon.hero.next();
						}
					});
				}
			});
        }
		detach( hero.belongings.backpack );
	}

	@Override
	public int value() {
		return 1;   // "You don't know what this potion does? It's trash then" - Shopkeeper
	}
}

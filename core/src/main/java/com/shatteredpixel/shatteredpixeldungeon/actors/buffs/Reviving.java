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

 package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou.Mokou;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou.Kaguya;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MokouSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.KaguyaSprite;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class Reviving extends FlavourBuff {

    {
        type = buffType.POSITIVE;
    }

    public static int DOWN_TIME = 3;
    private int turnsToRevive = DOWN_TIME;

    @Override
    public boolean act() {
        turnsToRevive--;
        if (turnsToRevive <= 0){
            if (target.paralysed > 0){
			    target.paralysed = 0;
            }
            Sample.INSTANCE.play( Assets.Sounds.EVOKE, 1f, 5f );
            target.HP = target.HT;
            target.sprite.idle();
            super.detach();
            return true;
        }

        spend(TICK);
        return true;
    }

    @Override
    public boolean attachTo(Char target) {
        if (super.attachTo( target )) {
            target.paralysed++;
        return true;
        } else {
            return false;
        }
    }

    @Override
    public void detach() {
        if (target.paralysed > 0)
			target.paralysed = 0;
        super.detach();
    }

    @Override
	public void fx(boolean on) {
        if(target instanceof Mokou){
            if (on) target.sprite.add(CharSprite.State.CHARGING);
            else target.sprite.remove(CharSprite.State.CHARGING);
        }
	}

    private static final String LEFT  = "left";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEFT, turnsToRevive);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        turnsToRevive = bundle.getInt(LEFT);
    }
}
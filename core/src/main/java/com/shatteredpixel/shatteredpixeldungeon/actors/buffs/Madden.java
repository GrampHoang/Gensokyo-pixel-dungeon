package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;


public class Madden extends FlavourBuff {

    public static final float DURATION = 3f;

    public int madstack;
    
    {
        type = buffType.NEGATIVE;
        announced = true;
        madstack = 0;
    }

    public int getMadStack() {
        return madstack;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("madstack", madstack);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        madstack = bundle.getInt("madstack");
    }

    @Override
    public int icon() {
        return BuffIndicator.BERSERK;
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", dispTurns(), (1f + 0.03f* madstack));
    }
}
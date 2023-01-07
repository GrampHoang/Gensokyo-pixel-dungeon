package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;


public class MusicFlow extends FlavourBuff {

    public static final float DURATION = 30f;

    public int flowstack;
    
    {
        type = buffType.POSITIVE;
        announced = false;
        flowstack = 0;
    }

    public int getflowstack() {
        return flowstack;
    }

    public float getSpeedBuff(){
        return 1 + flowstack*2/100;
    }

    public void increaseFlow(int count){
        if (flowstack < 25) flowstack += count;
        if (flowstack > 25) flowstack = 25;
    }
    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("flowstack", flowstack);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        flowstack = bundle.getInt("flowstack");
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
        return Messages.get(this, "desc", dispTurns(), (1f + 0.02f* flowstack));
    }
}
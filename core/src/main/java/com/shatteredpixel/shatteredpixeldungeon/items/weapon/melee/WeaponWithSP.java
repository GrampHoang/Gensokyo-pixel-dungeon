package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindofMisc;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class WeaponWithSP extends MeleeWeapon{
    {
        have_skill = true;

        defaultAction = AC_SKILL;
    }

    protected int charge = 0;
    protected int chargeCap = 100;      //always cap at 100%
    protected int chargeNeed = 100;    //charge needed to use skill

    protected int chargeGain = 1;  //charge gain per hit

    // protected int skill_type = 0;   // 0: No target needed,   1: Select a cell,   2: Select a target

    // protected int NO_TARGET  = 0;
    // protected int TARGET = 1;
    // protected int CELL = 2;

    public static final String AC_SKILL = "SKILL";

    @Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (isEquipped( hero ))
			actions.add(AC_SKILL);
		return actions;
	}

    @Override
	public void execute(Hero hero, String action ) {
		super.execute(hero, action);
		if (action.equals(AC_SKILL)){
            if (charge >= chargeNeed){
                if (useSkill()) charge = charge - chargeNeed;
                updateQuickslot();
            } else {
				GLog.w( Messages.get(this,"need_charge") );
			}
		}
	}

    protected boolean useSkill(){
        //do nothing by default
        //Only the hero can use skill
        return false;
    }

    @Override
	public String status() {
		
		//if isn't IDed, or is cursed, don't display anything
		if (!isIdentified() || cursed){
			return null;
		}

		//display as percent
		if (chargeCap == 100)
			return Messages.format( "%d%%", charge );

		//otherwise, if there's no charge, return null.
		return null;
	}

    public void charge(float amount){
		charge += amount;
	}

    @Override
	public int proc(Char attacker, Char defender, int damage) {
        charge += chargeGain;
        if (charge > chargeCap) charge = chargeCap;
        updateQuickslot();
		return super.proc(attacker, defender, damage);
	}

    // @Override
	// public String info() {
    //     String info = super.info();

    // }

    private static final String CHARGE = "skill_charge";

    @Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);
		bundle.put( CHARGE , charge );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);
		if (chargeCap > 0)  charge = Math.min( chargeCap, bundle.getInt( CHARGE ));
		else                charge = bundle.getInt( CHARGE );
	}
}

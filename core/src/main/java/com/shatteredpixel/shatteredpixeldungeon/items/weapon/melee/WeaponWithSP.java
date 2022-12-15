package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfMagic;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class WeaponWithSP extends MeleeWeapon{
    {
        have_skill = true;

        defaultAction = AC_SKILL;
    }

    protected int charge = 100;
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
				// Dungeon.hero.busy();
                if (useSkill()) charge = charge - chargeNeed;
				// Dungeon.hero.next();
                updateQuickslot();
            } else {
				GLog.w( Messages.get(this,"need_charge") );
			}
		}
	}

    protected boolean useSkill(){
        //do nothing by default
        //Only the hero can use skill
		Dungeon.hero.spend(1f); //Should scale with speed, but oh well. We haven't call this as super yet so still take 0 turn
        return false;
    }

	//These 2 are for cell-selecting skill. By default you use skill immediatly and spend SP
	//However, for cell selecting, you will not spend SP until you actually use skill
	//To do that we spend SP by default, then refund SP when select cell and spend SP again if a cell is picked successfully.
	protected void refundSP(){
		charge = charge + chargeNeed;
	}

	protected void spendSP(){
		charge = charge - chargeNeed;
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
        charge += chargeGain * RingOfMagic.weaponSPChargeMultiplier(attacker);;
        if (charge > chargeCap) charge = chargeCap;
        updateQuickslot();
		return super.proc(attacker, defender, damage);
	}

	public String skillInfo(){
		return Messages.get(this, "skill_desc", chargeGain, chargeNeed);
	}

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

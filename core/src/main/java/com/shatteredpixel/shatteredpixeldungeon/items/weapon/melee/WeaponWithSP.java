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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfMagic;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public abstract class WeaponWithSP extends MeleeWeapon{
    {
        have_skill = true;
		usesTargeting = true;
        defaultAction = AC_SKILL;
    }

    protected int charge = 100;
    protected int chargeCap = 100;      //always cap at 100%, except for drinking wine.
	protected int chargeHardCap = 1000; //Hardcap for endless alcohol
    protected int chargeNeed = 100;     //charge needed to use skill, default 100

    protected int chargeGain = 1;  //charge gain per hit

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
			if (!isEquipped( hero )) {
				GLog.i( Messages.get(WeaponWithSP.class, "need_to_equip") );
			} else if (!isIdentified()){
				GLog.i( Messages.get(WeaponWithSP.class, "need_to_identify") );
			} else if (charge >= chargeNeed){
                if (useSkill()) charge = charge - chargeNeed;
                updateQuickslot();
            } else {
				GLog.w( Messages.get(WeaponWithSP.class,"need_charge") );
			}
		}
	}

	public int charge(){
        return charge;
    }

    protected abstract boolean useSkill();
	// {
    //     //do nothing by default, weapon will override this
    //     //Only the hero can use skill
	// 	Dungeon.hero.spend(1f); //Should scale with speed, but oh well. We haven't call this as super yet so still take 0 turn
    //     return false;
    // }

	//These 2 are for cell-selecting skill. By default you use skill immediatly and spend SP
	//However, for cell selecting, you will not spend SP until you actually use skill
	//To do that we spend SP by default, then refund SP when select cell and spend SP again if a cell is picked successfully.

	public boolean refundSP(int refund){
		if (charge < chargeHardCap){
			charge = charge + refund;
			if (charge > chargeHardCap) charge = chargeHardCap;
			return true;
		} else {
			GLog.w("Your weapon have too much charge!");
			return false;
		}
	}

	protected boolean spendSP(int spend){
		if (charge > spend){
			Badges.useWeaponSkill();
			charge = charge - spend;
			return true;
		} else {
			GLog.w("Not enough charge to use this skill!");
			return false;
		}
	}

	protected void refundSP(){
		charge = charge + chargeNeed;
	}

	//Usually we just call this at the end of skill, I handle not enough charge in the method execute() above
	protected void spendSP(){
		Badges.useWeaponSkill();
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
			// return Messages.format( "%d%%", charge );
			return Integer.toString(charge);

		//otherwise, if there's no charge, return null.
		return null;
	}

    public void charge(float amount){
		charge += amount;
	}

    @Override
	public int proc(Char attacker, Char defender, int damage) {
		//Only charge if not full
		if (charge < chargeCap){
			charge += chargeGain * RingOfMagic.weaponSPChargeMultiplier(attacker);;
        	if (charge > chargeCap) charge = chargeCap;
		}
		//So that when you overflow after drink wine it won't reset
        updateQuickslot();
		return super.proc(attacker, defender, damage);
	}

	public String skillInfo(){
		return Messages.get(WeaponWithSP.class, "skill_desc", chargeGain, chargeNeed);
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
		charge = bundle.getInt( CHARGE );
	}
}

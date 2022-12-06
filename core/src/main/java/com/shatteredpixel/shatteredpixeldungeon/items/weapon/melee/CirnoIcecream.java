package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class CirnoIcecream extends WeaponWithSP {

	{
		image = ItemSpriteSheet.CIRNO_ICECREAM;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1f;

		tier = 2;
		DLY = 1f;

        chargeGain = 5;
    }

	@Override
	public int max(int lvl) {
		return  Math.round(4f*(tier+1)) - 1 + //11 base
				lvl*Math.round(1f*(tier+1)); // no change scale
	}

    @Override
	public int proc(Char attacker, Char defender, int damage) {
        if ( Random.IntRange(0, 9) == 1){
            Buff.affect(defender, Chill.class, 3f );
        }
        return super.proc(attacker, defender, damage);
	}

    @Override
	protected boolean useSkill(){
        Buff.affect(Dungeon.hero, Hunger.class).satisfy(50);
		// new Flare( 5, 32 ).color( 0xFF0000, true ).show( curUser.sprite, 2f );
		Buff.affect( Dungeon.hero, Chill.class, 6f );
        return true;
	}

    public static final String AC_EAT = "DEVOUR";

    @Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (isEquipped( hero ))
			actions.add(AC_EAT);
		return actions;
	}

    @Override
	public void execute(Hero hero, String action ) {
		super.execute(hero, action);
		if (action.equals(AC_EAT)){
            if(cursed && cursedKnown){
                GLog.w("Don't eat cursed ice-cream");
            } else {
                new Flare( 10, 32 ).color( 0xadd8e6, true ).show( curUser.sprite, 0.3f );
                this.detach( Dungeon.hero.belongings.backpack );
                updateQuickslot();
                Buff.affect(Dungeon.hero, Frost.class, 8f);
                if(cursed) MysteryMeat.effect(Dungeon.hero);
            }
		}
	}

}

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Crab;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Slime;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Goo;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;


public class WoodStick extends MeleeWeapon {

	{
		image = ItemSpriteSheet.WOOD_STICK;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1f;

		tier = 2;
		DLY = 1f;
    }

	@Override
	public int max(int lvl) {
		return  Math.round(4f*(tier+1)) + //12 base
				lvl*Math.round(1f*(tier+1)); // no change scale
	}

    @Override
	public int proc(Char attacker, Char defender, int damage) {
        if (defender.properties().contains(Char.Property.FAIRY) || defender instanceof Crab || defender instanceof Slime || defender instanceof Goo){
            damage += 5;
        }
        return super.proc(attacker, defender, damage);
	}
}
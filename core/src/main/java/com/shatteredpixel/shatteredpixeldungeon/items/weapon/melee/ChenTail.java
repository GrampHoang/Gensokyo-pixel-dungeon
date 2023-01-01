package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Stamina;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;


public class ChenTail extends MeleeWeapon {

	{
		image = ItemSpriteSheet.CHEN_TAIL;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1f;

		tier = 4;
		DLY = 0.75f; 

    }

	@Override
	public int max(int lvl) {
		return  Math.round(3f*(tier+1)) + 3 + //18 base
				lvl*Math.round(0.75f*(tier+1)); // slightly weaker scale
	}

    @Override
	public int proc(Char attacker, Char defender, int damage) {
        if (Dungeon.level.water[attacker.pos]){
            Buff.affect(attacker, Slow.class, 4f);
        } else {
            Buff.affect(attacker, Stamina.class, 5f);
        }
        
        return super.proc(attacker, defender, damage);
	}

}

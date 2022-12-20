package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;


public class ClownTorch extends WeaponWithSP {

	{
		image = ItemSpriteSheet.CLOWN_TORCH;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1f;

		tier = 2;
		DLY = 1f;

        chargeGain = 4;
    }

	@Override
	public int max(int lvl) {
		return  Math.round(4f*(tier+1)) + //12 base
				lvl*Math.round(1f*(tier+1)); // no change scale
	}

    @Override
	public int proc(Char attacker, Char defender, int damage) {
        if ( Random.IntRange(0, 9) == 1){
            Buff.affect(defender, Amok.class, 4f );
        }
        return super.proc(attacker, defender, damage);
	}

    @Override
	protected boolean useSkill(){
		new Flare( 10, 64 ).color( 0xFF0000, true ).show( curUser.sprite, 4f );
		Sample.INSTANCE.play( Assets.Sounds.CHALLENGE );
		for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
			if (mob.alignment != Char.Alignment.ALLY && Dungeon.level.heroFOV[mob.pos]) {
				Buff.affect( mob, Amok.class, 4f );
			}
		}
		Dungeon.hero.spendAndNext(1f);
        return true;
	}
	
}

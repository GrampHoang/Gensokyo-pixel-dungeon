package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Random;

public class WriggleBug extends WeaponWithSP {

	{
		image = ItemSpriteSheet.WRIGGLE_BUG;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1f;

		tier = 2;
		DLY = 1f;

        chargeGain = 10;
    }

	public float BLIND_DUR = 3f;
	public float LIGHT_DUR = 10f;

	@Override
	public int max(int lvl) {
		return  Math.round(4f*(tier+1)) + //12 base
				lvl*Math.round(1f*(tier+1)); // no change scale
	}

    @Override
	public int proc(Char attacker, Char defender, int damage) {
        if ( Random.IntRange(0, 9) == 1){
            Buff.affect(defender, Blindness.class, BLIND_DUR );
        }
        return super.proc(attacker, defender, damage);
	}

    @Override
	protected boolean useSkill(){
		new Flare( 8, 128 ).color( 0xFFFF55, true ).show( curUser.sprite, 0.5f );
		Buff.affect( Dungeon.hero, Light.class, LIGHT_DUR );
		for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
			if (mob.alignment != Char.Alignment.ALLY && Dungeon.level.heroFOV[mob.pos] && Dungeon.level.distance(mob.pos, Dungeon.hero.pos) < 8) {
				Buff.affect( mob, Blindness.class, BLIND_DUR );
			}
		}
		Dungeon.hero.spendAndNext(1f);
        return true;
	}

	@Override
	public String skillInfo(){
		return Messages.get(WriggleBug.class, "skill_desc", chargeGain, chargeNeed, (int)BLIND_DUR, (int)LIGHT_DUR);
	}
}

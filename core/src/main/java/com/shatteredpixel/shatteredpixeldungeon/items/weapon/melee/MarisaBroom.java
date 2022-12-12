package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class MarisaBroom extends WeaponWithSP {

	{
		image = ItemSpriteSheet.WOOD_STICK;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1f;

		tier = 3;
		DLY = 1f;

        chargeGain = 25;
    }

	@Override
	public int max(int lvl) {
		return  Math.round(4f*(tier+1)) + //16 base
				lvl*Math.round(1f*(tier+1)); // no change scale
	}

    @Override
	protected boolean useSkill(){
		for (int i : PathFinder.NEIGHBOURS8){
            Char ch = Actor.findChar(i + Dungeon.hero.pos);
            if (ch != null){
                Buff.prolong( ch, Blindness.class, 3);
            }
            CellEmitter.get(i + Dungeon.hero.pos).burst(SmokeParticle.FACTORY, 4);
        }
        CellEmitter.center(Dungeon.hero.pos).burst(BlastParticle.FACTORY, 5);
        return true;
	}
}

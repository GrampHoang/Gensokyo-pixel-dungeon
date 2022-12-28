package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char.Alignment;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;


public class LusanaViolin extends WeaponWithSP {

	{
		image = ItemSpriteSheet.CLOWN_TORCH;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1f;

		tier = 4;
		DLY = 1f;

        chargeGain = 10;
    }

	@Override
	public int max(int lvl) {
		return  Math.round(3f*(tier+1)) + //15 base
				lvl*Math.round(0.6f*(tier+1)); // 3 instead of 5 per level
	}

    @Override
	public int proc(Char attacker, Char defender, int damage) {
        int damageDo = super.proc(attacker, defender, damage);
        int count = 0;
        for (int i : PathFinder.NEIGHBOURS8){
            Char ch = Actor.findChar(attacker.pos + i);
            //Exist and not same alignment
            if (ch != null && ch.alignment != attacker.alignment){
                count++;
                ch.damage(damageDo, attacker);
                ch.sprite.centerEmitter().start( Speck.factory( Speck.NOTE ), 0.2f, 3 );
            }
        }
        if (count == 0) {
            damageDo = (int)Math.round(damageDo * 1.5f);
            defender.sprite.centerEmitter().start( Speck.factory( Speck.NOTE ), 0.3f, 5 );
        } else {
            defender.sprite.centerEmitter().start( Speck.factory( Speck.NOTE ), 0.2f, 2 );
        }
        return damageDo;
	}

    @Override
	protected boolean useSkill(){
        Dungeon.hero.busy();
		for (int i : PathFinder.NEIGHBOURS8){
            Char ch = Actor.findChar(Dungeon.hero.pos + i);
            //Exist, not attacker and not same alignment
            if (ch != null && ch.alignment != Alignment.ALLY){
                ch.damage(max()/2, Dungeon.hero);
            }
            ch.sprite.centerEmitter().start( Speck.factory( Speck.NOTE ), 0.3f, 5 );
        }
        return true;
    }

    public String skillInfo(){
		return Messages.get(this, "skill_desc", chargeGain, chargeNeed, max()/2);
	}
}

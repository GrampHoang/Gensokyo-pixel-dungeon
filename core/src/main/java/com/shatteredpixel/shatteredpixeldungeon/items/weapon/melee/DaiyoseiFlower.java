package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;

public class DaiyoseiFlower extends WeaponWithSP {

	{
		image = ItemSpriteSheet.DAIYOSEI_FLOWER;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1f;

		tier = 2;
		DLY = 1f;

        chargeGain = 8;
        // skill_type = NO_TARGET;
    }

	@Override
	public int max(int lvl) {
		return  Math.round(4f*(tier+1)) - 2 + //10 base
				lvl*Math.round(1f*(tier+1)); // no change scale
	}

    @Override
	protected boolean useSkill(){
        Hero hero = Dungeon.hero;
		hero.sprite.emitter().start( Speck.factory( Speck.HEALING ), 0.4f, 3 );
		hero.HP += hero.HT/5;
        if(hero.HP > hero.HT) hero.HP = hero.HT;
        return true;
	}
}

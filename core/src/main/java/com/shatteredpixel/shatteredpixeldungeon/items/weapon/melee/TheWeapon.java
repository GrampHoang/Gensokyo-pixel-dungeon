/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2022 Evan Debenham
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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Camera;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.audio.Sample;

public class TheWeapon extends MeleeWeapon {

	{
		image = ItemSpriteSheet.ARTIFACT_BEACON;
		hitSound = Assets.Sounds.HIT_STRONG;
		hitSoundPitch = 1.3f;

        unique = true;
        bones = false;
		tier = 17;
		DLY = 0.4f;
	}

	@Override
	public int max(int lvl) {
		return  69;
	}

    @Override
	public int damageRoll(Char owner) {
        Hero hero = (Hero) owner;
        Char enemy = hero.enemy();

        hero.sprite.add(CharSprite.State.BURSTING_POWER);
        Camera.main.shake(2, 0.5f);
        GameScene.flash(0xAAAAAA);
        Sample.INSTANCE.play(Assets.Sounds.DEGRADE, 0.75f, 0.88f);
        for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
            if (Dungeon.level.heroFOV[mob.pos]) {
                mob.sprite.emitter().burst(ElmoParticle.FACTORY, 30);
                Buff.affect(mob, Amok.class, 20f);
                Buff.affect(mob, Cripple.class, 10f);
                Buff.affect(mob, Paralysis.class, 2f);
                Buff.affect(mob, Bleeding.class).set(10f);
                Buff.affect(mob, Doom.class);

            }
        }

        Buff.affect(enemy, Bleeding.class).set(Math.round((max()+1)/4));
		return super.damageRoll(owner);
	}

    @Override
	public int value() {
		return  0;
	}
}

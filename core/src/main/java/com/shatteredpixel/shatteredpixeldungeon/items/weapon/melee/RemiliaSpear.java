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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.*;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.sprites.*;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RemiliaSprite;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SmokeScreen;
import com.watabou.noosa.Camera;
import com.watabou.utils.Bundle;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;

public class RemiliaSpear extends WeaponWithSP {

    {
        image = ItemSpriteSheet.WOOD_STICK;
        hitSound = Assets.Sounds.HIT_CRUSH;
        hitSoundPitch = 1f;

        tier = 3;
        ACC = 99f; // lots of boost to accuracy
        RCH = 2; // extra reach

        chargeGain = 2;
        chargeNeed = 100;
    }

    @Override
    public int max(int lvl) {
        return 14 * (tier + 1) + // 14 base
                lvl * (tier); // 1 less damage per levl
    }

    @Override
	protected boolean useSkill(){
		GameScene.selectCell(targeter);
        return true;
	}

	private CellSelector.Listener targeter = new CellSelector.Listener() {

		@Override
		public void onSelect(Integer cell) {
            Camera.main.shake(0.5f, 0.25f);
            WandOfBlastWave.BlastWave.blast(curUser.pos);
            Ballistica b = new Ballistica(curUser.pos, cell, Ballistica.MASTERSPARK);
            // levatin_pos = Dungeon.hero.pos; This should act like throwing projectile but
            // not sure how to lol
            curUser.sprite.parent.add(
                    new Beam.DeathRay(curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(b.collisionPos)));

            for (int i : b.subPath(1, Dungeon.level.distance(curUser.pos, b.collisionPos))) {
                CellEmitter.get(i).start(Speck.factory(Speck.STEAM), 0.07f, 10);
                if (Dungeon.level.pit[i]) GameScene.add(Blob.seed(i, 1, Fire.class));
                    else GameScene.add(Blob.seed(i, 10, Fire.class));

                Char ch = Actor.findChar(i);
                if (ch != null) {
                    Buff.affect(ch, Paralysis.class, 1f);
                    ch.damage(Random.IntRange(min() * 2, max()), curUser);

                    Actor.addDelayed(new Pushing(ch, ch.pos, b.collisionPos), 0);
                    ch.pos = b.collisionPos;
                    Dungeon.level.occupyCell(ch);
                }
            }
            CellEmitter.center(b.collisionPos).burst(BlastParticle.FACTORY, 30);
            PathFinder.buildDistanceMap(b.collisionPos, BArray.not(Dungeon.level.solid, null), 1);
            for (int i = 0; i < PathFinder.distance.length; i++) {
                if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                    if (Dungeon.level.pit[i]) GameScene.add(Blob.seed(i, 1, Fire.class));
                    else GameScene.add(Blob.seed(i, 10, Fire.class));
                    CellEmitter.get(i).burst(FlameParticle.FACTORY, 5);
                    CellEmitter.get(i).burst(SmokeParticle.FACTORY, 4);
                    Char ch = Actor.findChar(i);
                    if (ch != null) {
                        ch.damage(8, this);
                    }
                }
            }

            curUser.spend(1f);
        }

        @Override
        public String prompt() {
            return Messages.get(this, "prompt");
        }

	};
}

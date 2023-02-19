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
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.*;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.sprites.*;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.watabou.noosa.Camera;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Callback;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;

public class RemiliaSpear extends WeaponWithSP {

    {
        image = ItemSpriteSheet.REMILIA_SPEAR;
        hitSound = Assets.Sounds.HIT_CRUSH;
        hitSoundPitch = 1f;

        tier = 3;
        ACC = 99f; // lots of boost to accuracy
        RCH = 2; // extra reach

        chargeGain = 3;
        chargeNeed = 100;
    }

    public int skilldmg_min() { return max(); }
	public int skilldmg_max() { return max()*2;   }

    @Override
    public int max(int lvl) {
        return 12 + // 14 base
                lvl * (tier); // +3 per level instead of 4
    }

    @Override
	protected boolean useSkill(){
        refundSP();
		GameScene.selectCell(targeter);
        return true;
	}

	private CellSelector.Listener targeter = new CellSelector.Listener() {

		@Override
		public void onSelect(Integer cell) {
            if (cell == null){
				return;
			}
			// Char ch = Actor.findChar(cell);

			Ballistica attack = new Ballistica( Dungeon.hero.pos, cell, Ballistica.STOP_SOLID);

			if (attack.collisionPos == Dungeon.hero.pos){
				GLog.w(Messages.get(RemiliaSpear.class, "cannot_throw"));
				return;
			}

            Camera.main.shake(0.5f, 0.25f);
            WandOfBlastWave.BlastWave.blast(Dungeon.hero.pos);
            Ballistica b = new Ballistica(Dungeon.hero.pos, cell, Ballistica.MASTERSPARK);
            // levatin_pos = Dungeon.hero.pos; This should act like throwing projectile but
            // not sure how to lol
            Dungeon.hero.sprite.parent.add(
                    new Beam.DeathRay(Dungeon.hero.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(b.collisionPos)));

            for (int i : b.subPath(1, Dungeon.level.distance(Dungeon.hero.pos, b.collisionPos))) {
                CellEmitter.get(i).start(Speck.factory(Speck.STEAM), 0.07f, 10);
                if (Dungeon.level.flamable[i]) {
                    Dungeon.level.destroy(i);
                    GameScene.updateMap(i);
                }
                if (Dungeon.level.pit[i]) GameScene.add(Blob.seed(i, 1, Fire.class));
                    else GameScene.add(Blob.seed(i, 10, Fire.class));

                Char ch = Actor.findChar(i);
                if (ch != null) {
                    Buff.affect(ch, Paralysis.class, 1f);
                    ch.damage(skilldmg_max(), Dungeon.hero);

                    // Actor.addDelayed(new Pushing(ch, ch.pos, b.collisionPos), 1);
                    Actor.addDelayed(new Pushing(ch, ch.pos, b.collisionPos, new Callback() {
                        public void call() {
                            ch.pos = b.collisionPos;
                            Dungeon.level.occupyCell(ch);
                            Dungeon.observe();
                        }
                    }), 0);

                   
                }
            }
            
            CellEmitter.center(b.collisionPos).burst(BlastParticle.FACTORY, 30);
            PathFinder.buildDistanceMap(b.collisionPos, BArray.not(Dungeon.level.solid, null), 1);
            for (int i = 0; i < PathFinder.distance.length; i++) {
                if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                    if (Dungeon.level.flamable[i]) {
                        Dungeon.level.destroy(i);
                        GameScene.updateMap(i);
                    }

                    Trap t = Dungeon.level.traps.get(i);
					if (t != null && t.active){
						t.reveal();
                        t.disarm();
					}
                    //destroys items / triggers bombs caught in the blast.
					Heap heap = Dungeon.level.heaps.get(i);
					if (heap != null)
						heap.explode();
					
                    if (Dungeon.level.pit[i]) GameScene.add(Blob.seed(i, 1, Fire.class));
                    else GameScene.add(Blob.seed(i, 10, Fire.class));
                    CellEmitter.get(i).burst(FlameParticle.FACTORY, 5);
                    CellEmitter.get(i).burst(SmokeParticle.FACTORY, 4);
                    Char ch = Actor.findChar(i);
                    if (ch != null) {
                        ch.damage(Random.IntRange(skilldmg_min(), skilldmg_max()), this);
                        Buff.affect(ch, Paralysis.class, 2f);
                    }
                }
            }
            Dungeon.observe();
            spendSP();
            Dungeon.hero.spendAndNext(1f);
        }

        @Override
        public String prompt() {
            return Messages.get(RemiliaSpear.class, "prompt");
        }

	};

    @Override
    public String skillInfo(){
		return Messages.get(RemiliaSpear.class, "skill_desc", chargeGain, chargeNeed, skilldmg_min(), skilldmg_max());
	}
}

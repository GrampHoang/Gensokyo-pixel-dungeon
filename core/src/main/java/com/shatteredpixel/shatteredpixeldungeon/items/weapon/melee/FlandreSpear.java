/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2022 Evan Debenham
 *
 * Gensokyo Pixel Dungeon
 * Copyright (C) 2022-2023 GrampHoang
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
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.*;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.sprites.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.watabou.noosa.Camera;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import com.watabou.utils.Callback;
import com.shatteredpixel.shatteredpixeldungeon.effects.ThrowRay;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;

public class FlandreSpear extends WeaponWithSP {

    {
        image = ItemSpriteSheet.FLANDRE_SPEAR;
        hitSound = Assets.Sounds.HIT_CRUSH;
        hitSoundPitch = 1f;

        tier = 3;
        ACC = 1.4f; // lots of boost to accuracy
        RCH = 3; // extra reach

        chargeGain = 3;
        chargeNeed = 100;
    }

    public int skilldmg_min() { return max(); }
	public int skilldmg_max() { return max()*2; }

	@Override
	public int max(int lvl) {
		return  12 +    //14 base, down from 20
				lvl*(tier);     //+3 per level, down from +4
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

			if (cell.intValue() == Dungeon.hero.pos || Dungeon.level.solid[cell] == true){
				GLog.w(Messages.get(FlandreSpear.class, "cannot_throw"));
				return;
			}

            if (Dungeon.hero.fieldOfView[cell] == false){
				GLog.w(Messages.get(FlandreSpear.class, "out_vision"));
				return;
			}

            Dungeon.hero.sprite.zap(cell, new Callback(){
                @Override
                public void call(){
                    throwUp(cell);
                    Dungeon.hero.sprite.zap(cell, new Callback(){
                        @Override
                        public void call(){
                            fallDown(cell);
                            Dungeon.hero.sprite.zap(cell, new Callback(){
                                @Override
                                public void call(){
                                Dungeon.hero.spendAndNext(1f);
                                }
                            });
                        }
                    });
                }
            });
            spendSP();
        }

        @Override
        public String prompt() {
            return Messages.get(RemiliaSpear.class, "prompt");
        }

	};

    public void throwUp(int cell){
        Camera.main.shake(0.5f, 0.25f);
        WandOfBlastWave.BlastWave.blast(Dungeon.hero.pos);
        PointF start = Dungeon.hero.sprite.center();
        PointF end = Dungeon.hero.sprite.center();
        PointF target = DungeonTilemap.raisedTileCenterToWorld(cell);
        end.y -= 100;
        end.x = (end.x*3 + target.x)/4;
        Dungeon.hero.sprite.parent.add(new ThrowRay.DeathRay(start, end));
    }

    public void fallDown(int cell){
        PointF start = DungeonTilemap.raisedTileCenterToWorld(cell);
        PointF end = DungeonTilemap.raisedTileCenterToWorld(cell);
        PointF from = Dungeon.hero.sprite.center();
        start.y -= 100;
        start.x = (from.x + end.x*3)/4;
        Dungeon.hero.sprite.parent.add(new ThrowRay.DeathRay(start, end));

        CellEmitter.center(cell).burst(BlastParticle.FACTORY, 30);

        Char cha = Actor.findChar(cell);
                if (cha != null) {
                    cha.damage(skilldmg_max(), this);
                    Buff.affect(cha, Paralysis.class, 2f);
                }

        for (int i : PathFinder.TWOTILES_X) {
            if (Dungeon.level.flamable[cell + i]) {
                Dungeon.level.destroy(cell + i);
                GameScene.updateMap(cell + i);
            }

            if (!Dungeon.level.solid[cell+i]){
                //Disarm trap
                Trap t = Dungeon.level.traps.get(cell + i);
					if (t != null && t.active){
						t.reveal();
                        t.disarm();
					}
                //destroys items / triggers bombs caught in the blast.
                Heap heap = Dungeon.level.heaps.get(cell + i);
                if (heap != null)
                    heap.explode();
                
                //Fire + Damage
                if (Dungeon.level.pit[cell + i]) GameScene.add(Blob.seed(cell + i, 1, Fire.class));
                else GameScene.add(Blob.seed(cell + i, 5, Fire.class));
                CellEmitter.get(cell + i).burst(SmokeParticle.FACTORY, 4);
                CellEmitter.get(cell + i).burst(FlameParticle.FACTORY, 5);
                Char ch = Actor.findChar(cell + i);
                if (ch != null) {
                    ch.damage(Random.IntRange(skilldmg_min(), skilldmg_max()), this);
                    Buff.affect(ch, Paralysis.class, 1f);
                }
            }
        }
        Dungeon.observe();
        //Just some effect
        int[] i =  PathFinder.NEIGHBOURS4_CORNERS_FAR;
        PointF topleft = DungeonTilemap.raisedTileCenterToWorld(cell + i[0]);
        PointF topright = DungeonTilemap.raisedTileCenterToWorld(cell + i[1]);
        PointF botleft = DungeonTilemap.raisedTileCenterToWorld(cell + i[2]);
        PointF botright = DungeonTilemap.raisedTileCenterToWorld(cell + i[3]);
        topleft.y  += 4; topleft.x  -= 2;
        topright.y += 4; topright.x += 2;
        botleft.y  += 8; botleft.x  -= 2;
        botright.y += 8; botright.x += 2;
        Dungeon.hero.sprite.parent.add(new Beam.LightRay(topleft,  botright));
        Dungeon.hero.sprite.parent.add(new Beam.LightRay(topright, botleft));
    }

    @Override
    public String skillInfo(){
		return Messages.get(this, "skill_desc", chargeGain, chargeNeed, skilldmg_min(), skilldmg_max());
	}
}

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
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
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
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import com.watabou.utils.Callback;
import com.watabou.utils.Reflection;
import com.shatteredpixel.shatteredpixeldungeon.effects.ThrowRay;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;

public class FlandreSpear extends WeaponWithSP {

    {
        image = ItemSpriteSheet.WOOD_STICK;
        hitSound = Assets.Sounds.HIT_CRUSH;
        hitSoundPitch = 1f;

        tier = 3;
        ACC = 1.4f; // lots of boost to accuracy
        RCH = 2; // extra reach

        chargeGain = 2;
        chargeNeed = 1;
    }

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

			if (cell.intValue() == Dungeon.hero.pos){
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
                    throwUp();
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

    public void throwUp(){
        Camera.main.shake(0.5f, 0.25f);
        WandOfBlastWave.BlastWave.blast(Dungeon.hero.pos);
        PointF start = Dungeon.hero.sprite.center();
        PointF end = Dungeon.hero.sprite.center();
        end.y -= 100;
        Dungeon.hero.sprite.parent.add(new ThrowRay.DeathRay(start, end));
    }

    public void fallDown(int cell){
        PointF start = DungeonTilemap.raisedTileCenterToWorld(cell);
        PointF end = DungeonTilemap.raisedTileCenterToWorld(cell);
        start.y -= 100;
        Dungeon.hero.sprite.parent.add(new ThrowRay.DeathRay(start, end));

        CellEmitter.center(cell).burst(BlastParticle.FACTORY, 30);

        for (int i : PathFinder.TWOTILES_X) {
            if (!Dungeon.level.solid[cell+i]){
                if (Dungeon.level.pit[cell + i]) GameScene.add(Blob.seed(cell + i, 1, Fire.class));
                else GameScene.add(Blob.seed(cell + i, 5, Fire.class));
                CellEmitter.get(cell + i).burst(SmokeParticle.FACTORY, 4);
                CellEmitter.get(cell + i).burst(FlameParticle.FACTORY, 5);
                Char ch = Actor.findChar(cell + i);
                if (ch != null) {
                    ch.damage(max()*2, this);
                    Buff.affect(ch, Paralysis.class, 2f);
                }
            }
        }

        for (int i : PathFinder.NEIGHBOURS4_CORNERS_FAR){
			((MagicMissile)curUser.sprite.parent.recycle( MagicMissile.class )).reset(
				MagicMissile.FIRE_CONE,
				DungeonTilemap.tileCenterToWorld(cell),
				DungeonTilemap.tileCenterToWorld(cell + i),
				null
			);
		}

    }
}

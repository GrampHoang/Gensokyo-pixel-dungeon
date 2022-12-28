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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.IcyCloudParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;


public class LettySnowball extends WeaponWithSP {

	{
		image = ItemSpriteSheet.AQUA_BLAST;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1f;

		tier = 4;
		DLY = 1.25f;
        RCH = 2;

        chargeGain = 6;
		chargeNeed = 1;
    }

	@Override
	public int max(int lvl) {
		return  Math.round(5f*(tier+1)) + //25 base
				lvl*Math.round(1f*(tier+1)); // no change scale
	}

    @Override
	public int proc(Char attacker, Char defender, int damage) {
        // ((MissileSprite)Dungeon.hero.sprite.parent.recycle( MissileSprite.class )).
        //     reset( attacker.pos, defender.pos, new Orb(), new Callback() {
        //         @Override
        //         public void call() {
        //             return super.proc(attacker, defender, damage);
        //         }
        //     } );
		// Splash.atExplode(defender.pos, 0x368BC1, 12);
		Buff.affect(defender, Chill.class, 3f);
        return super.proc(attacker, defender, damage);
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
    
			Char ch = Actor.findChar(cell);

			Ballistica attack = new Ballistica( Dungeon.hero.pos, cell, Ballistica.PROJECTILE);

			if (attack.collisionPos != cell.intValue()){
				GLog.w(Messages.get(LettySnowball.class, "cannot_hit"));
				return;
			}
            
			if (ch != null){
				Dungeon.hero.busy();
				((MissileSprite)Dungeon.hero.sprite.parent.recycle( MissileSprite.class )).
                    reset(Dungeon.hero.pos, cell, new Orb(), new Callback() {
                        @Override
                        public void call() {
							Splash.atExplode(cell, 0x368BC1, 12);
                            snowballExplode(cell);
		                    CellEmitter.get(cell).burst(IcyCloudParticle.FACTORY, 5);
                            ch.damage(Random.Int(2), this);
							Dungeon.hero.spendAndNext(1f);
                        }
                    } );
				spendSP();
			} else {
				GLog.w(Messages.get(LettySnowball.class, "no_target"));
				return;
			}
			updateQuickslot();
		}

		@Override
		public String prompt() {
			return Messages.get(LettySnowball.class, "prompt");
		}

	};
	
	public void snowballExplode(int cell){
		for (int i : PathFinder.NEIGHBOURS9){
			Char cha = Actor.findChar(cell + i);
			if (cha!= null){
				cha.damage(max()/4, Dungeon.hero);
				Buff.affect(cha, Chill.class, 3f);
			}
			if(!Dungeon.level.solid[cell + i]){
				GameScene.add(Blob.seed(i + cell, 2, Freezing.class));
				((MagicMissile)curUser.sprite.parent.recycle( MagicMissile.class )).reset(
					MagicMissile.FROST,
					DungeonTilemap.tileCenterToWorld(cell),
					DungeonTilemap.tileCenterToWorld(cell + i),
					null
				);
			}
		}
	}
    public class Orb extends Item {
		{
			image = ItemSpriteSheet.AQUA_BLAST;
		}
	}

}

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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char.Alignment;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MusicFlow;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeilingHand.PunchWave;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;


public class RaikoDrum extends WeaponWithSP {

	{
		image = ItemSpriteSheet.RAIKO_DRUM;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1f;

		tier = 4;
		DLY = 0.5f;

        chargeGain = 4;
		usesTargeting = false;
    }

    protected int drum_count = 0;
    
	@Override
	public int max(int lvl) {
		return  Math.round(3f*(tier)) + //12 base
				lvl*Math.round(0.5f*(tier+1)); // 2.5 instead of 5 per level
	}

    protected int skillDamage(){
        return Random.IntRange(min(), max());
    }
    
    @Override
	public int proc(Char attacker, Char defender, int damage) {
        PunchWave.blast(defender.pos);
        defender.sprite.centerEmitter().start( Speck.factory( Speck.NOTE ), 0.2f, 2 );
        //Aoe 2 distance around you
        for (int i : PathFinder.NEIGHBOURS24){
            Char ch = Actor.findChar(attacker.pos + i);
            //Exist and not same alignment, not the current target
            if (ch != null && ch.alignment != attacker.alignment && ch != defender){
                ch.damage(skillDamage(), attacker);
                ch.sprite.centerEmitter().start( Speck.factory( Speck.NOTE ), 0.2f, 3 );
                PunchWave.blast(ch.pos);
            }
        }
        return super.proc(attacker, defender, damage);
	}

    @Override
	protected boolean useSkill(){
        Dungeon.hero.busy();
        //Stack speed
        Dungeon.hero.sprite.centerEmitter().start( Speck.factory( Speck.NOTE ), 0.3f, 4 );
        //Damage enemy in 8 tiles range
        drum_count = 0;
        drumSFX();
        return true;
    }

    @Override
    public String skillInfo(){
		return Messages.get(this, "skill_desc", chargeGain, chargeNeed, max());
	}

    protected void drumSFX(){
        PointF drum_from = Dungeon.hero.sprite.center();	{drum_from.y -= Random.IntRange(16,32);}
		PointF drum_to   = Dungeon.hero.sprite.center();
		drum_count++;
		int cell;
		do {cell = PathFinder.NEIGHBOURS24[Random.Int(0, 23)] + Dungeon.hero.pos;}
		while (cell < 0 || cell > Dungeon.level.map.length || Dungeon.level.solid[cell] == true);
		// GLog.w("cell:" + Integer.toString(cell));
		WandOfBlastWave.BlastWave.blast(cell);
        CellEmitter.get(cell).start( Speck.factory( Speck.NOTE ), 0.2f, 3 );
		if (drum_count < 10){
			((MagicMissile)Dungeon.hero.sprite.parent.recycle( MagicMissile.class )).reset(
				MagicMissile.FORCE_CONE, 
				drum_from, 
				drum_to, 
				new Callback(){
					@Override
					public void call(){
						drumSFX();
					}
				}
			);
			// Dungeon.hero.sprite.zap(cell, null);
		} else {
			drum_count = 0;
			drumDamage();
		}
    }

    protected void drumDamage(){
        PointF drum_from = Dungeon.hero.sprite.center();	{drum_from.y -= 32;}
		PointF drum_to   = Dungeon.hero.sprite.center();
        ((MagicMissile)Dungeon.hero.sprite.parent.recycle( MagicMissile.class )).reset(
				MagicMissile.FORCE_CONE, 
				drum_from, 
				drum_to, 
				new Callback(){
					@Override
					public void call(){
						for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
                            if (mob.alignment != Alignment.ALLY && Dungeon.level.distance(mob.pos, Dungeon.hero.pos) < 9) {
                                PunchWave.blast(mob.pos);
                                mob.damage(max(), Dungeon.hero);
                            }
                        }
                        Dungeon.hero.spendAndNext(1f);
					}
				}
		);
    }
}

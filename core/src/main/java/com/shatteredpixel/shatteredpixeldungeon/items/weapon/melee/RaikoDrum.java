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
		DLY = 1f;

        chargeGain = 10;
    }

    protected int drum_count = 0;

    //For MusicFlow on Mob, chec Mob.attackdelay
    @Override
    public float delayFactor(Char user) {
        if (user instanceof Hero && Dungeon.hero.buff(MusicFlow.class) != null)
            return 1/(Dungeon.hero.buff(MusicFlow.class).getSpeedBuff());
        return 1;
    }
    
	@Override
	public int max(int lvl) {
		return  Math.round(4f*(tier+1)) + //20 base
				lvl*Math.round(1f*(tier)); // 4 instead of 5 per level
	}

    @Override
	public int proc(Char attacker, Char defender, int damage) {
        //Stack speed
        if (attacker.buff(MusicFlow.class) == null){
            Buff.prolong( attacker, MusicFlow.class, MusicFlow.DURATION).increaseFlow(1);
        }
        else{
            MusicFlow flow = attacker.buff(MusicFlow.class);
            flow.increaseFlow(1);
            Buff.prolong( attacker, MusicFlow.class, MusicFlow.DURATION );
        }
        attacker.sprite.centerEmitter().start( Speck.factory( Speck.NOTE ), 0.3f, 4 );

        //Aoe 2 distance around you
        int damageDo = super.proc(attacker, defender, damage);
        int count = 0;
        for (int i : PathFinder.NEIGHBOURS24){
            Char ch = Actor.findChar(attacker.pos + i);
            //Exist and not same alignment, not the current target
            if (ch != null && ch.alignment != attacker.alignment && ch != defender){
                count++;
                ch.damage(damageDo, attacker);
                ch.sprite.centerEmitter().start( Speck.factory( Speck.NOTE ), 0.2f, 3 );
                PunchWave.blast(ch.pos);
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
        //Stack speed
		if (Dungeon.hero.buff(MusicFlow.class) == null){
            Buff.prolong( Dungeon.hero, MusicFlow.class, MusicFlow.DURATION).increaseFlow(5);
        }
        else{
            MusicFlow flow = Dungeon.hero.buff(MusicFlow.class);
            flow.increaseFlow(5);
            Buff.prolong( Dungeon.hero, MusicFlow.class, MusicFlow.DURATION );
        }
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
		GLog.w(Integer.toString(drum_count));
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

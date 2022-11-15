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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.*;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Reviving;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MokouSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Mokou extends Mob {
	
	{
		spriteClass = MokouSprite.class;
		
		HP = HT = 60; //But have at least 2 lives
		defenseSkill = 15;
		
		EXP = 12;
		maxLvl = 22;

		loot = Random.oneOf(Generator.Category.WEAPON, Generator.Category.ARMOR);
		lootChance = 0.125f; //initially, see lootChance()

        immunities.add(Fire.class);
        immunities.add(Burning.class);
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 25, 30 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 28;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 12);
	}

	@Override
	public float lootChance() {
		//each drop makes future drops 1/2 as likely
		// so loot chance looks like: 1/8, 1/16, 1/32, 1/64, etc.
		return super.lootChance() * (float)Math.pow(1/2f, Dungeon.LimitedDrops.GOLEM_EQUIP.count);
	}

	@Override
	public void rollToDropLoot() {
		Imp.Quest.process( this );
		super.rollToDropLoot();
	}

	public Item createLoot() {
		Dungeon.LimitedDrops.GOLEM_EQUIP.count++;
		//uses probability tables for demon halls
		if (loot == Generator.Category.WEAPON){
			return Generator.randomWeapon(5);
		} else {
			return Generator.randomArmor(5);
		}
	}

    private int skill_cd = -1;
	private int revive_count = 0;
	private static final String REVIVE_COUNT = "revive_count";
    private static final String SKILL_COOLDOWN = "skill_cooldown";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(REVIVE_COUNT, revive_count);
        bundle.put(SKILL_COOLDOWN, skill_cd);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		revive_count = bundle.getInt( REVIVE_COUNT );
        skill_cd = bundle.getInt( SKILL_COOLDOWN );
	}

	@Override
	protected boolean act() {
        skill_cd--;
        if( skill_cd == 0){
            revive_bomb();
        }
		return super.act();
	}

    @Override
	public void die(Object cause) {
		if ((Random.IntRange(0, 99) < (100/(revive_count+1)) && isLunatic())    
            //if Lunatic, each revive reduce next reviev chance by half
            || revive_count < 1){
            skill_cd = Reviving.DOWN_TIME*2;
			Buff.affect(this, Reviving.class, Reviving.DOWN_TIME);
            revive_count++;
            ((MokouSprite)this.sprite).crumple();
		} else {
		super.die(cause);
		}
	}

    public void revive_bomb() {
        Sample.INSTANCE.play( Assets.Sounds.BLAST );
        CellEmitter.get(this.pos).burst(BlastParticle.FACTORY, 60);
        PathFinder.buildDistanceMap( this.pos, BArray.not( Dungeon.level.solid, null ), 2 );
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE && Random.IntRange(0, 1) == 0) {
                CellEmitter.get(i).burst(SmokeParticle.FACTORY, 10);
                //avoid items
                Heap heap = Dungeon.level.heaps.get(i);
                Char ch = Actor.findChar(i);
                if(heap == null){
                    GameScene.add(Blob.seed(i, 5, Fire.class));
                }
                if (ch != null && !(ch instanceof Mokou)){
                    ch.damage(20, this);
                }
            }
        }
	}
}

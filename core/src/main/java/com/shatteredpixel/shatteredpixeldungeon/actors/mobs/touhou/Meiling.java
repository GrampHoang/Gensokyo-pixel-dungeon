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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MeilingSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeilingHand.PunchWave;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EarthParticle;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.particles.Emitter;

import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PathFinderUtils;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Meiling extends Mob {

	{
		spriteClass = MeilingSprite.class;

		HP = HT = 25;
		defenseSkill = 12;
		viewDistance = 8;

		EXP = 5;
        maxLvl = 10;

		loot = Generator.Category.WEAPON;
		lootChance = 0.1667f; //by default, see lootChance()

		baseSpeed = 1f;
		if (isLunatic()){
			immunities.add(Sleep.class);
			immunities.add(MagicalSleep.class);
		}; 
	}

	private float ROCK_COOLDOWN = (isLunatic() ? 50 : 100);
	private float PUNCH_COOLDOWN = (isLunatic() ? 10 : 20); 
    private float rock_cd = 30;
	private float punch_cd = 5;
	private int[] aim_pos = {-1,-1,-1};
	private boolean near = false;

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 3, 8 );
	}

	@Override
	public int attackSkill( Char target ) {
		return 12;
	}

	// @Override
	// public float attackDelay() {
	// 	return super.attackDelay();
	// }

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 2);
	}

    private static final String ROCK_CD = "rock_cd";
    private static final String PUNCH_CD = "punch_cd";
	private static final String AIMPOS = "aimpos";
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
        bundle.put(ROCK_CD, rock_cd);
        bundle.put(PUNCH_CD, punch_cd);
		bundle.put(AIMPOS, aim_pos);
		bundle.put("NEAR", near);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
        punch_cd = bundle.getFloat(PUNCH_CD);
        rock_cd = bundle.getFloat(ROCK_CD);
		aim_pos = bundle.getIntArray(AIMPOS);
		near = bundle.getBoolean("NEAR");
	}

	@Override
	protected boolean act() {
		if (canUseAbility()){
			spend(1f);
			return useAbility();
		}
		return super.act();
	}

    //SKILL

	public boolean canUseAbility(){
		if(enemySeen == false){
			return false;
		}
        if(Dungeon.hero.buff(Paralysis.class) != null){
            return false;
        }
        if (punch_cd < 2){
            return true;
        } else if (rock_cd < 1){
            return true;
        } else {
            punch_cd--;
            rock_cd--;
            return false;
        }
	}

	public boolean useAbility(){
		if(enemySeen == false){
			return false;
		}
		if(rock_cd < 1){
            rock_cd = ROCK_COOLDOWN;
			return dropRocks(Dungeon.hero);
		} else if (punch_cd == 1){
			punch_cd--;
			return punchAim(Dungeon.hero);
		}else {
            punch_cd = PUNCH_COOLDOWN;
			return punchShot();
		}
	}

	public boolean punchAim( Char target ) {
        if (Dungeon.level.distance(pos, target.pos) < 2){
			near = true;
			for (int i : PathFinder.NEIGHBOURS8){
				this.sprite.parent.addToBack(new TargetedCell(i + this.pos, 0xFF0000));
			}
		} else {
			aim_pos = PathFinderUtils.random_two_opposite_cell(target.pos);
			for (int aim : aim_pos){
				Ballistica aim_path = new Ballistica(this.pos, aim, Ballistica.STOP_SOLID);
				for (int cel : aim_path.subPath(1, 5)) {
					this.sprite.parent.addToBack(new TargetedCell(cel, 0xFF0000));
				}
			}
		}
        return true;
    }

    public boolean punchShot(){
		if (near == true){
			near = false;
			for (int i : PathFinder.NEIGHBOURS8){
				punchImpact(i + this.pos);
			}
		} else {
			for (int aim : aim_pos) {
				if(aim == -1) break; //Just in case
				Ballistica aim_path = new Ballistica(this.pos, aim, Ballistica.STOP_SOLID);
				for (int c : aim_path.subPath(1, 5)) {
					punchImpact(c);
				}
			}
		}
        return true;
    }

	public void punchImpact(int cell){
		PunchWave.blast(cell);
		CellEmitter.get(cell).start( Speck.factory( Speck.ROCK ), 0.1f, 10 );
		Char ch = Actor.findChar(cell);
		if (ch != null && !(ch instanceof Meiling)){
			Buff.prolong( ch, Paralysis.class, 2);
			if(ch.alignment != this.alignment) ch.damage(6, this);
		}
	}

    public boolean dropRocks( Char target ) {
		WandOfBlastWave.BlastWave.blast(this.pos);
		Dungeon.hero.interrupt();
		final int rockCenter;
        if (Dungeon.level.adjacent(pos, target.pos)){
			int oppositeAdjacent = target.pos + (target.pos - pos);
			Ballistica trajectory = new Ballistica(target.pos, oppositeAdjacent, Ballistica.MAGIC_BOLT);
			WandOfBlastWave.throwChar(target, trajectory, 2, false, false, getClass());
			if (target == Dungeon.hero){
				Dungeon.hero.interrupt();
			}
			rockCenter = trajectory.path.get(Math.min(trajectory.dist, 2));
		} else {
			rockCenter = target.pos;
		}

		int safeCell;
		do {
			safeCell = rockCenter + PathFinder.NEIGHBOURS8[Random.Int(8)];
		} while (safeCell == pos
				|| (Dungeon.level.solid[safeCell] && Random.Int(2) == 0));

		ArrayList<Integer> rockCells = new ArrayList<>();

		int start = rockCenter - Dungeon.level.width() * 3 - 3;
		int pos;
		for (int y = 0; y < 7; y++) {
			pos = start + Dungeon.level.width() * y;
			for (int x = 0; x < 7; x++) {
				if (!Dungeon.level.insideMap(pos)) {
					pos++;
					continue;
				}
				//add rock cell to pos, if it is not solid, and isn't the safecell
				if (!Dungeon.level.solid[pos] && pos != safeCell && Random.Int(Dungeon.level.distance(rockCenter, pos)) == 0) {
					//don't want to overly punish players with slow move or attack speed
					rockCells.add(pos);
				}
				pos++;
			}
		}
		Buff.append(this, FallingRockBuff.class, Math.min(target.cooldown(), 3*TICK)).setRockPositions(rockCells);
        return true;
	}

    public static class FallingRockBuff extends FlavourBuff {

		private int[] rockPositions;
		private ArrayList<Emitter> rockEmitters = new ArrayList<>();

		public void setRockPositions( List<Integer> rockPositions ) {
			this.rockPositions = new int[rockPositions.size()];
			for (int i = 0; i < rockPositions.size(); i++){
				this.rockPositions[i] = rockPositions.get(i);
			}

			fx(true);  
		}

		@Override
		public boolean act() {
			for (int i : rockPositions){
				CellEmitter.get( i ).start( Speck.factory( Speck.ROCK ), 0.07f, 10 );

				Char ch = Actor.findChar(i);
				if (ch != null && !(ch instanceof Meiling)){
					Buff.prolong( ch, Paralysis.class, 2);
				}
			}

			Camera.main.shake( 3, 0.7f );
			Sample.INSTANCE.play(Assets.Sounds.ROCKS);

			detach();
			return super.act();
		}

		@Override
		public void fx(boolean on) {
			if (on && rockPositions != null){
				for (int i : this.rockPositions){
                    Dungeon.hero.sprite.parent.addToBack(new TargetedCell(i, 0xFF0000));
					Emitter e = CellEmitter.get(i);
					e.y -= DungeonTilemap.SIZE*0.2f;
					e.height *= 0.4f;
					e.pour(EarthParticle.FALLING, 0.1f);
					rockEmitters.add(e);
				}
			} else {
				for (Emitter e : rockEmitters){
					e.on = false;
				}
			}
		}

		private static final String POSITIONS = "positions";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(POSITIONS, rockPositions);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			rockPositions = bundle.getIntArray(POSITIONS);
		}
	}
	
	@Override
	public float lootChance() {
		//each drop makes future drops 1/2 as likely
		// so loot chance looks like: 1/6, 1/12, 1/24, 1/48, etc.
		return super.lootChance() * (float)Math.pow(1/2f, Dungeon.LimitedDrops.SKELE_WEP.count);
	}

	@Override
	public Item createLoot() {
		Dungeon.LimitedDrops.SKELE_WEP.count++;
		return super.createLoot();
	}
}

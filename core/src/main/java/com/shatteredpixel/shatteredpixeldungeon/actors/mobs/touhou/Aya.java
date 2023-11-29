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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.AquaBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GeyserTrap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SmokeScreen;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.sprites.AyaNPCSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ChenSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.NitoriSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class Aya extends Mob {

	{	
		spriteClass = AyaNPCSprite.class;
		HP = HT = 150;
		defenseSkill = 26;
		EXP = 14;
		maxLvl = 30;

		flying = true;

        loot = Generator.Category.POTION;
		lootChance = 0.3f;
	}

    private int ROLL_CD = 20;
	private int roll_cd = ROLL_CD;
	protected boolean rolling = false;
	protected int enemy_pos = this.pos; //just to be safe

	private static final String ROLL_COOLDOWN = "roll_cooldown";
	private static final String ROLLING = "rolling";
	private static final String ENEMY_POS = "enemy_pos";
	


	@Override
	public int damageRoll() {
		return Random.NormalIntRange(35, 45);
	}

	@Override
	public int attackSkill(Char target) {
		return 37;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(4, 18);
	}

	@Override
	public void die(Object cause) {
		super.die(cause);
	}

    @Override
	public int attackProc(Char enemy, int damage) {
		return super.attackProc(enemy, damage);
	}

    @Override
	public void damage(int dmg, Object src) {
        super.damage(dmg, src);
        if (src instanceof MissileWeapon){
			GLog.w("MISSLEWEAPON");
		}
	}

    //rolling cuz I copy code from Chen
    @Override
	protected boolean act() {
        if(rolling == true){
            rolling = false;
            roll_cd --;
            spend(TICK);
            return roll(enemy_pos);
        } else if (roll_cd <= 1 && enemy != null && rolling == false){
            rolling = true;
            return true;
        }
        roll_cd--;
		return super.act();
	}

	protected int ready(int target){
        this.sprite.add(CharSprite.State.LEVITATING);
		// CellEmitter.center(this.pos).burst(RainbowParticle.BURST, 20);
        Ballistica b = new Ballistica(this.pos, target, Ballistica.STOP_SOLID);
		//Make sure she won't roll into pit
		while(Dungeon.level.pit[b.collisionPos] && b.collisionPos != this.pos){
			b.collisionPos = b.path.get(b.path.indexOf(b.collisionPos) - 1);
		}
        for (int p : b.subPath(0, Dungeon.level.distance(this.pos, b.collisionPos))){
            sprite.parent.add(new TargetedCell(p, 0xFF0000));
        }
        GLog.w("Chen is spining!");
		return b.collisionPos;
	}

	protected boolean roll(int stopCell) {
		//push char
		Char cha = Actor.findChar(stopCell);
		int push_pos = this.pos;
		if (cha != null && cha != this){
			for (int i : PathFinder.NEIGHBOURS8){
				if (Actor.findChar(stopCell + i) == null && Dungeon.level.passable[stopCell + i]){
					push_pos = stopCell+i;
					break;
				}
			}
			Actor.addDelayed(new Pushing(cha, cha.pos, push_pos), 0);
			// ch.moveSprite(ch.pos, push_pos);
			cha.move(push_pos);
			Dungeon.level.occupyCell(cha);
		}
		//roll
        sprite.parent.add(new Beam.DeathRay(sprite.center(), DungeonTilemap.raisedTileCenterToWorld(stopCell)));
		Ballistica b = new Ballistica(this.pos, stopCell, Ballistica.STOP_SOLID);
		for (int p : b.subPath(0, Dungeon.level.distance(this.pos, stopCell))){
            Char ch = Actor.findChar(p);
			if (ch != null && !(ch instanceof Chen)){
				ch.damage(18, this);
			}
        }
		//move
		this.move( stopCell);
        this.moveSprite(this.pos, stopCell);
        return true;
	}
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( ROLL_COOLDOWN, roll_cd );
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		roll_cd = bundle.getInt( ROLL_COOLDOWN );
	}

}

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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.*;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.AliceSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Youmu extends Mob {
	{
		spriteClass = AliceSprite.class;

		HP = HT = 80;
		defenseSkill = 12;

		EXP = 9;
		maxLvl = 17;

		loot = Random.oneOf(Generator.Category.WEAPON, Generator.Category.ARMOR);
		lootChance = 0.125f; //initially, see lootChance()
	}


	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 10, 25 );
	}

	@Override
	public int attackSkill( Char target ) {
		return 200;
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 8);
	}

	@Override
	public float lootChance(){
		//each drop makes future drops 1/2 as likely
		// so loot chance looks like: 1/8, 1/16, 1/32, 1/64, etc.
		return super.lootChance() * (float)Math.pow(1/2f, Dungeon.LimitedDrops.DM200_EQUIP.count);
	}

	public Item createLoot() {
		Dungeon.LimitedDrops.DM200_EQUIP.count++;
		//uses probability tables for dwarf city
		if (loot == Generator.Category.WEAPON){
			return Generator.randomWeapon(4);
		} else {
			return Generator.randomArmor(4);
		}
	}

	@Override
	public int defenseProc( Char enemy, int damage ) {		
		return super.defenseProc(enemy, damage);
	}

	@Override
	public void damage(int dmg, Object src) {
		if (ready){
			sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "deflect"));
			return;
		}
		super.damage(dmg, src);
	}

	private int SKILL_CD = (isLunatic() ? 10 : 20);
	private int skill_cd = SKILL_CD;
	private boolean ready = false;
	private int skill_type = 1;
	private int dash_pos = this.pos+1;

    @Override
	protected boolean act() {
		if (ready == true){
			switch(skill_type){
				case 1:
					//dash
					Ballistica b = new Ballistica(this.pos, dash_pos, Ballistica.STOP_SOLID);
					for (int p : b.subPath(0, Dungeon.level.distance(this.pos, dash_pos))){
						Char ch = Actor.findChar(p);
						if(ch != null && ch.alignment != this.alignment){
							ch.damage(20, this);
						}
					}
					//effect
					sprite.parent.add(new Beam.YoumuSlash(sprite.center(), DungeonTilemap.raisedTileCenterToWorld(dash_pos)));
					while(Dungeon.level.pit[dash_pos] && dash_pos != this.pos){
						dash_pos = b.path.get(b.path.indexOf(dash_pos) - 1);
					}
					//push char
					Char ch = Actor.findChar(dash_pos);
					int push_pos = this.pos;
					if (ch != null){
						for (int i : PathFinder.NEIGHBOURS8){
							if (Actor.findChar(dash_pos + i) == null && Dungeon.level.passable[dash_pos + i]){
								push_pos = dash_pos+i;
								break;
							}
						}
						Actor.addDelayed(new Pushing(ch, ch.pos, push_pos), 0);
						// ch.moveSprite(ch.pos, push_pos);
						ch.move(push_pos);
						Dungeon.level.occupyCell(ch);
					}
					//move Youmu
					this.moveSprite(this.pos, dash_pos);
					this.move(dash_pos);
					Dungeon.level.occupyCell(Youmu.this);
					
					break;
				default:
				//spin
					for (int p : PathFinder.NEIGHBOURS8){
						Char cha = Actor.findChar(p + this.pos);
							if(cha != null && cha.alignment != this.alignment){
								cha.damage(20, this);
							}
					}
					//effect
					sprite.parent.add(new Beam.YoumuSlash(DungeonTilemap.raisedTileCenterToWorld(this.pos + PathFinder.NEIGHBOURS_TOPLEFT), DungeonTilemap.raisedTileCenterToWorld(this.pos + PathFinder.NEIGHBOURS_TOPRIGHT)));
					sprite.parent.add(new Beam.YoumuSlash(DungeonTilemap.raisedTileCenterToWorld(this.pos + PathFinder.NEIGHBOURS_TOPLEFT), DungeonTilemap.raisedTileCenterToWorld(this.pos + PathFinder.NEIGHBOURS_BOTLEFT)));
					sprite.parent.add(new Beam.YoumuSlash(DungeonTilemap.raisedTileCenterToWorld(this.pos + PathFinder.NEIGHBOURS_BOTRIGHT), DungeonTilemap.raisedTileCenterToWorld(this.pos + PathFinder.NEIGHBOURS_TOPRIGHT)));
					sprite.parent.add(new Beam.YoumuSlash(DungeonTilemap.raisedTileCenterToWorld(this.pos + PathFinder.NEIGHBOURS_BOTRIGHT), DungeonTilemap.raisedTileCenterToWorld(this.pos + PathFinder.NEIGHBOURS_BOTLEFT)));
					break;
			}
			skill_cd = SKILL_CD;
			if(isLunatic() && Random.IntRange(0,2) == 1){
				skill_cd = 1;
			}
			ready = false;
			spend(TICK);
			return true;
		} else if( skill_cd <= 1 && ready == false && enemySeen){
			ready = true;
			skill_type = Random.IntRange(0,1);
			//always dash if too far
			if(enemySeen && Dungeon.level.distance(enemy.pos, this.pos) > 2){
				skill_type = 1;
			}
			switch(skill_type){
				case 1:
					//dash
					dash_pos = enemy.pos;
					Ballistica b = new Ballistica(this.pos, dash_pos, Ballistica.STOP_SOLID);
					for (int p : b.subPath(0, Dungeon.level.distance(this.pos, dash_pos))){
						sprite.parent.add(new TargetedCell(p, 0xFF0000));
					}
					break;
				default:
					//spin
					for (int p : PathFinder.NEIGHBOURS8){
						sprite.parent.add(new TargetedCell(p+this.pos, 0xFF0000));
					}
					break;
			}
			spend(TICK);
			return true;
		}
		skill_cd--;
		return super.act();
	}

	private static final String SKILL_COOLDOWN = "skill_cooldown";
	private static final String SKILL_TYPE = "skill_type";
	private static final String DASH_POS = "dash_pos";
	private static final String READY = "ready";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( SKILL_COOLDOWN, skill_cd );
		bundle.put( SKILL_TYPE, skill_type );
		bundle.put( DASH_POS, dash_pos );
		bundle.put( READY, ready );
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		skill_cd = bundle.getInt( SKILL_COOLDOWN );
		skill_type = bundle.getInt( SKILL_TYPE );
		dash_pos = bundle.getInt( DASH_POS );
		ready = bundle.getBoolean( READY );
	}
}

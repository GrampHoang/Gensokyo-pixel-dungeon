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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.LilyFlower;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.LilySprite;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.Iterator;

public class Lily extends Mob {
	{
		spriteClass = LilySprite.class;
		HP = HT = 20;
		defenseSkill = 1;
		EXP = 5;
		maxLvl = 12;
        loot =  PotionOfHealing.class;
		lootChance = 0.33f;
        
	}

    protected boolean charging_skill = false;
    private ArrayList<Integer> lilySkillCells = new ArrayList<>();
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange(2, 4);
	}

	@Override
	public int attackSkill(Char target) {
		return 5;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 4);
	}

    @Override
	protected boolean act() {
        if (charging_skill == true && !lilySkillCells.isEmpty()){
			lilySkill();
            return true;
        } else if(Random.IntRange(0, (isLunatic() ? 4 : 8)) == 1 && enemySeen == true){
			lilyCharge();
			charging_skill = true;
			return true;
		}
		return super.act();
	}

	@Override
	public void die(Object cause) {
		if(Random.Int(500) == 1){
			Dungeon.level.drop( new LilyFlower(), pos ).sprite.drop();
		}
		super.die(cause);
	}

    private void lilySkill(){
		int[] ret = new int[lilySkillCells.size()];
		for (int i=0; i < ret.length; i++)
		{
			ret[i] = lilySkillCells.get(i).intValue();
		}
		for (int p : ret){
			((MagicMissile)this.sprite.parent.recycle( MagicMissile.class )).reset(
				MagicMissile.FOLIAGE,
				this.sprite.center(),
				DungeonTilemap.tileCenterToWorld(p),
				new Callback() {
					@Override
					public void call() {
						Ballistica rand = new Ballistica(pos, p, Ballistica.STOP_SOLID);
						for (int cell : rand.subPath(1, Dungeon.level.distance(pos, rand.collisionPos))){
							//Deal with Char
							Char ch = Actor.findChar(cell);
							if (ch != null) {
								if(ch.alignment != alignment){
									ch.damage(3, this);
									Buff.prolong(ch, Roots.class, 1f);
								} else {
									if (ch.HP + 3 > ch.HT){
										Buff.prolong(ch, Stamina.class, 2f);
										ch.sprite.showStatus(CharSprite.POSITIVE, Integer.toString(ch.HT-ch.HP));
										ch.HP = ch.HT;
									} else {
										ch.HP += 3;
										ch.sprite.showStatus(CharSprite.POSITIVE, Integer.toString(3));
									}
								}
							}
							//Effect
							if (growableCell(cell)){
								Level.set(cell, Terrain.HIGH_GRASS);
								Level.set(cell, Terrain.FURROWED_GRASS);
								GameScene.updateMap( cell );
							};
						}
					}
				} );

			charging_skill = false;
			lilySkillCells.clear();
		}

		for (int p : lilySkillCells){
			Char ch = Actor.findChar(p);
            if (ch != null && ch != this) {
				if(ch.alignment != this.alignment){
					ch.damage(3, this);
					Buff.prolong(ch, Roots.class, 2f);
				} else {
					Buff.prolong(ch, Stamina.class, 2f);
					ch.HP += 3;
				}
            }
		}
		spend(TICK);
    }

	public boolean growableCell(int cell){
		int terr = Dungeon.level.map[cell];
		if (!(terr == Terrain.EMPTY || terr == Terrain.EMBERS || terr == Terrain.EMPTY_DECO ||
				terr == Terrain.GRASS || terr == Terrain.FURROWED_GRASS)) {
			return false;
		} else if (Char.hasProp(Actor.findChar(cell), Char.Property.IMMOVABLE)) {
			return false;
		} else if (Dungeon.level.plants.get(cell) != null){
			return false;
		} 
		return true;
	}

	//Remove cells that aren't suitable to grow grasses
	public ArrayList<Integer> growableCells(ArrayList<Integer> skillCells){
		for (Iterator<Integer> i = skillCells.iterator(); i.hasNext();) {
			int cell = i.next();
			int terr = Dungeon.level.map[cell];
			if (!(terr == Terrain.EMPTY || terr == Terrain.EMBERS || terr == Terrain.EMPTY_DECO ||
					terr == Terrain.GRASS || terr == Terrain.FURROWED_GRASS)) {
				i.remove();
			} else if (Char.hasProp(Actor.findChar(cell), Char.Property.IMMOVABLE)) {
				i.remove();
			} else if (Dungeon.level.plants.get(cell) != null){
				i.remove();
			} 
		}
		return skillCells;
	}

    private void lilyCharge(){
        for (int i : PathFinder.NEIGHBOURS8){
			if (Random.IntRange(1, 8) == 2){
				Ballistica rand = new Ballistica(this.pos, this.pos+i, Ballistica.STOP_SOLID);
				lilySkillCells.add(rand.collisionPos);
				for (int p : rand.subPath(0, Dungeon.level.distance(this.pos, rand.collisionPos))){
					sprite.parent.add(new TargetedCell(p, 0x457462));
				}
			}
		}

		if (!lilySkillCells.contains(Dungeon.hero.pos)){
			Ballistica rand = new Ballistica(this.pos, Dungeon.hero.pos, Ballistica.STOP_SOLID);
			lilySkillCells.add(rand.collisionPos);
			for (int p : rand.subPath(0, Dungeon.level.distance(this.pos, rand.collisionPos))){
				sprite.parent.add(new TargetedCell(p, 0x457462));
			}
		}
		spend(TICK);
    }

    @Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( "lilycharingskill", charging_skill );

		int[] bundleArr_3 = new int[lilySkillCells.size()];
		for (int i = 0; i < lilySkillCells.size(); i++){
			bundleArr_3[i] = lilySkillCells.get(i);
		}
		bundle.put("lilyskillcells", bundleArr_3);
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		charging_skill = bundle.getBoolean( "lilycharingskill" );

		for (int i : bundle.getIntArray("lilyskillcells")){
			lilySkillCells.add(i);
		}
	}

	@Override
	public float lootChance() {
		return super.lootChance() * (3f - Dungeon.LimitedDrops.SWARM_HP.count) / 3f;
	}
	
	@Override
	public Item createLoot(){
		Dungeon.LimitedDrops.SWARM_HP.count++;
		return super.createLoot();
	}
}

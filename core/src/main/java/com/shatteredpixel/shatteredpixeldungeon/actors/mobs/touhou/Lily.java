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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.LilyFlower;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.LilySprite;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.watabou.utils.Bundle;
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
		return Random.NormalIntRange(2, 5);
	}

	@Override
	public int attackSkill(Char target) {
		return 5;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(2, 5);
	}

    @Override
	protected boolean act() {
        if (charging_skill == true){
            return lilySkill();
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

    private boolean lilySkill(){
		for (int p : lilySkillCells){
			Char ch = Actor.findChar(p);
            if (ch != null && ch != this) {
				if(ch.alignment != this.alignment){
					ch.damage(3, this);
					Buff.affect(ch, Roots.class, 1f);
				} else {
					Buff.prolong(ch, Stamina.class, 2f);
					ch.HP += 3;
				}
            }
		}
		//remove some cell to grow grass
		lilySkillCells = growableCells(lilySkillCells);
        for (int p : lilySkillCells){	
			Level.set(p, Terrain.HIGH_GRASS);
			Level.set(p, Terrain.FURROWED_GRASS);
			GameScene.updateMap( p );
		}
		charging_skill = false;
		lilySkillCells.clear();
		// spend(TICK);
        return true;
    }

	//Remove cells that aren't suitable
	public ArrayList<Integer> growableCells(ArrayList<Integer> skillCells){
		for (Iterator<Integer> i = skillCells.iterator(); i.hasNext();) {
			int cell = i.next();
			int terr = Dungeon.level.map[cell];
			if (!(terr == Terrain.EMPTY || terr == Terrain.EMBERS || terr == Terrain.EMPTY_DECO ||
					terr == Terrain.GRASS || terr == Terrain.HIGH_GRASS || terr == Terrain.FURROWED_GRASS)) {
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
        // Ballistica forward = new Ballistica(this.pos, Dungeon.hero.pos, Ballistica.PROJECTILE);
		// for (int p : forward.subPath(0, Dungeon.level.distance(this.pos, forward.collisionPos))){
		// 	sprite.parent.add(new TargetedCell(p, 0xFF0000));
		// 	lilySkillCells.add(p);
		// }
        for (int i : PathFinder.NEIGHBOURS8){
			if (Random.IntRange(1, 8) == 2){
				Ballistica rand = new Ballistica(this.pos, this.pos+i, Ballistica.MAGIC_BOLT);
				// MagicMissile.boltFromChar( this.sprite.parent,
				// MagicMissile.FOLIAGE_CONE,
				// this.sprite,
				// rand.collisionPos,
				// new Callback() {
				// 	@Override
				// 	public void call() {
				// 		// Do nothing
				// 	}
				// } );
				
				for (int p : rand.subPath(0, Dungeon.level.distance(this.pos, rand.collisionPos))){
					sprite.parent.add(new TargetedCell(p, 0xFF0000));
					lilySkillCells.add(p);
				}
			}
		}

		Ballistica aim = new Ballistica(this.pos, Dungeon.hero.pos, Ballistica.MAGIC_BOLT);
		for (int p : aim.subPath(0, Dungeon.level.distance(this.pos, aim.collisionPos))){
			sprite.parent.add(new TargetedCell(p, 0xFF0000));
			lilySkillCells.add(p);
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

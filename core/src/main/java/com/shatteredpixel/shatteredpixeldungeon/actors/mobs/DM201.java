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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.MetalShard;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DM201Sprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.watabou.noosa.Camera;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class DM201 extends DM200 {

	{
		spriteClass = DM201Sprite.class;

		HP = HT = 120;

		properties.add(Property.IMMOVABLE);

		HUNTING = new Mob.Hunting();
	}

	private final int ROCKNADE_CD = 15;
	private int rocknade_cd = ROCKNADE_CD;
	private int rocknade_stop_pos = 0;
	private ArrayList<Integer> rocknadeCells = new ArrayList<>();

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 15, 25 );
	}

	private boolean threatened = false;

	@Override
	protected boolean act() {

		//in case DM-201 hasn't been able to act yet
		if (fieldOfView == null || fieldOfView.length != Dungeon.level.length()){
			fieldOfView = new boolean[Dungeon.level.length()];
			Dungeon.level.updateFieldOfView( this, fieldOfView );
		}

		if (paralysed <= 0 && state == HUNTING && enemy != null && enemySeen
				&& threatened && !Dungeon.level.adjacent(pos, enemy.pos) && fieldOfView[enemy.pos]){
			enemySeen = enemy.isAlive() && fieldOfView[enemy.pos] && enemy.invisible <= 0;
			if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
				sprite.zap( enemy.pos );
				return false;
			} else {
				zap();
				return true;
			}
		}
		if (rocknade_cd > 1){
			rocknade_cd--;
		} else if (rocknade_cd == 1 && enemy != null && enemySeen){
			rocknade_cd--;
			return useReady(enemy);
		} else if (rocknade_cd == 0){
			rocknade_cd = ROCKNADE_CD;
			return rockGrenade();
		}
		return super.act();
	}

	@Override
	public void damage(int dmg, Object src) {
		if ((src instanceof Char && !Dungeon.level.adjacent(pos, ((Char)src).pos))
				|| enemy == null || !Dungeon.level.adjacent(pos, enemy.pos)){
			threatened = true;
		}
		super.damage(dmg, src);
	}

	public void onZapComplete(){
		zap();
		next();
	}

	private void zap( ){
		threatened = false;
		spend(TICK);

		GameScene.add(Blob.seed(enemy.pos, 15, CorrosiveGas.class).setStrength(8));
		for (int i : PathFinder.NEIGHBOURS8){
			if (!Dungeon.level.solid[enemy.pos+i]) {
				GameScene.add(Blob.seed(enemy.pos + i, 5, CorrosiveGas.class).setStrength(8));
			}
		}

	}

	public boolean useReady(Char enemy){
		spend(TICK);
		Ballistica b = new Ballistica(this.pos, enemy.pos, Ballistica.STOP_SOLID);
		rocknade_stop_pos = b.collisionPos;
		for (int p : b.subPath(1, Dungeon.level.distance(this.pos, b.collisionPos))){
			rocknadeCells.add(p);
            sprite.parent.add(new TargetedCell(p, 0xFF0000));
        }
		return true;
	}

	public boolean rockGrenade(){
		sprite.parent.add(new Beam.DeathRay(sprite.center(), DungeonTilemap.raisedTileCenterToWorld(rocknade_stop_pos)));
        for (int i :rocknadeCells){
			Char ch = Actor.findChar(i);
			if(ch != null && (!(ch instanceof DM201))){
                ch.damage(Random.IntRange(8,16), this);
			}
        }
		CellEmitter.center(rocknade_stop_pos).burst(BlastParticle.FACTORY, 30);
		PathFinder.buildDistanceMap( rocknade_stop_pos, BArray.not( Dungeon.level.solid, null ), 1 );
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] < Integer.MAX_VALUE) {
				if (Dungeon.level.pit[i])
					GameScene.add(Blob.seed(i, 1, Fire.class));
				else{
					GameScene.add(Blob.seed(i, 5, Fire.class));
				}
				CellEmitter.get(i).burst(FlameParticle.FACTORY, 5);
				CellEmitter.get(i).burst(SmokeParticle.FACTORY, 4);
				CellEmitter.get(i).start( Speck.factory( Speck.ROCK ), 0.07f, 10 );
				Char ch = Actor.findChar(i);
					if (ch != null && !(ch instanceof DM201)) {
						ch.damage(20, this);
						Buff.affect(ch, Paralysis.class, 3f);
					}
			}
		}
		Camera.main.shake( 2, 0.7f );
		rocknadeCells.clear();
        return true;
    }	


	@Override
	protected boolean getCloser(int target) {
		return true;
	}

	@Override
	protected boolean getFurther(int target) {
		return true;
	}

	@Override
	public void rollToDropLoot() {
		if (Dungeon.hero.lvl > maxLvl + 2) return;

		super.rollToDropLoot();

		int ofs;
		do {
			ofs = PathFinder.NEIGHBOURS8[Random.Int(8)];
		} while (Dungeon.level.solid[pos + ofs] && !Dungeon.level.passable[pos + ofs]);
		Dungeon.level.drop( new MetalShard(), pos + ofs ).sprite.drop( pos );
	}

	private static final String ROCKNADE_COOLDOWN     = "rocknade_cooldown";
	private static final String ROCKNADE_CELLS     = "rocknade_cells";
	private static final String ROCKNADE_STOP_POS     = "rocknade_pos";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( ROCKNADE_COOLDOWN, rocknade_cd );
		bundle.put( ROCKNADE_STOP_POS, rocknade_stop_pos );

		int[] bundleArr = new int[rocknadeCells.size()];
		for (int i = 0; i < rocknadeCells.size(); i++){
			bundleArr[i] = rocknadeCells.get(i);
		}
        bundle.put(ROCKNADE_CELLS, bundleArr);
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		rocknade_cd = bundle.getInt( ROCKNADE_COOLDOWN );
		rocknade_stop_pos = bundle.getInt( ROCKNADE_STOP_POS );

		for (int i : bundle.getIntArray(ROCKNADE_CELLS)){
			rocknadeCells.add(i);
		}
	}

}

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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;

import java.util.ArrayList;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.RainbowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave.BlastWave;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class PatchouliBook extends WeaponWithSP {

	{
		image = ItemSpriteSheet.WOOD_STICK;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1.3f;

		tier = 3;
		DLY = 1;

		ACC = 99;
        RCH = 6;    //extra reach
		chargeGain = 5;
		chargeNeed = 1;
	}

	private ArrayList<Lightning.Arc> arcs = new ArrayList<>();

	@Override
	public int min(int lvl) {
		return  6 +    //6 base,
				lvl*2; //+2 per levl instead of 3
	}

	@Override
	public int max(int lvl) {
		return  6 +    //6 base,
				lvl*2; //+2 per levl instead of 3
	}
	@Override
	public boolean canReach( Char owner, int target){
		Ballistica attack = new Ballistica( owner.pos, target, Ballistica.PROJECTILE);
		return attack.collisionPos == target;
	}

    @Override
	public int proc(Char attacker, Char defender, int damage) {
		arcs.clear();
		PointF from = attacker.sprite.center();
		PointF to = defender.sprite.center();
		arcs.add(new Lightning.Arc(from, to));
		Dungeon.hero.sprite.parent.addToFront( new Lightning( arcs, null ) );
		Sample.INSTANCE.play( Assets.Sounds.ZAP );
		damage = damage/2;
		defender.damage(damage, attacker);
		arcs.clear();
		return super.proc(attacker, defender, damage);
	}

	public void magicAttack(Char attacker, Char defender) {
		defender.damage(max(), attacker);
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

			//CAll LIGHTING
			if (cell != Dungeon.hero.pos && ch != null){
				Dungeon.hero.busy();
				callLightingFall(cell);
				//The delay between lightingfall and aniamtion is just about right for the explosion
				Dungeon.hero.sprite.zap(cell, new Callback(){
					@Override
					public void call() {
						callLightingAoE(cell);
						Dungeon.hero.sprite.parent.addToFront( new Lightning( arcs, new Callback(){
							@Override
							public void call() {
								Dungeon.hero.spendAndNext(1f);
							}
						}));
					}
				});
				spendSP();

			//CALL ICE
			} else if (cell != Dungeon.hero.pos && Dungeon.level.water[cell] == true){
				Dungeon.hero.busy();
				Dungeon.hero.sprite.zap(cell, null);

				CellEmitter.get(cell).burst(SmokeParticle.FACTORY, 8);
				for (int i  : PathFinder.NEIGHBOURS9){
					Char cha = Actor.findChar(cell + i);
					if (cha != null) Buff.prolong(cha, Frost.class, 2f);
					GameScene.add(Blob.seed(i + cell, 1, Freezing.class));
				}
				Sample.INSTANCE.play( Assets.Sounds.HIT_MAGIC, 3, Random.Float(0.87f, 1.15f) );

				Dungeon.hero.spendAndNext(1f);
				spendSP();

			//CALL METEOR
			} else if (cell != Dungeon.hero.pos && Dungeon.level.flamable[cell] == true){
				Dungeon.hero.busy();
				callMeteor(cell);
				Dungeon.hero.sprite.zap(cell, null);
				// Dungeon.hero.spendAndNext(1f);
				spendSP();

			//CALL WIND
			} else if (cell == Dungeon.hero.pos){
				Dungeon.hero.busy();
				Dungeon.hero.sprite.zap(cell, null);
				BlastWave.blast(Dungeon.hero.pos);
				Sample.INSTANCE.play(Assets.Sounds.ZAP);
				for (int i  : PathFinder.NEIGHBOURS8){
					
					Char cha = Actor.findChar(Dungeon.hero.pos + i);
		
					if (cha != null){
						if (cha.pos == Dungeon.hero.pos + i) {
							Ballistica trajectory = new Ballistica(cha.pos, cha.pos + i, Ballistica.MAGIC_BOLT);
							WandOfBlastWave.throwChar(cha, trajectory, 2, false, true, getClass());
						}
		
					}
				}
				Dungeon.hero.spendAndNext(1f);
				spendSP();
			} else {
				GLog.w(Messages.get(this, "cancel"));
			}
		}

		@Override
		public String prompt() {
			return Messages.get(this, "prompt");
		}

	};

	void callLightingFall(int cell){
		//effect
		arcs.clear();
		PointF from = DungeonTilemap.tileCenterToWorld(cell);
		PointF to = DungeonTilemap.tileCenterToWorld(cell);
		from.y -= 96;
		to.y -= 4;
		arcs.add(new Lightning.Arc(from, to));
		//Another lighting for a thicker looking hit
		from.y += 16;
		arcs.add(new Lightning.Arc(from, to));
		Dungeon.hero.sprite.parent.addToFront( new Lightning( arcs, null ) );
		CellEmitter.center(cell).burst(BlastParticle.FACTORY, 25);
		CellEmitter.get(cell).burst(SmokeParticle.FACTORY, 8);
		//damage
		Char ch = Actor.findChar(cell);
		if (ch!=null) ch.damage(max(), Dungeon.hero);
	}
	
	void callLightingAoE(int cell){
		arcs.clear();
		PointF from = DungeonTilemap.tileCenterToWorld(cell);
		for (int i : PathFinder.NEIGHBOURS8){
			Char ch = Actor.findChar(cell + i);
			if (ch != null) ch.damage(max()/2, Dungeon.hero);
			PointF to = DungeonTilemap.tileCenterToWorld(cell + i);
			arcs.add(new Lightning.Arc(from, to));
		}
		Dungeon.hero.sprite.parent.addToFront( new Lightning( arcs, null ) );
	}

	void callMeteor(int cell){
		callMeteorFall(cell, new Callback(){
			@Override
			public void call() {
				callMeteorExplode(cell, new Callback(){
					@Override
					public void call(){
						Dungeon.hero.spendAndNext(1f);
					}
				});
			}
		});
	}

	void callMeteorFall(int cell, Callback callback){
		PointF from = DungeonTilemap.tileCenterToWorld(cell);
		PointF to   = DungeonTilemap.tileCenterToWorld(cell);
		from.y -= 80;
		from.x += Random.Float(-1, 1)*32;
		to.y -= 4;
		//Three calls so the meteor look more CHONKY
		((MagicMissile)Dungeon.hero.sprite.parent.recycle( MagicMissile.class )).reset(
			MagicMissile.FIRE_CONE, 
			from, 
			to, 
			callback
		);
		((MagicMissile)Dungeon.hero.sprite.parent.recycle( MagicMissile.class )).reset(
			MagicMissile.FIRE_CONE, 
			from, 
			to, 
			null
		);
		((MagicMissile)Dungeon.hero.sprite.parent.recycle( MagicMissile.class )).reset(
			MagicMissile.FIRE_CONE, 
			from, 
			to, 
			null
		);
		Sample.INSTANCE.play( Assets.Sounds.HIT_MAGIC, 1, Random.Float(0.87f, 1.15f) );
	}

	void callMeteorExplode(int cell, Callback callback){
		//Center
		Char ch = Actor.findChar(cell);
		if (ch!= null){
			ch.damage(max()/4, Dungeon.hero);
			Buff.affect(ch, Burning.class).reignite(ch, 4f);
		}
		
		GameScene.add(Blob.seed(cell, 4, Fire.class));
		CellEmitter.center(cell).burst(BlastParticle.FACTORY, 25);
		CellEmitter.get(cell).burst(SmokeParticle.FACTORY, 8);

		//AoE
		for (int i : PathFinder.NEIGHBOURS8){
			Char cha = Actor.findChar(cell + i);
			if (cha!= null){
				cha.damage(max()/4, Dungeon.hero);
				Buff.affect(cha, Burning.class).reignite(cha, 3f);
			}
			GameScene.add(Blob.seed(i + cell, 2, Fire.class));
			((MagicMissile)curUser.sprite.parent.recycle( MagicMissile.class )).reset(
				MagicMissile.FIRE_CONE,
				DungeonTilemap.tileCenterToWorld(cell),
				DungeonTilemap.tileCenterToWorld(cell + i),
				null
			);
		}
		Dungeon.hero.sprite.zap(cell, callback);
	}

	public String skillInfo(){
		return Messages.get(this, "skill_desc", chargeGain, chargeNeed, max(), max()/4);
	}
}

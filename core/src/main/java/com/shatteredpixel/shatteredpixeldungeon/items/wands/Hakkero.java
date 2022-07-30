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

package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ConfusionGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Web;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MasterSpark;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PurpleParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Kinetic;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.RainbowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import java.util.ArrayList;
import com.watabou.noosa.particles.Emitter;

public class Hakkero extends DamageWand {

	{
		image = ItemSpriteSheet.HAKKERO;

		collisionProperties = Ballistica.MASTERSPARK;

        unique = true;
		bones = false;

		isMagician = false;
	}

	float interval = 2f;
	int quantity = 1;
	
	public int min(int lvl){
		return 2 + lvl + Dungeon.depth/2;
	}

	public int max(int lvl){
		return 8 + 3*lvl + Dungeon.depth/2;
	}
	
    protected int initialCharges() {
		return 2;
	}

	public void turnMagician() {
		this.isMagician = true;
	}

	@Override
	public int targetingPos(Hero user, int dst) {
		return dst;
	}

	@Override
	public void onZap(Ballistica beam) {

		//yes I'm serious, it doesn't look as good as I though though
		//I wanted to burst 3 times, quickly, like how touhou work buuuut it doesn't go as planned...
		// Dungeon.hero.sprite.centerEmitter().burst(Speck.factory(Speck.STAR_CIRCLE_1), quantity);
		// Dungeon.hero.sprite.centerEmitter().burst(Speck.factory(Speck.STAR_CIRCLE_2), quantity);
		// Dungeon.hero.sprite.centerEmitter().burst(Speck.factory(Speck.STAR_CIRCLE_3), quantity);
		// Dungeon.hero.sprite.centerEmitter().burst(Speck.factory(Speck.STAR_CIRCLE_4), quantity);
		// Dungeon.hero.sprite.centerEmitter().burst(Speck.factory(Speck.STAR_CIRCLE_5), quantity);
		// Dungeon.hero.sprite.centerEmitter().burst(Speck.factory(Speck.STAR_CIRCLE_6), quantity);
		// Dungeon.hero.sprite.centerEmitter().burst(Speck.factory(Speck.STAR_CIRCLE_7), quantity);
		// Dungeon.hero.sprite.centerEmitter().burst(Speck.factory(Speck.STAR_CIRCLE_8), quantity);
		// Dungeon.hero.sprite.centerEmitter().burst(Speck.factory(Speck.STAR_CIRCLE_9), quantity);
		
		float blind_dur = 0;
		if (Dungeon.hero.hasTalent(Talent.STARLIGHT_SPARK)){
			Buff.prolong( curUser, Light.class, Dungeon.hero.pointsInTalent(Talent.STARLIGHT_SPARK)*10);
			blind_dur = 1 + Dungeon.hero.pointsInTalent(Talent.STARLIGHT_SPARK);
		} else {
			Buff.prolong( curUser, Light.class, 1f);
			blind_dur = 1;
		}

		if (Dungeon.hero.hasTalent(Talent.MAGIC_STRIKE)){
			Buff.affect(Dungeon.hero, AttackEmpower.class).set(Dungeon.hero.pointsInTalent(Talent.MAGIC_STRIKE)*2,1);
		}

		if (Dungeon.hero.subClass == HeroSubClass.MAGICIAN){
			if(Dungeon.hero.hasTalent(Talent.ENERGY_RECYCLE)){
				for (Wand.Charger c : Dungeon.hero.buffs(Wand.Charger.class)){
					if (c.wand() != this){
						c.gainCharge(0.1f * Dungeon.hero.pointsInTalent(Talent.ENERGY_RECYCLE));
						ScrollOfRecharging.charge(Dungeon.hero);
					}
				}
			}


			Camera.main.shake( 1, 1f );
			for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
				if (Dungeon.level.heroFOV[mob.pos]) {
					Buff.affect( mob, Vertigo.class, 1f );
					Buff.affect( mob, Blindness.class, blind_dur );
				}
			}
		}
        boolean noticed = false;
		boolean terrainAffected = false;

		int level = buffedLvl();
		
		int maxDistance = Math.min(distance(), beam.dist);
		
		ArrayList<Char> chars = new ArrayList<>();

		Blob web = Dungeon.level.blobs.get(Web.class);

		int terrainPassed = 2, terrainBonus = 0;
		for (int c : beam.subPath(1, maxDistance)) {
            //Light
            if (!Dungeon.level.insideMap(c)){
				continue;
			}
			if (Dungeon.hero.subClass == HeroSubClass.MAGICIAN){
				GameScene.add( Blob.seed( c, 2, Fire.class ) );
			}

			for (int n : PathFinder.NEIGHBOURS9){
				int cell = c+n;

				if (Dungeon.level.discoverable[cell])
					Dungeon.level.mapped[cell] = true;

				int terr = Dungeon.level.map[cell];
				if ((Terrain.flags[terr] & Terrain.SECRET) != 0) {

					Dungeon.level.discover( cell );

					GameScene.discoverTile( cell, terr );
					ScrollOfMagicMapping.discover(cell);

					noticed = true;
				}    
			}
            if (noticed){
			    Sample.INSTANCE.play( Assets.Sounds.SECRET );
            }
			CellEmitter.center(c).burst( RainbowParticle.BURST, Random.IntRange( 1, 2 ) );
            //End Light
			Char ch;
			if ((ch = Actor.findChar( c )) != null) {

				//we don't want to count passed terrain after the last enemy hit. That would be a lot of bonus levels.
				//terrainPassed starts at 2, equivalent of rounding up when /3 for integer arithmetic.
				terrainBonus += terrainPassed/3;
				terrainPassed = terrainPassed%3;

				chars.add( ch );
			}
            
			if (Dungeon.level.solid[c]) {
				terrainPassed++;
			}

			if (Dungeon.level.flamable[c]) {

				Dungeon.level.destroy( c );
				GameScene.updateMap( c );
				terrainAffected = true;
				
			}
			
			CellEmitter.center( c ).burst( PurpleParticle.BURST, Random.IntRange( 3, 5 ) );
		}
		if (terrainAffected) {
			Dungeon.observe();
		}
		
		int lvl = level + (chars.size()-1) - terrainBonus;
		for (Char ch : chars) {
			wandProc(ch, chargesPerCast());
			affectTarget(ch);
			int dmg = damageRoll(lvl);

			//this is a bit messy, we have to check buff and deal buff damage for all enemies first
			if (Dungeon.hero.buff(HakkeroOverheat.class) != null){
				dmg *= (1f + 0.1f * Dungeon.hero.pointsInTalent(Talent.HEAT_HAKKERO));
			}

			ch.damage( damageRoll(lvl), this );
			ch.sprite.centerEmitter().burst( PurpleParticle.BURST, Random.IntRange( 1, 2 ) );
			ch.sprite.flash();

			if (Dungeon.hero.subClass == HeroSubClass.MAGICIAN){
				Buff.affect(ch, Burning.class).reignite(ch);
				Buff.affect(ch, Cripple.class, 2f);
			}
            //
            //  Talent add here
            //
            //
            //
		}

		if (Dungeon.hero.hasTalent(Talent.SPARK_SHIELD)){
			Buff.affect(Dungeon.hero, Barrier.class).setShield(Dungeon.hero.pointsInTalent(Talent.SPARK_SHIELD)*chars.size());
		}

		//Then refresh the overheat here so that the first zap won't get anything
		if (Dungeon.hero.hasTalent(Talent.HEAT_HAKKERO)){
			Buff.prolong(Dungeon.hero, HakkeroOverheat.class, (float)(Dungeon.hero.pointsInTalent(Talent.HEAT_HAKKERO)));
		}
	}

	private void affectTarget(Char ch){

		if(Dungeon.hero.subClass == HeroSubClass.THIEF){
			if(Dungeon.hero.buff(Magicdust.class) != null && Dungeon.hero.hasTalent(Talent.EXTENDED_FLIGHT)){
				if (Dungeon.hero.buff(Magicdust.class).freeflying()){
					Dungeon.hero.buff(Magicdust.class).extend(Dungeon.hero.pointsInTalent(Talent.EXTENDED_FLIGHT));
				}
				if (Dungeon.hero.buff(Magicdust.class).resting()){
					Dungeon.hero.buff(Magicdust.class).reduce(Dungeon.hero.pointsInTalent(Talent.EXTENDED_FLIGHT));
				}
			}

		}

		if (Dungeon.hero.hasTalent(Talent.LOVE_MS)){
			if(Random.IntRange(0,99) < 15*Dungeon.hero.pointsInTalent(Talent.LOVE_MS)){
				for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
					if (Dungeon.level.heroFOV[mob.pos]) {
						Buff.affect(ch, Charm.class, 4f);
					}
				}
			}
		}
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
	}

	private int distance() {
		return buffedLvl()*2 + 6;
	}
	
	@Override
	public void fx(Ballistica beam, Callback callback) {
		
		int cell = beam.path.get(Math.min(beam.dist, distance()));
		curUser.sprite.parent.add(new MasterSpark.MiniMasterSpark(curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld( cell ), 1.5f));
		callback.call();
	}

	@Override
	public void staffFx(MagesStaff.StaffParticle particle) {
		particle.color(0x220022);
		particle.am = 0.6f;
		particle.setLifespan(1f);
		particle.acc.set(10, -10);
		particle.setSize( 0.5f, 3f);
		particle.shuffleXY(1f);
	}
	
}

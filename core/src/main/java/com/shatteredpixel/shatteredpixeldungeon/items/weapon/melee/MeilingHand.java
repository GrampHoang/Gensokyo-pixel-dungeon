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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Effects;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.*;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;

public class MeilingHand extends WeaponWithSP {

	{
		image = ItemSpriteSheet.REISENHAND;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1.3f;

		tier = 3;
		DLY = 0.4f; //2.5x speed

		chargeGain = 10;
		chargeNeed = 100;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return enchantment != null && (cursedKnown || !enchantment.curse()) ?
				new ItemSprite.Glowing(enchantment.glowing().color, 1f*enchantment.glowing().period) : RAINBOW;
	}

	private static ItemSprite.Glowing RAINBOW = new ItemSprite.Glowing( 0x880000, 0.5f );


	@Override
	public int max(int lvl) {
		return  Math.round(1.5f*(tier+1)) +     //6 base, down from 20
				lvl*Math.round(0.5f*(tier+1));  //+2 per level, down from +4
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
        if ( Random.IntRange(0, 9) == 1){
            Buff.affect(defender, Paralysis.class, 1f );
        }
        return super.proc(attacker, defender, damage);
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
			
			if (cell != Dungeon.hero.pos){
				spendSP();
				((HeroSprite)Dungeon.hero.sprite).punch(Dungeon.hero.pos, cell);
				Ballistica aim = new Ballistica(Dungeon.hero.pos, cell, Ballistica.STOP_SOLID);
				for (int i : aim.subPath(1, Math.min(6, Dungeon.level.distance(Dungeon.hero.pos, aim.collisionPos)))){
					Char ch = Actor.findChar(i);
					if (ch != null && !(ch instanceof Hero)){
						Buff.prolong( ch, Paralysis.class, 1);
						ch.damage(Random.Int(max()/3, max()), Dungeon.hero);
					}
					Sample.INSTANCE.play(Assets.Sounds.ROCKS);
					PunchWave.blast(i);
					CellEmitter.get( i ).start( Speck.factory( Speck.ROCK ), 0.08f, 8 );
				}
			} else {
				spendSP();
				((HeroSprite)Dungeon.hero.sprite).punch(Dungeon.hero.pos, Dungeon.hero.pos);
				WandOfBlastWave.BlastWave.blast(Dungeon.hero.pos);
				for (int i : PathFinder.NEIGHBOURS8){
					Char ch = Actor.findChar(i + Dungeon.hero.pos);
					if (ch != null){
						Buff.prolong( ch, Paralysis.class, 1);
						ch.damage(Random.Int(max()/3, max()), Dungeon.hero);
					}
					Sample.INSTANCE.play(Assets.Sounds.ROCKS);
					CellEmitter.get( i + Dungeon.hero.pos ).start( Speck.factory( Speck.ROCK ), 0.07f, 5 );
				}
			}
			updateQuickslot();
		}

		@Override
		public String prompt() {
			return Messages.get(MeilingHand.class, "prompt");
		}

	};

	public static class PunchWave extends Image {

		private static final float TIME_TO_FADE = 0.64f;

		private float time;

		public PunchWave(){
			super(Effects.get(Effects.Type.RIPPLE));
			origin.set(width / 2, height / 2);
			hardlight(1, 0.5f, 0.5f);
		}

		public void reset(int pos) {
			revive();

			x = (pos % Dungeon.level.width()) * DungeonTilemap.SIZE + (DungeonTilemap.SIZE - width) / 2;
			y = (pos / Dungeon.level.width()) * DungeonTilemap.SIZE + (DungeonTilemap.SIZE - height) / 2;

			time = TIME_TO_FADE;
		}

		@Override
		public void update() {
			super.update();

			if ((time -= Game.elapsed) <= 0) {
				kill();
			} else {
				float p = time / TIME_TO_FADE;
				alpha(0.7f);
				scale.y = scale.x = (1-p)*1;
			}
		}

		public static void blast(int pos) {
			Group parent = Dungeon.hero.sprite.parent;
			PunchWave b = (PunchWave) parent.recycle(PunchWave.class);
			parent.bringToFront(b);
			b.reset(pos);
		}

	}
}

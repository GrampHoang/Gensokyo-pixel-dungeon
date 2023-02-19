package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou.MagicBook;

import java.util.ArrayList;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.IcyCloudParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.PatchouliSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class Patchouli extends Mob {
	
	{
		spriteClass = PatchouliSprite.class;
		
		HP = HT = 40;
		defenseSkill = 14;
		
		EXP = 7;
		maxLvl = 14;
		
		loot = new PotionOfHealing();
		lootChance = 0.2f; //see lootChance()
		
	}
	
	protected final int STATE_NONE_CASTING = 0;
	protected final int STATE_FIRE = 1;
	protected final int STATE_FROST = 2;
	protected final int STATE_SHOCK = 3;
	protected int state = STATE_NONE_CASTING;
	protected int summonCD = Random.NormalIntRange( 20, 25 );
	protected int SKILL_CD = 5;
	protected int skill_cd = 5;
	private ArrayList<Integer> pacthyCells = new ArrayList<>();


	//TODO: Must be careful with enemy, it can easily result into null, need check later
	@Override
	protected boolean act() {
		if (isLunatic()){
			skill_cd--;

			if (!enemySeen && state != STATE_NONE_CASTING){
				state = STATE_NONE_CASTING;
				skill_cd = SKILL_CD;
				spend(TICK);
				return true;
			}

			if (state != STATE_NONE_CASTING){
				this.sprite.remove(CharSprite.State.CHARGING);
				switch(state){
					case STATE_FIRE:
						skillFire();
						break;
					case STATE_FROST:
						skillFrost();
						break;
					case STATE_SHOCK:
						skillShock();
						break;	
					default:
						break;
				}
				if (this.HP > 4 && !isLunatic()){
					this.HP = this.HP - 4;
				}
				skill_cd = SKILL_CD;
				state = 0;
				spend(TICK);
				return true;
			}

			if (skill_cd <= 0 && state == STATE_NONE_CASTING && enemySeen){
				this.sprite.add(CharSprite.State.CHARGING);
				state = Random.IntRange(1,3);
				readySkill(enemy, state);
				spend(TICK);
				return true;
			}
		}

		
		if (summonCD > 0){
			summonCD--;
		} else if (summonCD <= 0 && enemy != null){
			for (int i : PathFinder.NEIGHBOURS8){
				if (Actor.findChar(this.pos + i) == null 
				&& (Dungeon.level.map[this.pos+i] == Terrain.EMPTY ||  Dungeon.level.map[this.pos+i] == Terrain.EMPTY_SP)){
					summonCD = 25;
					return summonBook(this.pos+i);
				}
			}
		}
		return super.act();
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 5);
	}
	
	@Override
	public float lootChance() {
		return super.lootChance() * ((6f - Dungeon.LimitedDrops.NECRO_HP.count) / 6f);
	}
	
	@Override
	public Item createLoot(){
		Dungeon.LimitedDrops.NECRO_HP.count++;
		return super.createLoot();
	}
	
	@Override
	public void die(Object cause) {
		this.sprite.remove(CharSprite.State.CHARGING);
		super.die(cause);
	}

	@Override
	protected boolean canAttack(Char enemy) {
		return false;
	}

	private static final String SUMMONING_CD = "summoning_cd";
	private static final String CUR_STATE = "cur_state";
	private static final String SKILL_COOLDOWN = "skill_cd";
	private static final String PATCHYCELLS = "patchyCells";
	// private static final String SUMMONING_CD = "summoning_cd";
	
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( SUMMONING_CD, summonCD );
		bundle.put( CUR_STATE, state );
		bundle.put( SKILL_COOLDOWN, skill_cd );
		
		int[] bundleArr = new int[pacthyCells.size()];
		for (int i = 0; i < pacthyCells.size(); i++){
			bundleArr[i] = pacthyCells.get(i);
		}
		bundle.put(PATCHYCELLS, bundleArr);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		summonCD = bundle.getInt( SUMMONING_CD );
		state = bundle.getInt(CUR_STATE);
		skill_cd = bundle.getInt(SKILL_COOLDOWN);
		for (int i : bundle.getIntArray(PATCHYCELLS)){
			pacthyCells.add(i);
		}
	}
	
	public boolean summonBook(int summoningPos){
		MagicBook patchyBook;
		float roll = Random.Float();
		if (roll < 0.4f){
			state = STATE_FIRE;
			patchyBook = new MagicBook.FireMagicBook();
		} else if (roll < 0.8f){
			state = STATE_FROST;
			patchyBook = new MagicBook.FrostMagicBook();
		} else {
			state = STATE_SHOCK;
			patchyBook = new MagicBook.ShockMagicBook();
		}

		patchyBook.pos = summoningPos;
		CellEmitter.get( summoningPos ).burst( Speck.factory( Speck.WOOL ), 6 );
		GameScene.add( patchyBook );
		Dungeon.level.occupyCell( patchyBook );

		for (Buff b : buffs(AllyBuff.class)){
			Buff.affect(patchyBook, b.getClass());
		}
		for (Buff b : buffs(ChampionEnemy.class)){
			Buff.affect( patchyBook, b.getClass());
		}
		patchyBook.spend_modified(1f);
		CellEmitter.get(summoningPos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);
		Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
		return true;
	}

	public void readySkill(Char enemy, int state){
		Dungeon.hero.interrupt();
		pacthyCells.clear();
		int color = 0xFF0000;
		switch(state){
			case STATE_FROST:
				color = 0x7EC8E3;
				break;
			case STATE_SHOCK:
				color = 0xFFDDFF;
				break;
			default:
				break;

		}
		for (int i : PathFinder.NEIGHBOURS8){
				if (Dungeon.level.solid[enemy.pos + i] == false && Random.IntRange(1,2) == 1){
					pacthyCells.add(enemy.pos + i);
					sprite.parent.add(new TargetedCell(enemy.pos + i, color));
				}
		}
	}

	public void skillFire(){
		Buff.affect(this, FireImbue.class).set(10f);
		for (int p : pacthyCells) {
            Char ch = Actor.findChar(p);
            if (ch != null && ch.alignment == this.alignment){
            } else {
				if (enemy == null) enemy = Dungeon.hero;
				PointF sky = enemy.sprite.center();
				sky.y -= 48;
				this.sprite.parent.add(new Beam.Gust(sky, DungeonTilemap.raisedTileCenterToWorld(p)));
				CellEmitter.center(p).burst(BlastParticle.FACTORY, 15);
				GameScene.add( Blob.seed( p, 3, Fire.class ) );
				if (ch != null) Buff.affect(ch, Burning.class).reignite(ch, 5f);
			}
        }

	}

	public void skillFrost(){
		Buff.affect(this, FrostImbue.class, 10f);
		for (int p : pacthyCells) {
            Char ch = Actor.findChar(p);
            if (ch != null && ch.alignment == this.alignment){
            } else {
				if (ch != null){Buff.affect(ch, Chill.class, 8f);}
				CellEmitter.get(p).burst(IcyCloudParticle.FACTORY, 5);
				GameScene.add( Blob.seed( p, 2, Freezing.class ) );
			}
        }

	}

	
	private ArrayList<Lightning.Arc> arcs = new ArrayList<>();

	public void skillShock(){
		Buff.affect(this, Stamina.class, 10f);
		for (int p : pacthyCells) {
            Char ch = Actor.findChar(p);
            if (ch != null && ch.alignment == this.alignment){
            } else {
				arcs.clear();
				PointF from = DungeonTilemap.tileCenterToWorld(p);
				PointF to = DungeonTilemap.tileCenterToWorld(p);
				from.y -= 96;
				to.y -= 4;
				arcs.add(new Lightning.Arc(from, to));
				this.sprite.parent.addToFront( new Lightning( arcs, null ) );
				CellEmitter.center(p).burst(BlastParticle.FACTORY, 10);
				CellEmitter.center(p).burst(SmokeParticle.FACTORY, 4);
				if (ch != null) {
					Buff.affect(ch, Paralysis.class, 2f);
					Buff.affect(ch, Blindness.class, 6f);
					ch.damage(5, this);
				}
			}
			
        }

	}
}

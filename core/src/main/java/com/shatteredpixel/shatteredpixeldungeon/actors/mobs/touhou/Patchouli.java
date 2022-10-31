package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou.MagicBook;

import java.util.ArrayList;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Adrenaline;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.NecromancerSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SkeletonSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Patchouli extends Mob {
	
	{
		spriteClass = NecromancerSprite.class;
		
		HP = HT = 40;
		defenseSkill = 14;
		
		EXP = 7;
		maxLvl = 14;
		
		loot = new PotionOfHealing();
		lootChance = 0.2f; //see lootChance()
		
	}
	
	protected final int STATE_CASTING = 4;
	protected final int STATE_FIRE = 1;
	protected final int STATE_FROST = 2;
	protected final int STATE_SHOCK = 3;
	protected int state = 0;
	protected int summonCD = Random.NormalIntRange( 20, 25 );
	protected int SKILL_CD = 5;
	protected int skillCD = 5;
	private ArrayList<Integer> pacthyCells = new ArrayList<>();


	//TODO: Must be careful with enemy, it can easily result into null, need check later
	@Override
	protected boolean act() {
		if (Dungeon.isChallenged(Challenges.LUNATIC)){
			skillCD--;

			if (!enemySeen && state == STATE_CASTING){
				state = 0;
				skillCD = SKILL_CD;
				spend(TICK);
				return true;
			}

			if (state == STATE_CASTING){
				this.sprite.remove(CharSprite.State.CHARGING);
				state = Random.IntRange(1,3);
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
				}
				if (this.HP > 4){
					this.HP = this.HP - 4;
				}
				skillCD = SKILL_CD;
				state = 0;
				spend(TICK);
				return true;
			}

			if (skillCD <= 0 && state != STATE_CASTING & enemySeen){
				this.sprite.add(CharSprite.State.CHARGING);
				state = STATE_CASTING;
				readySkill(enemy);
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
		super.die(cause);
	}

	@Override
	protected boolean canAttack(Char enemy) {
		return false;
	}

	private static final String SUMMONING_CD = "summoning_cd";
	
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( SUMMONING_CD, summonCD );
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		summonCD = bundle.getInt( SUMMONING_CD );
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

	public void readySkill(Char enemy){
		pacthyCells.clear();
		switch(Random.IntRange(0, 2)){
			case 0:
				for (int i : PathFinder.NEIGHBOURS8){
					pacthyCells.add(enemy.pos + i);
					sprite.parent.add(new TargetedCell(enemy.pos + i, 0xFF0000));
				}
				break;
			case 1:
				for (int i : PathFinder.NEIGHBOURS4){
					pacthyCells.add(enemy.pos + i);
					sprite.parent.add(new TargetedCell(enemy.pos + i, 0xFF0000));
				}
				pacthyCells.add(enemy.pos);
				sprite.parent.add(new TargetedCell(enemy.pos, 0xFF0000));
				break;
			default:
				for (int i : PathFinder.NEIGHBOURS4_CORNERS){
					pacthyCells.add(enemy.pos + i);
					sprite.parent.add(new TargetedCell(enemy.pos + i, 0xFF0000));
				}
				pacthyCells.add(enemy.pos);
				sprite.parent.add(new TargetedCell(enemy.pos, 0xFF0000));
				break;
		}
	}

	public void skillFire(){
		Buff.affect(this, FireImbue.class).set(10f);
		for (int p : pacthyCells) {
            Char ch = Actor.findChar(p);
            if (ch != null && ch.alignment == this.alignment){
            } else {
				GameScene.add( Blob.seed( p, 2, Fire.class ) );
			}
        }

	}

	public void skillFrost(){
		Buff.affect(this, FrostImbue.class, 10f);
		for (int p : pacthyCells) {
            Char ch = Actor.findChar(p);
            if (ch != null && ch.alignment == this.alignment){
            } else {
				if (ch != null){Buff.affect(ch, Chill.class, 3f);}
				GameScene.add( Blob.seed( p, 2, Freezing.class ) );
			}
        }

	}

	public void skillShock(){
		Buff.affect(this, Stamina.class, 10f);
		for (int p : pacthyCells) {
            Char ch = Actor.findChar(p);
            if (ch != null && ch.alignment == this.alignment){
            } else {
				GameScene.add( Blob.seed( p, 2, ParalyticGas.class ) );
			}
			
        }

	}
}

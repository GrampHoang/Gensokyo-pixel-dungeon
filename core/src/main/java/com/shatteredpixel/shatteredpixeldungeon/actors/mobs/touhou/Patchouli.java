package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou.MagicBook;

import java.util.ArrayList;

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
	
	protected final int STATE_CASTING = 3;
	protected final int STATE_FIRE = 1;
	protected final int STATE_FROST = 2;
	protected final int STATE_SHOCK = 3;
	protected int state = STATE_FIRE;
	protected int summonCD = Random.NormalIntRange( 20, 25 );
	protected int skillCD = 5;
	private ArrayList<Integer> pacthyCells = new ArrayList<>();


	//TODO: Must be careful with enemy, it can easily result into null, need check later
	@Override
	protected boolean act() {
		if (Dungeon.isChallenged(Challenges.LUNATIC)){
			if (state == STATE_CASTING){
				this.sprite.remove(CharSprite.State.CHARGING);
				switch(state){
					case STATE_FIRE:
						skillFire(enemy);
						break;
					case STATE_FROST:
						skillFrost(enemy);
						break;
					case STATE_SHOCK:
						skillShock(enemy);
						break;	
				}
				this.HP = this.HP/5*4;
				skillCD = 4;
			}
			if (skillCD <= 0 && state != STATE_CASTING){
				this.sprite.add(CharSprite.State.CHARGING);
				state = STATE_CASTING;
				readySkill(enemy);
			}
		}
		skillCD--;
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
		GameScene.add( patchyBook );
		Dungeon.level.occupyCell( patchyBook );

		for (Buff b : buffs(AllyBuff.class)){
			Buff.affect(patchyBook, b.getClass());
		}
		for (Buff b : buffs(ChampionEnemy.class)){
			Buff.affect( patchyBook, b.getClass());
		}
		return true;
	}

	public void readySkill(Char enemy){
		pacthyCells.clear();
		if (Random.IntRange(0, 0) == 0){
			for (int i : PathFinder.NEIGHBOURS8){
				if(Random.IntRange(0, 2) == 0){
					Ballistica b = new Ballistica(this.pos, enemy.pos+i, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID);
					pacthyCells.addAll(b.path);
				}
				pacthyCells.remove(pacthyCells.size() - 1);
			}
		} //Another patern maybe?
	}

	public void skillFire(Char enemy){
		Buff.affect(this, FireImbue.class).set(25f);
		for (int p : pacthyCells) {
            Char ch = Actor.findChar(p);
            if (ch != null && ch.alignment == this.alignment){
                pacthyCells.remove(ch.pos);
            }
			if (p != this.pos){
				GameScene.add( Blob.seed( p, 2, Fire.class ) );
			}
        }

	}

	public void skillFrost(Char enemy){
		Buff.affect(this, FrostImbue.class, 25f);
		for (int p : pacthyCells) {
            Char ch = Actor.findChar(p);
            if (ch != null && ch.alignment == this.alignment){
                pacthyCells.remove(ch.pos);
            }
			if (p != this.pos){
				GameScene.add( Blob.seed( p, 2, Freezing.class ) );
			}
        }

	}

	public void skillShock(Char enemy){
		Buff.affect(this, Haste.class, 25f);
		for (int p : pacthyCells) {
            Char ch = Actor.findChar(p);
            if (ch != null && ch.alignment == this.alignment){
                pacthyCells.remove(ch.pos);
            }
			if (p != this.pos){
				GameScene.add( Blob.seed( p, 2, ConfusionGas.class ) );
			}
        }

	}
}

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char.Alignment;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Circle8Utils;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;


public class MerlinTrumpet extends WeaponWithSP {

	{
		image = ItemSpriteSheet.MERLIN_TRUMPET;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1f;

		tier = 4;
		DLY = 1f;

        chargeGain = 10;
    }

	@Override
	public int max(int lvl) {
		return  Math.round(3f*(tier+1)) + 2 + //17 base
				lvl*Math.round(0.6f*(tier+1)); // 3 instead of 5 per level
	}

    @Override
	public int proc(Char attacker, Char defender, int damage) {
        int damageDo = super.proc(attacker, defender, damage);
        int collisionPos = attacker.pos;
        if (Dungeon.level.distance(attacker.pos, defender.pos) > 1){
            Ballistica b = new Ballistica( attacker.pos, defender.pos, Ballistica.WONT_STOP );
            for (int i = 0; i < b.path.size(); i++){
                if (b.path.get(i) == defender.pos){
                    collisionPos = i;
                    break;
                }
            }
		}
        //Return 3 tiles on the opposite side, already align to world cell so use them directly (unlike PathFinder)
        //TODO NEED FIX
        int[] damagePos = Circle8Utils.opposite3(PathFinder.NEIGHBOURS8, collisionPos, defender.pos);
        int count = 0;
        for (int i : damagePos){
            Char ch = Actor.findChar(i);
            //Exist and not same alignment
            if (ch != null && ch.alignment != attacker.alignment){
                count++;
                ch.damage(damageDo, attacker);
                ch.sprite.centerEmitter().start( Speck.factory( Speck.NOTE ), 0.2f, 3 );
            }
        }
        if (count == 0) {
            damageDo = (int)Math.round(damageDo * 1.5f);
            defender.sprite.centerEmitter().start( Speck.factory( Speck.NOTE ), 0.3f, 5 );
        } else {
            defender.sprite.centerEmitter().start( Speck.factory( Speck.NOTE ), 0.2f, 2 );
        }
        return damageDo;
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

			Ballistica attack = new Ballistica( Dungeon.hero.pos, cell, Ballistica.PROJECTILE);

			if (attack.collisionPos != cell.intValue()){
				GLog.w(Messages.get(MerlinTrumpet.class, "cannot_hit"));
				return;
			}
			if (ch != null){
                Dungeon.hero.busy();
                int collisionPos = ch.pos;
                if (Dungeon.level.distance(ch.pos, Dungeon.hero.pos) > 1){
                    Ballistica b = new Ballistica(Dungeon.hero.pos, ch.pos, Ballistica.WONT_STOP );
                    for (int i = 0; i < b.path.size(); i++){
                        if (b.path.get(i) == ch.pos){
                            collisionPos = i;
                            break;
                        }
                    }
                }
				int damageDo = max()/2;
                //Return 3 tiles on the opposite side, already align to world cell so use them directly (unlike PathFinder)
                //TODO NEED FIX
                int[] damagePos = Circle8Utils.opposite3(PathFinder.NEIGHBOURS8, collisionPos, ch.pos);
                
                for (int i : damagePos){
                    GLog.w(Integer.toString(i));
                    Char cha = Actor.findChar(i);
                    //Exist and not same alignment
                    if (cha != null && cha.alignment != Alignment.ALLY){
                        cha.damage(damageDo, Dungeon.hero);
                        cha.sprite.centerEmitter().start( Speck.factory( Speck.NOTE ),  0.3f, 5 );
                        Dungeon.hero.sprite.parent.add(
                    new Beam.DeathRay(Dungeon.hero.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(i)));
                    }
                }
                ch.sprite.centerEmitter().start( Speck.factory( Speck.NOTE ), 0.3f, 5 );
				spendSP();
                Dungeon.hero.spendAndNext(1f);
			} else {
				GLog.w(Messages.get(MerlinTrumpet.class, "no_target"));
				return;
			}
			updateQuickslot();
		}

		@Override
		public String prompt() {
			return Messages.get(MerlinTrumpet.class, "prompt");
		}

	};

    public String skillInfo(){
		return Messages.get(this, "skill_desc", chargeGain, chargeNeed, max()/2);
	}

    public class Note extends Item {
		{
			image = ItemSpriteSheet.NOTE_TEAL;
		}
	}
}

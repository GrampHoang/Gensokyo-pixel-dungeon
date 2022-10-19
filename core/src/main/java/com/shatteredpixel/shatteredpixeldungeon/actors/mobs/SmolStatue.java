package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon.Enchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.StatueSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Callback;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class SmolStatue extends Mob {
    {
        spriteClass = StatueSprite.class;

        EXP = 0;
        state = HUNTING;
        alignment = Alignment.ENEMY;
        properties.add(Property.INORGANIC);
    }
    
    
    public SmolStatue() {
        
        HP = HT = 15 + Dungeon.depth * 3;
        defenseSkill = 4 + Dungeon.depth/2;
    }

    @Override
    public int damageRoll() {
        return Random.IntRange(Dungeon.depth/2, Dungeon.depth);
    }

    @Override
    public int attackSkill( Char target ) {
        return (int)((9 + Dungeon.depth));
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, Dungeon.depth);
    }

    @Override
    public void die( Object cause ) {
        for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
            mob.beckon( this.pos );
        }
        this.sprite.centerEmitter().start( Speck.factory( Speck.SCREAM ), 0.2f, 2 );
        Sample.INSTANCE.play( Assets.Sounds.CHALLENGE );
        super.die(cause);
    }

    @Override
    public String description() {
        return Messages.get(this, "desc");
    }

    // @Override
    // public void storeInBundle( Bundle bundle ) {
    //     super.storeInBundle( bundle );
    // }
    
    // @Override
    // public void restoreFromBundle( Bundle bundle ) {
    //     super.restoreFromBundle( bundle );
    // }
}
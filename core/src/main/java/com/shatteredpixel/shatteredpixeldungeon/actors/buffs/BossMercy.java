/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2022 Evan Debenham
 *
 * Gensokyo Pixel Dungeon
 * Copyright (C) 2022-2023 GrampHoang
 *  * 
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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.TenshiNPC;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou.TenshiBoss.FallingLaser;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;

public class BossMercy extends Buff {
	
	{
		type = buffType.POSITIVE;
	}
	
	// @Override
	// public void fx(boolean on) {
	// 	if (on) target.sprite.add( CharSprite.State.DARKENED );
	// 	else if (target.invisible == 0) target.sprite.remove( CharSprite.State.DARKENED );
	// }
    
    private int pos;
    private int floor;
    private int branch;
    private int hp;
    private boolean tbd;

    public void set(int floor, int branch, Hero hero ) {
        this.floor = floor;
        this.branch = branch;
        this.hp = hero.HP;
        this.pos = hero.pos;
	}


    public void set(Hero hero ) {
        this.floor = Dungeon.depth;
        this.branch = Dungeon.branch;
        this.hp = hero.HP;
        this.pos = hero.pos;
	}

    public void tobeDetach() {
        this.tbd = true;
	}

    @Override
	public boolean act() {
        spend( TICK );
        if (tbd == true){
            detach();
        }

		if (target.HP < 1) {
            teleBack(true);
            detach();
		}
        return true;
    }

    public void teleBack(boolean lose){
        if (Dungeon.hero.buff(Burning.class) != null ) Dungeon.hero.buff(Burning.class).detach();
        if (Dungeon.hero.buff(FallingLaser.class) != null ) Dungeon.hero.buff(FallingLaser.class).detach();
        PotionOfHealing.cure(Dungeon.hero);
        InterlevelScene.mode = InterlevelScene.Mode.RETURN;
        InterlevelScene.returnDepth = this.floor;
        InterlevelScene.returnBranch = this.branch;
        InterlevelScene.returnPos = this.pos;
        if (lose){
            TenshiNPC.Quest.setImpression(1);
            Dungeon.hero.HP = this.hp;
        }
        Game.switchScene(InterlevelScene.class);
        Dungeon.hero.spendAndNext(1f);
    }

	@Override
	public int icon() {
		return BuffIndicator.AMULET;
	}
	
	@Override
	public String toString() {
		return Messages.get(this, "name");
	}
	
	@Override
	public String desc() {
		return Messages.get(this, "desc");
	}

    @Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( "FLOOR", floor );
		bundle.put( "BRANCH", branch );
        bundle.put( "HP", hp );
        bundle.put( "POS", pos );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);
		floor = bundle.getInt( "FLOOR" );
        branch = bundle.getInt( "BRANCH" );
        hp = bundle.getInt( "HP" );
        pos = bundle.getInt( "POS" );
	}
}

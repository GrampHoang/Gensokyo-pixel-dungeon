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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bless;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Hakkero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;

public class Magicdust extends Buff implements ActionIndicator.Action {
	
	{
		type = buffType.POSITIVE;

		//acts before the hero
		actPriority = HERO_PRIO+1;
	}
	
	private int dustStacks = 0;
	private int freeflyTurns = 0;
	private int freeflyCooldown = 0;

	private boolean movedLastTurn = true;

	@Override
	public boolean act() {
		if (freeflyCooldown > 0){
			freeflyCooldown--;
		}
		
		if (freeflyCooldown == 0 && !freeflying() && target.buff(Light.class) != null && Dungeon.hero.pointsInTalent(Talent.MAGUS_NIGHT) == 3){
			dustStacks = Math.min(dustStacks + 1, 20);
			movedLastTurn = true;
		}

		if (freeflyTurns > 0){
			Buff.prolong(Dungeon.hero, Levitation.class, 1f);
			
			if (Dungeon.hero.pointsInTalent(Talent.MAGUS_NIGHT) >= 1) {
				Buff.prolong(Dungeon.hero, Bless.class, 1f);
			}

			if (Dungeon.hero.pointsInTalent(Talent.MAGUS_NIGHT) >= 2) {
				for (Wand.Charger c : Dungeon.hero.buffs(Wand.Charger.class)){
					if (c.wand() instanceof Hakkero){
						c.gainCharge(0.05f);
					} else {
						c.gainCharge(0.1f);
					}
				}
			}

			if (Dungeon.hero.pointsInTalent(Talent.MAGUS_NIGHT) == 3) {
				Buff.prolong(Dungeon.hero, Light.class, 1f);
			}
			freeflyTurns--;
		} else if (!movedLastTurn){
			dustStacks = (int)GameMath.gate(0, dustStacks-1, Math.round(dustStacks * 0.8f));
			if (dustStacks <= 0) {
				ActionIndicator.clearAction(this);
				if (freeflyCooldown <= 0) detach();
			}
		}
		movedLastTurn = false;

		spend(TICK);
		return true;
	}
	
	public void gainStack(){
		movedLastTurn = true;
		if (freeflyCooldown <= 0 && !freeflying()){
			postpone(target.cooldown()+(1/target.speed()));
			dustStacks = Math.min(dustStacks + 1, 20);
			ActionIndicator.setAction(this);
		}
	}

	public void extend(int turn){
		if (freeflyTurns > 0){
			freeflyTurns += turn;
		}
	}

	public boolean freeflying(){
		return freeflyTurns > 0;
	}

	public boolean resting(){
		return freeflyCooldown > 0;
	}
	
	public void reduce(int turn){
		if (freeflyCooldown <= 3){
			freeflyCooldown = 0;
		} else{
			freeflyCooldown -= 3;
		}
	}

	public float speedMul(){
		int STRdif = Dungeon.hero.STR() - Dungeon.hero.belongings.armor.STRReq();
		if (Dungeon.hero.pointsInTalent(Talent.MAGICAL_FLIGHT) > 0 && STRdif > 0){
			return ((Dungeon.hero.pointsInTalent(Talent.MAGICAL_FLIGHT) * 0.05f + 0.05f) * STRdif);
		}
		else {
			return 0;
		}
	}

	public float speedMultiplier(){
		if (freeflying()){
			return 1.5f*(1f + speedMul());
		} else {
			return 1f + speedMul()/2;
		}
	}
	
	@Override
	public int icon() {
		return BuffIndicator.MOMENTUM;
	}
	
	@Override
	public void tintIcon(Image icon) {
		if (freeflyTurns > 0){
			icon.hardlight(1,1,0);
		} else if (freeflyCooldown > 0){
			icon.hardlight(0.5f,0.5f,1);
		} else {
			icon.hardlight(1f - (dustStacks /10f),1,1f - (dustStacks /10f));
		}
	}

	@Override
	public float iconFadePercent() {
		if (freeflyTurns > 0){
			return (20 - freeflyTurns) / 20f;
		} else if (freeflyCooldown > 0){
			return (freeflyCooldown) / 30f;
		} else {
			return (10 - dustStacks) / 10f;
		}
	}

	@Override
	public String iconTextDisplay() {
		if (freeflyTurns > 0){
			return Integer.toString(freeflyTurns);
		} else if (freeflyCooldown > 0){
			return Integer.toString(freeflyCooldown);
		} else {
			return Integer.toString(dustStacks);
		}
	}

	@Override
	public String toString() {
		if (freeflyTurns > 0){
			return Messages.get(this, "running");
		} else if (freeflyCooldown > 0){
			return Messages.get(this, "resting");
		} else {
			return Messages.get(this, "dust");
		}
	}
	
	@Override
	public String desc() {
		if (freeflyTurns > 0){
			return Messages.get(this, "running_desc", freeflyTurns);
		} else if (freeflyCooldown > 0){
			return Messages.get(this, "resting_desc", freeflyCooldown);
		} else {
			return Messages.get(this, "dust_desc", dustStacks);
		}
	}
	
	private static final String DUST_STACKS =        "dust_stacks";
	private static final String FREEFLY_TURNS = "freefly_turns";
	private static final String FREEFLY_CD =    "freefly_CD";
	
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(DUST_STACKS, dustStacks);
		bundle.put(FREEFLY_TURNS, freeflyTurns);
		bundle.put(FREEFLY_CD, freeflyCooldown);
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		dustStacks = bundle.getInt(DUST_STACKS);
		freeflyTurns = bundle.getInt(FREEFLY_TURNS);
		freeflyCooldown = bundle.getInt(FREEFLY_CD);
		if (dustStacks > 0 && freeflyTurns <= 0){
			ActionIndicator.setAction(this);
		}
		movedLastTurn = false;
	}

	@Override
	public String actionName() {
		return Messages.get(this, "action_name");
	}

	@Override
	public Image actionIcon() {
		Image im = new BuffIcon(BuffIndicator.HASTE, true);
		im.hardlight(0x99992E);
		return im;
	}

	@Override
	public void doAction() {
		Buff.prolong(Dungeon.hero, Levitation.class, 1f);
		if (Dungeon.hero.pointsInTalent(Talent.MAGUS_NIGHT) >= 1) {
			Buff.prolong(Dungeon.hero, Bless.class, 1f);
		}
		if (Dungeon.hero.pointsInTalent(Talent.MAGUS_NIGHT) == 3) {
			Buff.prolong(Dungeon.hero, Light.class, 1f);
		}
		freeflyTurns = dustStacks;
		//cooldown is functionally 10+2*stacks when active effect ends
		freeflyCooldown = 10 + 3*dustStacks;
		Sample.INSTANCE.play(Assets.Sounds.MISS, 1f, 0.8f);
		target.sprite.emitter().burst(Speck.factory(Speck.JET), 5+ dustStacks);
		target.sprite.emitter().burst(Speck.factory(Speck.STAR_FLY), 10);
		SpellSprite.show(target, SpellSprite.HASTE, 1, 1, 0);
		dustStacks = 0;
		BuffIndicator.refreshHero();
		ActionIndicator.clearAction(this);
		//Update FoV for the see through tall grass
		Dungeon.level.updateFieldOfView(Dungeon.hero, Dungeon.level.heroFOV);
	}	

	@Override
	public void fx(boolean on) {
		if (on) target.sprite.add(CharSprite.State.STAR_FLY);
		else target.sprite.remove(CharSprite.State.STAR_FLY);
	}

}

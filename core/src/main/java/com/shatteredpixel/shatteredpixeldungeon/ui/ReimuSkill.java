/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 *  Shattered Pixel Dungeon
 *  Copyright (C) 2014-2022 Evan Debenham
 *
 * Summoning Pixel Dungeon
 * Copyright (C) 2019-2022 TrashboxBobylev
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

package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Ofuda;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.input.GameAction;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;

public class ReimuSkill extends Tag {

    private static final float ENABLED	= 1.0f;
    private static final float DISABLED	= 0.3f;

    private ItemSprite sprite = null;
    public static ReimuSkill instance;

    @Override
    public GameAction keyAction() {
        return SPDAction.TAG_REIMU;
    }

    public ReimuSkill() {
        super(0xd10000 );
        setSize(24, 24);
        visible( false );
        enable( false );
        instance = this;
    }

    @Override
    public synchronized void destroy() {
        super.destroy();
        instance = null;
    }

    @Override
    protected synchronized void layout() {
        super.layout();

        if (sprite != null){
            sprite.x = x + (width - sprite.width()) / 2 + 2;
            sprite.y = y + (height - sprite.height()) / 2;
            PixelScene.align(sprite);
            if (!members.contains(sprite))
                add(sprite);
        }
    }

    private boolean enabled = true;
    private synchronized void enable( boolean value ) {
        enabled = value;
        if (sprite != null) {
            sprite.alpha( value ? ENABLED : DISABLED );
        }
    }

    private synchronized void visible( boolean value ) {
        bg.visible = value;
        if (sprite != null) {
            sprite.visible = value;
        }
    }

    private boolean needsLayout = false;

    @Override
    public synchronized void update() {
        super.update();
        lightness = 0.6f;

        if (!Dungeon.hero.ready){
            if (sprite != null) sprite.alpha(0.5f);
        } else {
            if (sprite != null) sprite.alpha(1f);
        }
        Hunger hunger = Dungeon.hero.buff(Hunger.class);
        if (hunger == null) return;

        if (Dungeon.hero.heroClass == HeroClass.REIMU) {
            if (Dungeon.hero.belongings.weapon != null && ((Weapon)Dungeon.hero.belongings.weapon).yy != null) {
                visible(true);
                enable(true);
                if (instance != null) {
                    synchronized (instance) {
                        if (instance.sprite != null) {
                            instance.sprite.killAndErase();
                            instance.sprite = null;
                        }
                        if (Dungeon.hero.belongings.weapon != null && ((Weapon)Dungeon.hero.belongings.weapon).yy != null) {
                            instance.sprite = new ItemSprite(Dungeon.hero.belongings.weapon.image, null);
                        }
                        instance.needsLayout = true;
                    }
                }
            } else {
                visible(false);
                enable(false);
            }
        } else {
            visible(false);
            enable(false);
        }

        if (needsLayout){
            layout();
            needsLayout = false;
        }
    }

    @Override
    protected void onClick() {
        if (enabled) GameScene.selectCell(attack);
    }

    private CellSelector.Listener attack = new CellSelector.Listener(){

        @Override
        public void onSelect(Integer cell) {
            if (cell == null) return;
            final Char enemy = Actor.findChar( cell );
            if (enemy == null
                    || !Dungeon.level.heroFOV[cell]
                    || Dungeon.hero.isCharmedBy( enemy )){
                GLog.w( Messages.get(ReimuSkill.class, "bad_target") );
            } else if (Dungeon.energy < 2){
                GLog.w( Messages.get(ReimuSkill.class, "low_energy") );
            }
            else {
                Dungeon.hero.sprite.attack(cell, new Callback() {
                    @Override
                    public void call() {
                        doAttack(enemy, cell);
                    }
                });
            }
        }

        @Override
        public String prompt() {
            return Messages.get(ReimuSkill.class, "prompt");
        }
    };

    public static void doAttack(final Char enemy, int cell){
        AttackIndicator.target(enemy);

        int energy_cost = 2;
        if(Dungeon.hero.hasTalent(Talent.DANMAKU_COMBAT)){
            if (Random.Int(0,9) > Dungeon.hero.pointsInTalent(Talent.DANMAKU_COMBAT)){
                energy_cost = 1;
                if (Random.Int(0,9) > 5){
                    energy_cost = 0;
                }
            }
        }
        Dungeon.energy -= energy_cost;

        MissileWeapon ofuda = new Ofuda();
        ofuda.cast(Dungeon.hero, cell);

        if (Dungeon.hero.buff(FireImbue.class) != null)
            Dungeon.hero.buff(FireImbue.class).proc(enemy);
        if (Dungeon.hero.buff(FrostImbue.class) != null)
            Dungeon.hero.buff(FrostImbue.class).proc(enemy);
        
        // Dungeon.hero.hitSound(Random.Float(0.87f, 1.15f));

        
        // Sample.INSTANCE.play( Assets.Sounds.ZAP, 1, Random.Float(0.87f, 1.15f) );
        // enemy.sprite.bloodBurstA( Dungeon.hero.sprite.center(), dmg );
        // enemy.sprite.flash();
    }
}
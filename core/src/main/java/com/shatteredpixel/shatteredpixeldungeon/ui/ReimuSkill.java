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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.ReisenGun;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.ReisenGun.Bullet;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Amulet;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon.Augment;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.input.GameAction;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.RevealedArea;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.NaturesPower;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfFuror;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfSharpshooting;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.watabou.noosa.particles.Emitter;

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

        Dungeon.energy -= 2;

        MissileWeapon amulet = new Amulet();
        amulet.cast(Dungeon.hero, cell);

        if (Dungeon.hero.buff(FireImbue.class) != null)
            Dungeon.hero.buff(FireImbue.class).proc(enemy);
        if (Dungeon.hero.buff(FrostImbue.class) != null)
            Dungeon.hero.buff(FrostImbue.class).proc(enemy);
        
        // Dungeon.hero.hitSound(Random.Float(0.87f, 1.15f));

        
        // Sample.INSTANCE.play( Assets.Sounds.ZAP, 1, Random.Float(0.87f, 1.15f) );
        // enemy.sprite.bloodBurstA( Dungeon.hero.sprite.center(), dmg );
        // enemy.sprite.flash();
    }

    // public class Amulet extends MissileWeapon {
		
	// 	{
	// 		image = ItemSpriteSheet.BULLET;
	// 		hitSound = Assets.Sounds.ZAP;
	// 	}

	// 	@Override
	// 	public Emitter emitter() {
	// 		return super.emitter();
	// 	}

	// 	@Override
	// 	protected void onThrow( int cell ) {
	// 		Char enemy = Actor.findChar( cell );
	// 		if (enemy == null || enemy == curUser) {
	// 			parent = null;
	// 			Splash.at( cell, 0xCC99FFFF, 1 );
	// 		} else {
	// 			if (!curUser.shoot( enemy, this )) {
	// 				Splash.at(cell, 0xCC99FFFF, 1);
	// 			}
	// 		}
	// 	}

    //     public Amulet knockArrow(){
    //         return new Amulet();
    //     }

    //     public int targetingPos(Hero user, int dst) {
    //         return knockArrow().targetingPos(user, dst);
    //     }

	// 	@Override
	// 	public void cast(final Hero user, final int dst) {
	// 		final int cell = throwPos( user, dst );
	// 		ReisenGun.this.targetPos = cell;
	// 		if (sniperSpecial && ReisenGun.this.augment == Augment.SPEED){
	// 			if (flurryCount == -1) flurryCount = 3;
				
	// 			final Char enemy = Actor.findChar( cell );
				
	// 			if (enemy == null){
	// 				user.spendAndNext(castDelay(user, dst));
	// 				sniperSpecial = false;
	// 				flurryCount = -1;
	// 				return;
	// 			}
	// 			QuickSlotButton.target(enemy);
				
	// 			final boolean last = flurryCount == 1;
				
	// 			user.busy();
				
	// 			throwSound();
				
	// 			((MissileSprite) user.sprite.parent.recycle(MissileSprite.class)).
	// 					reset(user.sprite,
	// 							cell,
	// 							this,
	// 							new Callback() {
	// 								@Override
	// 								public void call() {
	// 									if (enemy.isAlive()) {
	// 										curUser = user;
	// 										onThrow(cell);
	// 									}
										
	// 									if (last) {
	// 										user.spendAndNext(castDelay(user, dst));
	// 										sniperSpecial = false;
	// 										flurryCount = -1;
	// 									}
	// 								}
	// 							});
				
	// 			user.sprite.zap(cell, new Callback() {
	// 				@Override
	// 				public void call() {
	// 					flurryCount--;
	// 					if (flurryCount > 0){
	// 						cast(user, dst);
	// 					}
	// 				}
	// 			});
				
	// 		} else {

	// 			if (user.hasTalent(Talent.SCOUT_SHOT)
	// 					&& user.buff(Talent.ScoutingShotCooldown.class) == null){
	// 				int shotPos = throwPos(user, dst);
	// 				if (Actor.findChar(shotPos) == null) {
	// 					RevealedArea a = Buff.affect(user, RevealedArea.class, 5 * user.pointsInTalent(Talent.SCOUT_SHOT));
	// 					a.depth = Dungeon.depth;
	// 					a.pos = shotPos;
	// 					Buff.affect(user, Talent.ScoutingShotCooldown.class, 20f);
	// 				}
	// 			}

	// 			super.cast(user, dst);
	// 		}
	// 	}
	// }
}
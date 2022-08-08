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

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.UFOSettings;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.services.news.News;
import com.shatteredpixel.shatteredpixeldungeon.services.updates.Updates;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.GameLog;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.OptionSlider;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Toolbar;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.input.ControllerHandler;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class WndUFO extends WndTabbed {

	private static final int WIDTH_P	    = 122;
	private static final int WIDTH_L	    = 223;

	private static final int SLIDER_HEIGHT	= 23;
	private static final int BTN_HEIGHT	    = 17;
	private static final float GAP          = 2;

    private RedUFOTab       red;
    // private BlueUFOTab      blue;
    // private GreenUFOTab     green;
    // private RainbowUFOTab   rainbow;


	public static int last_index = 0;

	public WndUFO() {
		super();

		float height;

		int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;

        red = new RedUFOTab();
		red.setSize(width, 0);
		height = red.height();
		add( red );

		add( new IconTab(Icons.get(Icons.DISPLAY)){
			@Override
			protected void select(boolean value) {
				super.select(value);
				red.visible = red.active = value;
				if (value) last_index = 0;
			}
		});

        // blue = new BlueUFOTab();
		// blue.setSize(width, 0);
		// height = Math.max(height, blue.height());
		// add( blue );

		// add( new IconTab(Icons.get(Icons.DISPLAY)){
		// 	@Override
		// 	protected void select(boolean value) {
		// 		super.select(value);
		// 		blue.visible = blue.active = value;
		// 		if (value) last_index = 1;
		// 	}
		// });

        // green = new GreenUFOTab();
		// green.setSize(width, 0);
		// height = Math.max(height, green.height());
		// add( green );

		// add( new IconTab(Icons.get(Icons.DISPLAY)){
		// 	@Override
		// 	protected void select(boolean value) {
		// 		super.select(value);
		// 		green.visible = green.active = value;
		// 		if (value) last_index = 2;
		// 	}
		// });

        // rainbow = new RainbowUFOTab();
		// rainbow.setSize(width, 0);
		// height = Math.max(height, rainbow.height());
		// add( rainbow );

		// add( new IconTab(Icons.get(Icons.DISPLAY)){
		// 	@Override
		// 	protected void select(boolean value) {
		// 		super.select(value);
		// 		rainbow.visible = rainbow.active = value;
		// 		if (value) last_index = 4;
		// 	}
		// });

		resize(width, (int)Math.ceil(height));
		layoutTabs();

		if (tabs.size() == 5 && last_index >= 3){
			//input tab isn't visible
			select(last_index-1);
		} else {
			select(last_index);
		}
	}



	@Override
	public void hide() {
		super.hide();
		//resets generators because there's no need to retain chars for languages not selected
		ShatteredPixelDungeon.seamlessResetScene(new Game.SceneChangeCallback() {
			@Override
			public void beforeCreate() {
				Game.platform.resetGenerators();
			}
			@Override
			public void afterCreate() {
				//do nothing
			}
		});
	}

    private static class RedUFOTab extends Component {

		RenderedTextBlock title;
		ScrollPane pane;
		Component content;
		ColorBlock sep1;
		CheckBox red_HP;
		CheckBox red_Acc;
		CheckBox red_Eva;
		CheckBox red_Hunger;
		CheckBox red_Vision;
		CheckBox red_AttSpeed;
		CheckBox red_Search;
		CheckBox red_Mobs;
		CheckBox red_Shop;
		CheckBox red_Quest;
		CheckBox red_Gold;
		CheckBox red_Item;

        @Override
		protected void createChildren() {
			// pane = new ScrollPane(new Component());
			// add(pane);

			// content = pane.content();
			// content.clear();

			title = PixelScene.renderTextBlock(Messages.get(this, "title"), 9);
			title.hardlight(16721446); //Red
			add(title);

            sep1 = new ColorBlock(1, 1, 0xFF000000);
			add(sep1);

			red_HP = new CheckBox(Messages.get(this, "red_hp")){
				@Override
				protected void onClick() {
					super.onClick();
					UFOSettings.red_HP(checked());
				}
			};
			red_HP.checked(UFOSettings.red_HP());
			add(red_HP);

            red_Acc = new CheckBox(Messages.get(this, "red_acc")){
				@Override
				protected void onClick() {
					super.onClick();
					UFOSettings.red_Acc(checked());
				}
			};
			red_Acc.checked(UFOSettings.red_Acc());
			add(red_Acc);
			
            red_Eva = new CheckBox(Messages.get(this, "red_eva")){
				@Override
				protected void onClick() {
					super.onClick();
					UFOSettings.red_Eva(checked());
				}
			};
			red_Eva.checked(UFOSettings.red_Eva());
			add(red_Eva);

            red_Hunger = new CheckBox(Messages.get(this, "red_hunger")){
				@Override
				protected void onClick() {
					super.onClick();
					UFOSettings.red_Hunger(checked());
				}
			};
			red_Hunger.checked(UFOSettings.red_Hunger());
			add(red_Hunger);

			red_Vision = new CheckBox(Messages.get(this, "red_vision")){
				@Override
				protected void onClick() {
					super.onClick();
					UFOSettings.red_Vision(checked());
				}
			};
			red_Vision.checked(UFOSettings.red_Vision());
			add(red_Vision);

			red_AttSpeed = new CheckBox(Messages.get(this, "red_attspeed")){
				@Override
				protected void onClick() {
					super.onClick();
					UFOSettings.red_AttSpeed(checked());
				}
			};
			red_AttSpeed.checked(UFOSettings.red_AttSpeed());
			add(red_AttSpeed);

			red_Gold = new CheckBox(Messages.get(this, "red_gold")){
				@Override
				protected void onClick() {
					super.onClick();
					UFOSettings.red_Gold(checked());
				}
			};
			red_Gold.checked(UFOSettings.red_Gold());
			add(red_Gold);

			red_Item = new CheckBox(Messages.get(this, "red_item")){
				@Override
				protected void onClick() {
					super.onClick();
					UFOSettings.red_Item(checked());
				}
			};
			red_Item.checked(UFOSettings.red_Item());
			add(red_Item);

			red_Quest = new CheckBox(Messages.get(this, "red_quest")){
				@Override
				protected void onClick() {
					super.onClick();
					UFOSettings.red_Quest(checked());
				}
			};
			red_Quest.checked(UFOSettings.red_Quest());
			add(red_Quest);

			red_Search = new CheckBox(Messages.get(this, "red_search")){
				@Override
				protected void onClick() {
					super.onClick();
					UFOSettings.red_Search(checked());
				}
			};
			red_Search.checked(UFOSettings.red_Search());
			add(red_Search);

			red_Mobs = new CheckBox(Messages.get(this, "red_mobs")){
				@Override
				protected void onClick() {
					super.onClick();
					UFOSettings.red_Mobs(checked());
				}
			};
			red_Mobs.checked(UFOSettings.red_Mobs());
			add(red_Mobs);

			red_Shop = new CheckBox(Messages.get(this, "red_shop")){
				@Override
				protected void onClick() {
					super.onClick();
					UFOSettings.red_Shop(checked());
				}
			};
			red_Shop.checked(UFOSettings.red_Shop());
			add(red_Shop);
		}

		@Override
		protected void layout() {
			title.setPos((width - title.width())/2, y + GAP);
			sep1.size(width, 1);
	        sep1.y = title.bottom() + 2*GAP;
			
			// content.setSize(width, BTN_HEIGHT*13);
			// content.setSize(width, BTN_HEIGHT*13);
			// pane.setRect(0, sep1.y + GAP, width, height - GAP*4.5f);
			red_Quest.setRect(0, sep1.y + 1 + GAP + GAP, width-1, BTN_HEIGHT);
            red_HP.setRect(0, red_Quest.bottom() + GAP, width-1, BTN_HEIGHT);
			red_Hunger.setRect(0, red_HP.bottom() + GAP, width-1, BTN_HEIGHT);
			red_AttSpeed.setRect(0, red_Hunger.bottom() + GAP, width-1, BTN_HEIGHT);
			red_Gold.setRect(0, red_AttSpeed.bottom() + GAP, width-1, BTN_HEIGHT);
			red_Item.setRect(0, red_Gold.bottom() + GAP, width-1, BTN_HEIGHT);
			red_Acc.setRect(0, red_Item.bottom() + GAP, width-1, BTN_HEIGHT);
			red_Eva.setRect(0, red_Acc.bottom() + GAP, width-1, BTN_HEIGHT);
			red_Vision.setRect(0, red_Eva.bottom() + GAP, width-1, BTN_HEIGHT);
			red_Search.setRect(0, red_Vision.bottom() + GAP, width-1, BTN_HEIGHT);
			red_Mobs.setRect(0, red_Search.bottom() + GAP, width-1, BTN_HEIGHT);
			red_Shop.setRect(0, red_Mobs.bottom() + GAP, width-1, BTN_HEIGHT);

            height = red_Shop.bottom();

			// red_HP.setRect(0, sep1.y + 1 + GAP, width-1, BTN_HEIGHT);
			// red_Acc.setRect(0, red_HP.bottom() + GAP, width-1, BTN_HEIGHT);
			// height = red_Acc.bottom();
		}

	}


    // private static class BlueUFOTab extends Component {

	// 	RenderedTextBlock title;
	// 	ColorBlock sep1;
    //     CheckBox red_HP;
    //     CheckBox upgrade_1_ACC;
    //     CheckBox upgrade_1_EVA;
    //     CheckBox upgrade_1_HUNGER;


    //     @Override
	// 	protected void createChildren() {
	// 		title = PixelScene.renderTextBlock(Messages.get(this, "title"), 9);
	// 		title.hardlight(16721446); //Red
	// 		add(title);

    //         sep1 = new ColorBlock(1, 1, 0xFF000000);
	// 		add(sep1);

	// 		red_HP = new CheckBox(Messages.get(this, "red_hp")){
	// 			@Override
	// 			protected void onClick() {
	// 				super.onClick();
	// 				UFOSettings.red_HP(checked());
	// 			}
	// 		};
	// 		red_HP.checked(UFOSettings.red_HP());
	// 		add(red_HP);
	// 	}

	// 	@Override
	// 	protected void layout() {
	// 		title.setPos((width - title.width())/2, y + GAP);
	// 		sep1.size(width, 1);
	//         sep1.y = title.bottom() + 2*GAP;
            
    //         red_HP.setRect(0, sep1.y + 1 + GAP, width-1, BTN_HEIGHT);
    //         height = upgrade_1_HUNGER.bottom();
	// 	}

	// }
    // private static class GreenUFOTab extends Component {

	// 	RenderedTextBlock title;
	// 	ColorBlock sep1;
    //     CheckBox upgrade_1_HP;
    //     CheckBox upgrade_1_ACC;
    //     CheckBox upgrade_1_EVA;
    //     CheckBox upgrade_1_HUNGER;


    //     @Override
	// 	protected void createChildren() {
	// 		title = PixelScene.renderTextBlock(Messages.get(this, "title"), 9);
	// 		title.hardlight(16721446); //Red
	// 		add(title);

    //         sep1 = new ColorBlock(1, 1, 0xFF000000);
	// 		add(sep1);

	// 		upgrade_1_HP = new CheckBox(Messages.get(this, "red_hp")){
	// 			@Override
	// 			protected void onClick() {
	// 				super.onClick();
	// 				UFOSettings.upgrade_1_HP(checked());
	// 			}
	// 		};
	// 		upgrade_1_HP.checked(UFOSettings.upgrade_1_HP());
	// 		add(upgrade_1_HP);


    //         upgrade_1_EVA = new CheckBox(Messages.get(this, "red_eva")){
	// 			@Override
	// 			protected void onClick() {
	// 				super.onClick();
	// 				UFOSettings.upgrade_1_EVA(checked());
	// 			}
	// 		};
	// 		upgrade_1_EVA.checked(UFOSettings.upgrade_1_EVA());
	// 		add(upgrade_1_EVA);


    //         upgrade_1_ACC = new CheckBox(Messages.get(this, "red_acc")){
	// 			@Override
	// 			protected void onClick() {
	// 				super.onClick();
	// 				UFOSettings.upgrade_1_ACC(checked());
	// 			}
	// 		};
	// 		upgrade_1_ACC.checked(UFOSettings.upgrade_1_ACC());
	// 		add(upgrade_1_ACC);

    //         upgrade_1_HUNGER = new CheckBox(Messages.get(this, "red_hunger")){
	// 			@Override
	// 			protected void onClick() {
	// 				super.onClick();
	// 				UFOSettings.upgrade_1_HUNGER(checked());
	// 			}
	// 		};
	// 		upgrade_1_HUNGER.checked(UFOSettings.upgrade_1_HUNGER());
	// 		add(upgrade_1_HUNGER);


	// 	}

	// 	@Override
	// 	protected void layout() {
	// 		title.setPos((width - title.width())/2, y + GAP);
	// 		sep1.size(width, 1);
	//         sep1.y = title.bottom() + 2*GAP;
            
    //         upgrade_1_HP.setRect(0, sep1.y + 1 + GAP, width-1, BTN_HEIGHT);
    //         upgrade_1_ACC.setRect(0, upgrade_1_HP.bottom() + GAP, width-1, BTN_HEIGHT);
    //         upgrade_1_EVA.setRect(0, upgrade_1_ACC.bottom() + GAP, width-1, BTN_HEIGHT);
    //         upgrade_1_HUNGER.setRect(0, upgrade_1_EVA.bottom() + GAP, width-1, BTN_HEIGHT);

    //         height = upgrade_1_HUNGER.bottom();
	// 	}

	// }
    // private static class RainbowUFOTab extends Component {

	// 	RenderedTextBlock title;
	// 	ColorBlock sep1;
	// 	OptionSlider optMusic;
	// 	CheckBox chkMusicMute;
	// 	ColorBlock sep2;
	// 	OptionSlider optSFX;
	// 	CheckBox chkMuteSFX;
	// 	ColorBlock sep3;
	// 	CheckBox chkIgnoreSilent;

    //     CheckBox upgrade_1_HP;
    //     CheckBox upgrade_1_ACC;
    //     CheckBox upgrade_1_EVA;
    //     CheckBox upgrade_1_HUNGER;


	// 	@Override
	// 	protected void createChildren() {
	// 		title = PixelScene.renderTextBlock(Messages.get(this, "title"), 9);
	// 		title.hardlight(6826080 ); //Red
	// 		add(title);

    //         sep1 = new ColorBlock(1, 1, 0xFF000000);
	// 		add(sep1);

	// 		upgrade_1_HP = new CheckBox(Messages.get(this, "upgrade_1_HP")){
	// 			@Override
	// 			protected void onClick() {
	// 				super.onClick();
	// 				SPDSettings.music(!checked());
	// 			}
	// 		};
	// 		upgrade_1_HP.checked(SPDSettings.music());
	// 		add(upgrade_1_HP);


    //         upgrade_1_EVA = new CheckBox(Messages.get(this, "upgrade_1_EVA")){
	// 			@Override
	// 			protected void onClick() {
	// 				super.onClick();
	// 				SPDSettings.music(!checked());
	// 			}
	// 		};
	// 		upgrade_1_EVA.checked(SPDSettings.music());
	// 		add(upgrade_1_EVA);


    //         upgrade_1_ACC = new CheckBox(Messages.get(this, "upgrade_1_ACC")){
	// 			@Override
	// 			protected void onClick() {
	// 				super.onClick();
	// 				SPDSettings.music(!checked());
	// 			}
	// 		};
	// 		upgrade_1_ACC.checked(SPDSettings.music());
	// 		add(upgrade_1_ACC);

    //         upgrade_1_HUNGER = new CheckBox(Messages.get(this, "upgrade_1_HUNGER")){
	// 			@Override
	// 			protected void onClick() {
	// 				super.onClick();
	// 				SPDSettings.music(!checked());
	// 			}
	// 		};
	// 		upgrade_1_HUNGER.checked(SPDSettings.music());
	// 		add(upgrade_1_HUNGER);


	// 	}

	// 	@Override
	// 	protected void layout() {
	// 		title.setPos((width - title.width())/2, y + GAP);
	// 		sep1.size(width, 1);
	//         sep1.y = title.bottom() + 2*GAP;
    //         upgrade_1_HP.setRect(0, sep1.y + 1 + GAP, width/2-1, BTN_HEIGHT);
    //         upgrade_1_ACC.setRect(0, upgrade_1_HP.bottom() + GAP, width/2-1, BTN_HEIGHT);
    //         upgrade_1_EVA.setRect(0, upgrade_1_EVA.bottom() + GAP, width/2-1, SLIDER_HEIGHT);
    //         upgrade_1_HUNGER.setRect(0, upgrade_1_HUNGER.bottom() + GAP, width/2-1, BTN_HEIGHT);
	// 	}
	// }
}

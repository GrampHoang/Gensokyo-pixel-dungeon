package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.SakuyaNPC;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.YoumuNPC;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoItem;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;

public class RewardButton extends Component {

    protected NinePatch bg;
    protected ItemSlot slot;
    protected Window wnd;
    protected NPC npc;
    protected Item questItem;

    public RewardButton( Item item, NPC questGiver, Window windown, Item qItem){
        this.wnd = windown;
        this.npc = questGiver;
        this.questItem = (qItem == null) ? null : qItem;
        bg = Chrome.get( Chrome.Type.RED_BUTTON);
        add( bg );

        slot = new ItemSlot( item ){
            @Override
            protected void onPointerDown() {
                bg.brightness( 1.2f );
                Sample.INSTANCE.play( Assets.Sounds.CLICK );
            }
            @Override
            protected void onPointerUp() {
                bg.resetColor();
            }
            @Override
            protected void onClick() {
                GameScene.show(new RewardWindow(item));
            }
        };
        add(slot);
    }

    @Override
    protected void layout() {
        super.layout();

        bg.x = x;
        bg.y = y;
        bg.size( width, height );

        slot.setRect( x + 2, y + 2, width - 4, height - 4 );
    }

    private void selectReward( Item reward ) {

		if (reward == null){
			return;
		}

		wnd.hide();

		if (questItem != null) questItem.detach( Dungeon.hero.belongings.backpack );

		reward.identify(false);
		if (reward.doPickUp( Dungeon.hero )) {
			GLog.i( Messages.get(Dungeon.hero, "you_now_have", reward.name()) );
		} else {
			Dungeon.level.drop( reward, npc.pos ).sprite.drop();
		}
		
		// npc.yell( Messages.get(this, "farewell", Dungeon.hero.name()) );
		npc.destroy();
		npc.sprite.die();

        // More convinience to do it here instead of each class
        if (npc instanceof SakuyaNPC) SakuyaNPC.Quest.complete();
        else if (npc instanceof YoumuNPC) YoumuNPC.Quest.complete();
        // else if (npc instanceof SakuyaNPC) ((SakuyaNPC)npc).Quest.complete();
        // else if (npc instanceof SakuyaNPC) ((SakuyaNPC)npc).Quest.complete();
        // else if (npc instanceof SakuyaNPC) ((SakuyaNPC)npc).Quest.complete();
        // else if (npc instanceof SakuyaNPC) ((SakuyaNPC)npc).Quest.complete();

	}

    private class RewardWindow extends WndInfoItem {

		public RewardWindow( Item item ) {
			super(item);

			RedButton btnConfirm = new RedButton(Messages.get(RewardButton.class, "confirm")){
				@Override
				protected void onClick() {
					RewardWindow.this.hide();
					RewardButton.this.selectReward( item );
				}
			};
			btnConfirm.setRect(0, height+2, width/2-1, 16);
			add(btnConfirm);

			RedButton btnCancel = new RedButton(Messages.get(RewardButton.class, "cancel")){
				@Override
				protected void onClick() {
					RewardWindow.this.hide();
				}
			};
			btnCancel.setRect(btnConfirm.right()+2, height+2, btnConfirm.width(), 16);
			add(btnCancel);

			resize(width, (int)btnCancel.bottom());
		}
	}
}
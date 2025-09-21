package org.newdawn.spaceinvaders.game_object.ingame.loot_item;

import org.newdawn.spaceinvaders.loop.Loop;

public class ShieldItem extends LootItem {
    public ShieldItem(Loop loop) {
        super(loop);

        System.out.println("Shield Item 소환");
    }

    protected void onCollideWithPlayerShip(){
        System.out.println("봉쇄죠?");
    }
}

package org.newdawn.spaceinvaders.game_object.ingame.loot_item;

import org.newdawn.spaceinvaders.loop.GameLoop;
import org.newdawn.spaceinvaders.loop.Loop;

public class BatteryItem extends LootItem {
    public BatteryItem(Loop loop) {
        super(loop, "sprites/testLootItem.png");

        System.out.println("Battery Item 소환");
    }

    protected void onCollideWithPlayerShip(){
        System.out.println("유저 속도 빨라짐");
    }
}

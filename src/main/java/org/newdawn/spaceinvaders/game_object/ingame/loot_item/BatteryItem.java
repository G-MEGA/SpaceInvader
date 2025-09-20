package org.newdawn.spaceinvaders.game_object.ingame.loot_item;

import org.newdawn.spaceinvaders.loop.Loop;

public class BatteryItem extends LootItem {
    public BatteryItem(Loop loop) {
        super(loop);

        System.out.println("Battery Item 소환");
    }
}

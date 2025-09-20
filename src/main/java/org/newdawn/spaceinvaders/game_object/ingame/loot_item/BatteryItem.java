package org.newdawn.spaceinvaders.game_object.ingame.loot_item;

public class BatteryItem extends LootItem {
    public BatteryItem() {
        super();

        System.out.println("Battery Item 소환");
    }

    public void onCollideWithPlayer(){}
}

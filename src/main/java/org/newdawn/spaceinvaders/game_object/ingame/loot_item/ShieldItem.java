package org.newdawn.spaceinvaders.game_object.ingame.loot_item;

public class ShieldItem extends LootItem {
    public ShieldItem() {
        super();

        System.out.println("Shield Item 소환");
    }

    public void onCollideWithPlayer(){}
}

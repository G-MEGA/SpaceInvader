package org.newdawn.spaceinvaders.game_object.ingame.loot_item;

public class FrozenItem extends LootItem {
    public FrozenItem(){
        super();

        System.out.println("Frozen Item 소환");
    }

    public void onCollideWithPlayer(){}
}

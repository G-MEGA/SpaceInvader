package org.newdawn.spaceinvaders.game_object.ingame.loot_item;

public class CoinItem extends LootItem {
    public CoinItem() {
        super();

        System.out.println("Coin Item 소환");
    }
    
    public void onCollideWithPlayer(){}
}

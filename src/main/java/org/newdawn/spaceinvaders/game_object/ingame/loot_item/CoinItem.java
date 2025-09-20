package org.newdawn.spaceinvaders.game_object.ingame.loot_item;

import org.newdawn.spaceinvaders.loop.Loop;

public class CoinItem extends LootItem {
    public CoinItem(Loop loop) {
        super(loop);

        System.out.println("Coin Item 소환");
    }
}

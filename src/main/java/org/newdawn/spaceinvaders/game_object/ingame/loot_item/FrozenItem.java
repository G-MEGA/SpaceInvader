package org.newdawn.spaceinvaders.game_object.ingame.loot_item;

import org.newdawn.spaceinvaders.loop.GameLoop;
import org.newdawn.spaceinvaders.loop.Loop;

public class FrozenItem extends LootItem {
    public FrozenItem(Loop loop){
        super(loop,"sprites/testLootItem.png");

        System.out.println("Frozen Item 소환");
    }

    protected void onCollideWithPlayerShip(){
        System.out.println("멈춰!");
    }
}

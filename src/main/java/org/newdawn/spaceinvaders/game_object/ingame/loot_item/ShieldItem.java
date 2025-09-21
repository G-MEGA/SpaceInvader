package org.newdawn.spaceinvaders.game_object.ingame.loot_item;

import org.newdawn.spaceinvaders.loop.GameLoop;
import org.newdawn.spaceinvaders.loop.Loop;

public class ShieldItem extends LootItem {
    public ShieldItem(Loop loop) {
        super(loop, "sprites/testShieldItem.png");
    }

    protected void onCollideWithPlayerShip(){
        if (loop instanceof GameLoop){
            GameLoop gameLoop = (GameLoop)loop;
            gameLoop.addShieldOnPlayerShip();
        }
    }
}

package org.newdawn.spaceinvaders.game_object.ingame.loot_item;

import org.newdawn.spaceinvaders.loop.GameLoop;
import org.newdawn.spaceinvaders.loop.Loop;

public class BatteryItem extends LootItem {
    public BatteryItem(Loop loop) {
        super(loop, "sprites/testBatteryItem.png");
    }

    protected void onCollideWithPlayerShip(){
        if (loop instanceof GameLoop){
            GameLoop gameLoop = (GameLoop)loop;
            gameLoop.requestToSpeedUpOnPlayerShip();
        }
    }
}

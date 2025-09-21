package org.newdawn.spaceinvaders.game_object.ingame.loot_item;

import org.newdawn.spaceinvaders.loop.GameLoop;
import org.newdawn.spaceinvaders.loop.Loop;

public class FrozenItem extends LootItem {
    public FrozenItem(Loop loop){
        super(loop,"sprites/testFrozenItem.png");
    }

    protected void onCollideWithPlayerShip(){
        if (loop instanceof GameLoop){
            GameLoop gameLoop = (GameLoop)loop;
            gameLoop.requestToSlowDownEnemies();
        }
    }
}

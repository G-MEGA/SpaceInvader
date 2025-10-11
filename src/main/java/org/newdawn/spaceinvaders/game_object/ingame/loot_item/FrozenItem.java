package org.newdawn.spaceinvaders.game_object.ingame.loot_item;

import org.newdawn.spaceinvaders.game_object.ingame.player.PlayerShip;
import org.newdawn.spaceinvaders.loop.GameLoop;
import org.newdawn.spaceinvaders.loop.Loop;

public class FrozenItem extends LootItem {
    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public FrozenItem(){
        super();
    }
    public FrozenItem(Loop loop){
        super(loop,"sprites/testFrozenItem.png");
    }

    protected void onCollideWithPlayerShip(PlayerShip ship){
        if (getLoop() instanceof GameLoop){
            GameLoop gameLoop = (GameLoop)getLoop();
            gameLoop.requestToSlowDownEnemies();
        }
    }
}

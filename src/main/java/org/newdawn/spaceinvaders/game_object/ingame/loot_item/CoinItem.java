package org.newdawn.spaceinvaders.game_object.ingame.loot_item;

import org.newdawn.spaceinvaders.Game;
import org.newdawn.spaceinvaders.loop.GameLoop;
import org.newdawn.spaceinvaders.loop.Loop;

public class CoinItem extends LootItem {
    public CoinItem(Loop loop) {
        super(loop, "sprites/coin.png");

        System.out.println("Coin Item 소환");
    }

    protected void onCollideWithPlayerShip(){
        if (loop instanceof GameLoop){
            GameLoop gameLoop = (GameLoop)loop;
            gameLoop.addCoin();
        }
    }
}

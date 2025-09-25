package org.newdawn.spaceinvaders.game_object.ingame.loot_item;

import org.newdawn.spaceinvaders.Game;
import org.newdawn.spaceinvaders.loop.GameLoop;
import org.newdawn.spaceinvaders.loop.Loop;

public class ScoringItem extends LootItem {
    private static final String spriteRef = "sprites/scoringItem.png";
    private final int scoringScore = 1000;

    public ScoringItem(GameLoop gameLoop) {
        super(gameLoop, spriteRef);
    }

    @Override
    protected void onCollideWithPlayerShip() {
        if (loop instanceof GameLoop){
            GameLoop gameLoop = (GameLoop)loop;
            gameLoop.increaseScore(scoringScore);
        }
    }
}

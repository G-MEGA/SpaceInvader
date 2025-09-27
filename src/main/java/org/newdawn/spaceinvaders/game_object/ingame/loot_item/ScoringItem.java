package org.newdawn.spaceinvaders.game_object.ingame.loot_item;

import org.newdawn.spaceinvaders.Game;
import org.newdawn.spaceinvaders.loop.GameLoop;
import org.newdawn.spaceinvaders.loop.Loop;

public class ScoringItem extends LootItem {
    private static final String spriteRef = "sprites/scoringItem.png";
    private final int scoringScore = 1000;

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public ScoringItem(){
        super();
    }
    public ScoringItem(GameLoop gameLoop) {
        super(gameLoop, spriteRef);
    }

    @Override
    protected void onCollideWithPlayerShip() {
        if (getLoop() instanceof GameLoop){
            GameLoop gameLoop = (GameLoop)getLoop();
            gameLoop.increaseScore(scoringScore);
        }
    }
}

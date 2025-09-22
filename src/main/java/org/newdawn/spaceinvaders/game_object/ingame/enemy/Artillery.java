package org.newdawn.spaceinvaders.game_object.ingame.enemy;

import org.newdawn.spaceinvaders.game_object.logic.HiveMind;
import org.newdawn.spaceinvaders.loop.GameLoop;

public class Artillery extends SweeperEnemy{

    public Artillery(GameLoop gameLoop, HiveMind hiveMind, long initialHealth) {
        super(gameLoop, hiveMind, initialHealth);
    }

    @Override
    protected void addSprites() {
    }
    
}

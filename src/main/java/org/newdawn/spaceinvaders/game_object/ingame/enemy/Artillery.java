package org.newdawn.spaceinvaders.game_object.ingame.enemy;

import org.newdawn.spaceinvaders.game_object.logic.IHiveMindListener;
import org.newdawn.spaceinvaders.loop.GameLoop;

public class Artillery extends Enemy{
    public Artillery(GameLoop gameLoop) {
        super(gameLoop, 1);
    }

    @Override
    protected void addSprites() {
        frames.add("sprites/artilleryAlien.png");
    }

    @Override
    public void onBroadcast() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onBroadcast'");
    }
}

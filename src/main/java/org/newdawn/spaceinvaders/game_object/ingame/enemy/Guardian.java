package org.newdawn.spaceinvaders.game_object.ingame.enemy;

import org.newdawn.spaceinvaders.loop.GameLoop;
import org.newdawn.spaceinvaders.sprite.SpriteStore;

public class Guardian extends Enemy{
    public Guardian(GameLoop gameLoop){
        super(gameLoop, 2);
    }

    @Override
    public void onBroadcast() {
    }

    @Override
    protected void addSprites() {
        frames.add(SpriteStore.get().getSprite("sprites/shieldAlien1.gif"));
        frames.add(SpriteStore.get().getSprite("sprites/shieldAlien2.gif"));
        frames.add(SpriteStore.get().getSprite("sprites/shieldAlien3.gif"));
    }
}

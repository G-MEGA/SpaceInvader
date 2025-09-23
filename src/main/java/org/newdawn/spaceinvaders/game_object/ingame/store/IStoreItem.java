package org.newdawn.spaceinvaders.game_object.ingame.store;

import org.newdawn.spaceinvaders.game_object.ingame.PlayerShip;
import org.newdawn.spaceinvaders.loop.GameLoop;
import org.newdawn.spaceinvaders.sprite.Sprite;

public interface IStoreItem {
    public String getName();
    public String getSpriteRef();
    public void acquire(GameLoop gameLoop);
}

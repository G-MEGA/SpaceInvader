package org.newdawn.spaceinvaders.game_object.logic;

public interface IHiveMindListener {
    boolean isDestroyed();
    void onBroadcast();
}

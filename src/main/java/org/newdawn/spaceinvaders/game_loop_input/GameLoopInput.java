package org.newdawn.spaceinvaders.game_loop_input;

import java.io.Serializable;

public abstract class GameLoopInput implements Serializable {
    public GameLoopInput() {}

    public abstract String toSaveData();
}

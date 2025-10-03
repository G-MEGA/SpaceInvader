package org.newdawn.spaceinvaders.loop.game_loop;

import org.newdawn.spaceinvaders.loop_input.LoopInput;

import java.util.ArrayList;

public class GameLoopSnapshot {
    public ArrayList<LoopInput> inputs = new ArrayList<>();
    public byte[] state;
}

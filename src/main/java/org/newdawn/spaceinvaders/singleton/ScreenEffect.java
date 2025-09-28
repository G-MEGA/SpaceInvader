package org.newdawn.spaceinvaders.singleton;

import org.newdawn.spaceinvaders.game_object.effect.BlankScreenEffect;
import org.newdawn.spaceinvaders.loop.Loop;

public class ScreenEffect {
    private Loop loop;
    public void setLoop(Loop loop) { this.loop = loop; }

    private static ScreenEffect instance = new ScreenEffect();
    public static ScreenEffect getInstance()
    {
        return instance;
    }

    public void blankScreen(long duration) { new BlankScreenEffect(loop, duration); }
}

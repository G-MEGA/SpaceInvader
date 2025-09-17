package org.newdawn.spaceinvaders.game_object;

import org.newdawn.spaceinvaders.Game;
import org.newdawn.spaceinvaders.loop.Loop;

public abstract class Mover2D extends GameObject2D{
    public double velocityX;
    public double velocityY;

    public Mover2D(Loop loop) {
        super(loop);
    }

    @Override
    protected void process(double deltaTime) {
        super.process(deltaTime);

        transform.translate(velocityX * deltaTime, velocityY * deltaTime);
    }
}

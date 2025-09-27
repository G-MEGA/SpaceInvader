package org.newdawn.spaceinvaders.game_object.effect;

import org.newdawn.spaceinvaders.game_object.GameObject2D;
import org.newdawn.spaceinvaders.game_object.visual.SpriteRenderer;
import org.newdawn.spaceinvaders.loop.Loop;

public class BlankScreenEffect extends GameObject2D {
    private long elapsed = 0;
    private long duration;

    public BlankScreenEffect() {}

    public BlankScreenEffect(Loop loop, long duration) {
        this.duration = duration;

        SpriteRenderer spriteRenderer = new SpriteRenderer(loop);
        spriteRenderer.setSpriteRef("sprites/whiteBackground.png");
        spriteRenderer.setSortingLayer(100);
        addChild(spriteRenderer);

        loop.addGameObject(this);
    }

    @Override
    protected void process(long deltaTime) {
        if (elapsed >= duration){
            destroy();
        }
        else{
            elapsed += deltaTime;
        }
    }
}

package org.newdawn.spaceinvaders.game_object.effect;

import org.newdawn.spaceinvaders.game_object.GameObject2D;
import org.newdawn.spaceinvaders.game_object.visual.SpriteRenderer;
import org.newdawn.spaceinvaders.loop.GameLoop;

public class BlankScreenEffect extends GameObject2D {
    private long elapsed = 0;
    private long duration;

    public BlankScreenEffect() {
        // Kryo 역직렬화를 위한 매개변수 없는 생성자
    }

    public BlankScreenEffect(GameLoop gameLoop, long duration) {
        this.duration = duration;

        SpriteRenderer spriteRenderer = new SpriteRenderer(gameLoop);
        spriteRenderer.setSpriteRef("sprites/whiteBackground.png");
        spriteRenderer.setSortingLayer(100);
        addChild(spriteRenderer);

        gameLoop.addGameObject(this);
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

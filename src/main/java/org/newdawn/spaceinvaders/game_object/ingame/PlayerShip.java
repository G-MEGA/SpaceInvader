package org.newdawn.spaceinvaders.game_object.ingame;

import org.newdawn.spaceinvaders.game_object.Mover2D;
import org.newdawn.spaceinvaders.game_object.collision.Collider2D;
import org.newdawn.spaceinvaders.game_object.collision.ICollider2DOwner;
import org.newdawn.spaceinvaders.game_object.visual.SpriteRenderer;
import org.newdawn.spaceinvaders.loop.GameLoop;
import org.newdawn.spaceinvaders.sprite.SpriteStore;

public class PlayerShip extends Mover2D implements ICollider2DOwner {
    public PlayerShip(GameLoop gameLoop) {
        super(gameLoop);

        SpriteRenderer spriteRenderer = new SpriteRenderer(gameLoop);
        spriteRenderer.sprite = SpriteStore.get().getSprite("sprites/ship.gif");
        addChild(spriteRenderer);

        Collider2D collider2D = new Collider2D(gameLoop, this);
        collider2D.bounds.setRect(
                -spriteRenderer.sprite.getPivotX(),
                -spriteRenderer.sprite.getPivotY(),
                spriteRenderer.sprite.getWidth(),
                spriteRenderer.sprite.getHeight());
        addChild(collider2D);
    }

    protected void process(double deltaTime) {
        super.process(deltaTime);

        if ((velocityX < 0) && (getX() < 10)) {
            setX(10);
        }
        if ((velocityX > 0) && (getX() > 750)) {
            setX(750);
        }
    }

    @Override
    public void collidedWith(ICollider2DOwner collider) {
        if (collider instanceof Alien) {
            ((GameLoop)loop).notifyDeath();
        }
    }
}

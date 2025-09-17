package org.newdawn.spaceinvaders.game_object.ingame;

import org.newdawn.spaceinvaders.game_object.Mover2D;
import org.newdawn.spaceinvaders.game_object.collision.Collider2D;
import org.newdawn.spaceinvaders.game_object.collision.ICollider2DOwner;
import org.newdawn.spaceinvaders.game_object.visual.SpriteRenderer;
import org.newdawn.spaceinvaders.loop.GameLoop;
import org.newdawn.spaceinvaders.sprite.SpriteStore;

public class Bullet extends Mover2D implements ICollider2DOwner {
    /** The vertical speed at which the players shot moves */
    private double moveSpeed = -300;
    /** True if this shot has been "used", i.e. its hit something */
    private boolean used = false;

    public Bullet(GameLoop gameLoop) {
        super(gameLoop);

        SpriteRenderer spriteRenderer = new SpriteRenderer(gameLoop);
        spriteRenderer.sprite = SpriteStore.get().getSprite("sprites/shot.gif");
        addChild(spriteRenderer);

        Collider2D collider2D = new Collider2D(gameLoop, this);
        //TODO 스프라이트 피벗 구현
        collider2D.bounds.setRect(
                -spriteRenderer.sprite.getPivotX(),
                -spriteRenderer.sprite.getPivotY(),
                spriteRenderer.sprite.getWidth(),
                spriteRenderer.sprite.getHeight());
        addChild(collider2D);

        velocityY = moveSpeed;
    }

    protected void process(double deltaTime) {
        super.process(deltaTime);

        if(getY() < -100){
            destroy();
        }
    }

    @Override
    public void collidedWith(ICollider2DOwner collider) {
        // prevents double kills, if we've already hit something,
        // don't collide
        if (used || isDestroyed()) return;

        // if we've hit an alien, kill it!
        if (collider instanceof Alien) {
            Alien alien = (Alien) collider;

            if(alien.isDestroyed()) return;

            destroy();
            alien.destroy();

            used = true;
        }
    }
}

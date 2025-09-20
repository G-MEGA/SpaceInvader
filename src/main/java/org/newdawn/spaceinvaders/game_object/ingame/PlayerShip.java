package org.newdawn.spaceinvaders.game_object.ingame;

import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
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
        collider2D.boundsPosX = -spriteRenderer.sprite.getPivotX();
        collider2D.boundsPosY = -spriteRenderer.sprite.getPivotY();
        collider2D.boundsWidth = ((long)spriteRenderer.sprite.getWidth()) << 16;
        collider2D.boundsHeight = ((long)spriteRenderer.sprite.getHeight()) << 16;
        addChild(collider2D);
    }

    protected void process(long deltaTime) {
        super.process(deltaTime);

        if ((velocityX < 0L) && (getPosX() < (16 << 16) + FixedPointUtil.ZERO_5)) {
            setPosX((16 << 16) + FixedPointUtil.ZERO_5);
        }
        if ((velocityX > 0L) && (getPosX() > ((800L << 16)) - (16 << 16) + FixedPointUtil.ZERO_5)) {
            setPosX(((800L << 16)) - (16 << 16) + FixedPointUtil.ZERO_5);
        }
    }

    @Override
    public void collidedWith(ICollider2DOwner collider) {
        if (collider instanceof Alien) {
            ((GameLoop)loop).notifyDeath();
        }
    }
}

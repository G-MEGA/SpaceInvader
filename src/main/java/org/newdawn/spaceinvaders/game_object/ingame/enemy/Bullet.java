package org.newdawn.spaceinvaders.game_object.ingame.enemy;

import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
import org.newdawn.spaceinvaders.game_object.Mover2D;
import org.newdawn.spaceinvaders.game_object.collision.Collider2D;
import org.newdawn.spaceinvaders.game_object.collision.ICollider2DOwner;
import org.newdawn.spaceinvaders.game_object.visual.SpriteRenderer;
import org.newdawn.spaceinvaders.loop.GameLoop;
import org.newdawn.spaceinvaders.sprite.SpriteStore;

public class Bullet extends Mover2D implements ICollider2DOwner {
    /** True if this shot has been "used", i.e. its hit something */
    private boolean used = false;

    public Bullet(GameLoop gameLoop) {
        super(gameLoop);

        SpriteRenderer spriteRenderer = new SpriteRenderer(gameLoop);
        spriteRenderer.sprite = SpriteStore.get().getSprite("sprites/shot.gif");
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

        if(getPosY() < FixedPointUtil.fromLong(-100)){
            destroy();
        }
    }

    @Override
    public void collidedWith(ICollider2DOwner collider) {
        // prevents double kills, if we've already hit something,
        // don't collide
        if (used || isDestroyed()) return;

        // if we've hit an Enemy, kill it!
        if (collider instanceof Enemy) {
            Enemy enemy = (Enemy) collider;

            if(enemy.isDestroyed()) return;

            destroy();
            enemy.onHitByBullet();

            used = true;
        }
    }
}

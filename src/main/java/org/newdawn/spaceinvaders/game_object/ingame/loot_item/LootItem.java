package org.newdawn.spaceinvaders.game_object.ingame.loot_item;

import org.newdawn.spaceinvaders.game_object.Mover2D;
import org.newdawn.spaceinvaders.game_object.collision.Collider2D;
import org.newdawn.spaceinvaders.game_object.collision.ICollider2DOwner;
import org.newdawn.spaceinvaders.game_object.ingame.PlayerShip;
import org.newdawn.spaceinvaders.game_object.visual.SpriteRenderer;
import org.newdawn.spaceinvaders.loop.Loop;
import org.newdawn.spaceinvaders.sprite.SpriteStore;

public abstract class LootItem extends Mover2D implements ICollider2DOwner {
    private Long moveSpeed = 100L;

    public LootItem(Loop loop) {
        super(loop);

        SpriteRenderer spriteRenderer = new SpriteRenderer(loop);
        spriteRenderer.sprite = SpriteStore.get().getSprite("sprites/testLootItem.png");
        addChild(spriteRenderer);

        Collider2D collider2D = new Collider2D(loop, this);
        collider2D.bounds.setRect(
            -spriteRenderer.sprite.getPivotX(),
            -spriteRenderer.sprite.getPivotY(),
            spriteRenderer.sprite.getWidth(),
            spriteRenderer.sprite.getHeight());
        addChild(collider2D);
            
        velocityY = moveSpeed;
        loop.addGameObject(this);
    }

    public void collidedWith(ICollider2DOwner collider){
        if (collider instanceof PlayerShip){
            onCollideWithPlayerShip();
        }

        destroy();
    }

    protected abstract void onCollideWithPlayerShip();
}

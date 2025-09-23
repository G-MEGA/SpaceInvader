package org.newdawn.spaceinvaders.game_object.ingame.store;

import org.newdawn.spaceinvaders.game_object.GameObject2D;
import org.newdawn.spaceinvaders.game_object.collision.ICollider2DOwner;
import org.newdawn.spaceinvaders.game_object.visual.SpriteRenderer;
import org.newdawn.spaceinvaders.loop.GameLoop;
import org.newdawn.spaceinvaders.loop.Loop;

public class StoreSlot extends GameObject2D implements ICollider2DOwner {
    private IStoreItem item;

    public StoreSlot(Loop loop, IStoreItem item) {
        super(loop);
        this.item = item;

        SpriteRenderer spriteRenderer = new SpriteRenderer(loop);
        spriteRenderer.setSpriteRef(item.getSpriteRef());
        
        addChild(spriteRenderer);
    }

    @Override
    public void collidedWith(ICollider2DOwner collider) {
        if (collider instanceof GameLoop){
            GameLoop playerShip = (GameLoop)collider;
            item.acquire(playerShip);
        }
    }
}

package org.newdawn.spaceinvaders.game_object.ingame.loot_item;

import org.newdawn.spaceinvaders.game_object.GameObject;
import org.newdawn.spaceinvaders.game_object.Mover2D;
import org.newdawn.spaceinvaders.game_object.collision.Collider2D;
import org.newdawn.spaceinvaders.game_object.collision.ICollider2DOwner;
import org.newdawn.spaceinvaders.game_object.ingame.PlayerShip;
import org.newdawn.spaceinvaders.game_object.visual.SpriteRenderer;
import org.newdawn.spaceinvaders.loop.GameLoop;
import org.newdawn.spaceinvaders.loop.Loop;
import org.newdawn.spaceinvaders.sprite.SpriteStore;

public abstract class LootItem extends Mover2D implements ICollider2DOwner {
    private Long moveSpeed = 100L << 16;

    public LootItem(Loop loop, String ref) {
        super(loop);

        SpriteRenderer spriteRenderer = new SpriteRenderer(loop);
        spriteRenderer.sprite = SpriteStore.get().getSprite(ref);
        addChild(spriteRenderer);

        Collider2D collider2D = new Collider2D(loop, this);
        collider2D.boundsPosX = -spriteRenderer.sprite.getPivotX();
        collider2D.boundsPosY = -spriteRenderer.sprite.getPivotY();
        collider2D.boundsWidth = ((long)spriteRenderer.sprite.getWidth()) << 16;
        collider2D.boundsHeight = ((long)spriteRenderer.sprite.getHeight()) << 16;
        addChild(collider2D);
            
        velocityY = moveSpeed;
        loop.addGameObject(this);
    }

    public void collidedWith(ICollider2DOwner collider){
        if (collider instanceof PlayerShip){
            onCollideWithPlayerShip();

            destroy();
        }
    }

    protected abstract void onCollideWithPlayerShip();
    
    @Override
    protected void process(long deltaTime) {
        super.process(deltaTime);

        if (getPosY() > 800 << 16){
            System.out.println(getPosY());
            destroy();
        }
    }
}

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

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public LootItem(){
        super();
    }
    public LootItem(Loop loop, String ref) {
        super(loop);

        SpriteRenderer spriteRenderer = new SpriteRenderer(loop);
        spriteRenderer.setSpriteRef(ref);
        addChild(spriteRenderer);

        Collider2D collider2D = new Collider2D(loop, this);
        collider2D.boundsPosX = -spriteRenderer.getSpritePivotX();
        collider2D.boundsPosY = -spriteRenderer.getSpritePivotY();
        collider2D.boundsWidth = ((long)spriteRenderer.getSpriteWidth()) << 16;
        collider2D.boundsHeight = ((long)spriteRenderer.getSpriteHeight()) << 16;
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

    /**
     * 플레이어가 LootItem을 먹었을 때, 발동할 효과를 구현함
     */
    protected abstract void onCollideWithPlayerShip();
    
    @Override
    protected void process(long deltaTime) {
        super.process(deltaTime);

        if (getPosY() > 800 << 16){
            destroy();
        }
    }
}

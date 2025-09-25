package org.newdawn.spaceinvaders.game_object.ingame;

import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
import org.newdawn.spaceinvaders.game_object.GameObject2D;
import org.newdawn.spaceinvaders.game_object.collision.Collider2D;
import org.newdawn.spaceinvaders.game_object.collision.ICollider2DOwner;
import org.newdawn.spaceinvaders.game_object.ingame.enemy.Enemy;
import org.newdawn.spaceinvaders.game_object.visual.SpriteRenderer;
import org.newdawn.spaceinvaders.loop.Loop;

public class Laser extends GameObject2D implements ICollider2DOwner{
    private final String spriteRef = "sprites/testLaser.png";
    private final int damage = 10;
    private final long lifeDuration = FixedPointUtil.ZERO_5;
    private long spawnElapsed = 0;
    private SpriteRenderer spriteRenderer;

    public Laser(Loop loop, long spawnPosX, long spawnPosY, long spawnOffset, long spawnAngle){
        super(loop);

        spriteRenderer = new SpriteRenderer(loop);
        spriteRenderer.setSpriteRef(spriteRef);

        Collider2D collider2d = new Collider2D(loop, this);
        collider2d.boundsPosX = -spriteRenderer.getSpritePivotX();
        collider2d.boundsPosY = -spriteRenderer.getSpritePivotY();
        collider2d.boundsWidth = ((long)spriteRenderer.getSpriteWidth()) << 16;
        collider2d.boundsHeight = ((long)spriteRenderer.getSpriteHeight()) << 16;
        
        setRotation(spawnAngle);
        
        spawnAngle -= 90 << 16;
        spawnPosX += FixedPointUtil.mul(FixedPointUtil.cos(spawnAngle), FixedPointUtil.fromLong(512)); //! 아니 이거랑 뭐가 다름?? FixedPointUtil.mul(FixedPointUtil.cos(spawnAngle), FixedPointUtil.div(-spriteRenderer.getSpriteHeight(), 2 << 16))
        spawnPosY += FixedPointUtil.mul(FixedPointUtil.sin(spawnAngle), FixedPointUtil.fromLong(512)); //! 아니 이거랑 뭐가 다름?? FixedPointUtil.mul(FixedPointUtil.sin(spawnAngle), FixedPointUtil.div(-spriteRenderer.getSpriteHeight(), 2 << 16))
        spawnPosX += FixedPointUtil.mul(FixedPointUtil.cos(spawnAngle), FixedPointUtil.fromLong(spawnOffset));
        spawnPosY += FixedPointUtil.mul(FixedPointUtil.sin(spawnAngle), FixedPointUtil.fromLong(spawnOffset));
        setPos(spawnPosX, spawnPosY);
        
        addChild(spriteRenderer);
        addChild(collider2d);
    }

    @Override
    protected void process(long deltaTime) {
        super.process(deltaTime);
        if (isDestroyed()) { return; }

        if (spawnElapsed >= lifeDuration){
            destroy();
        }
        else{
            spawnElapsed += deltaTime;
        }
    }

    @Override
    public void collidedWith(ICollider2DOwner collider) {
        if (collider instanceof Enemy){
            Enemy enemy = (Enemy)collider;
            enemy.onHit(damage);
        }
    }
}   

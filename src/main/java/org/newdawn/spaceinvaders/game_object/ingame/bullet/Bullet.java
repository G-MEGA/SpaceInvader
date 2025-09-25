package org.newdawn.spaceinvaders.game_object.ingame.bullet;

import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
import org.newdawn.spaceinvaders.game_object.Mover2D;
import org.newdawn.spaceinvaders.game_object.collision.Collider2D;
import org.newdawn.spaceinvaders.game_object.collision.ICollider2DOwner;
import org.newdawn.spaceinvaders.game_object.ingame.enemy.Enemy;
import org.newdawn.spaceinvaders.game_object.visual.SpriteRenderer;
import org.newdawn.spaceinvaders.loop.GameLoop;

public abstract class Bullet extends Mover2D implements ICollider2DOwner {
     /** True if this shot has been "used", i.e. its hit something */
    protected boolean used = false;

    public Bullet(GameLoop gameLoop, long spawnAngle, long spawnCentralX, long spawnCentralY, long spawnOffset, long spawnSpeed) {
        super(gameLoop);

        SpriteRenderer spriteRenderer = new SpriteRenderer(gameLoop);
        spriteRenderer.setSpriteRef("sprites/shot.gif");
        addChild(spriteRenderer);

        Collider2D collider2D = new Collider2D(gameLoop, this);
        collider2D.boundsPosX = -spriteRenderer.getSpritePivotX();
        collider2D.boundsPosY = -spriteRenderer.getSpritePivotY();
        collider2D.boundsWidth = ((long)spriteRenderer.getSpriteWidth()) << 16;
        collider2D.boundsHeight = ((long)spriteRenderer.getSpriteHeight()) << 16;
        addChild(collider2D);
        
        setRotation(spawnAngle);
        
        //* 현재 Bullet Sprite가 -90도 회전 되어있어서 이를 맞추기위한 연산
        spawnAngle += 90 << 16;
        long spawnX = spawnCentralX + FixedPointUtil.mul(FixedPointUtil.cos(spawnAngle), FixedPointUtil.fromLong(spawnOffset));
        long spawnY = spawnCentralY + FixedPointUtil.mul(FixedPointUtil.sin(spawnAngle), FixedPointUtil.fromLong(spawnOffset));
        setPosX(spawnX);
        setPosY(spawnY);

        velocityX = FixedPointUtil.mul(FixedPointUtil.cos(spawnAngle), spawnSpeed);
        velocityY = FixedPointUtil.mul(FixedPointUtil.sin(spawnAngle), spawnSpeed);
    }

    protected void process(long deltaTime) {
        super.process(deltaTime);

        //* 총알이 화면 밖으로 벗어나면 없어지게 함.
        if ( 
            getPosY() < FixedPointUtil.fromLong(-100) || 
            getPosY() > FixedPointUtil.fromLong(700) || 
            getPosX() < FixedPointUtil.fromLong(-100) ||
            getPosX() > FixedPointUtil.fromLong(900)){
            destroy();
        }
    }

    @Override
    public void collidedWith(ICollider2DOwner collider) {
        // prevents double kills, if we've already hit something,
        // don't collide
        if (used || isDestroyed()) return;
    }
}

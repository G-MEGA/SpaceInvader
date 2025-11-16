package org.newdawn.spaceinvaders.game_object.ingame.player;

import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
import org.newdawn.spaceinvaders.game_object.GameCharacter;
import org.newdawn.spaceinvaders.game_object.collision.Collider2D;
import org.newdawn.spaceinvaders.game_object.collision.ICollider2DOwner;
import org.newdawn.spaceinvaders.game_object.ingame.bullet.EnemyBullet;
import org.newdawn.spaceinvaders.game_object.visual.SpriteRenderer;
import org.newdawn.spaceinvaders.loop.Loop;

public class Barrier extends GameCharacter {
    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public Barrier(){
        super();
    }
    public Barrier(Loop loop, Long spawnX, Long spawnY, Long spawnOffset, Long spawnAngle) {
        super(loop, 3);

        spriteRenderer = new SpriteRenderer(loop);
        spriteRenderer.setSpriteRef("sprites/testBarrier.png");

        Collider2D collider2D = new Collider2D(loop, this);
        collider2D.boundsPosX = -spriteRenderer.getSpritePivotX();
        collider2D.boundsPosY = -spriteRenderer.getSpritePivotY();
        collider2D.boundsWidth = ((long)spriteRenderer.getSpriteWidth()) << 16;
        collider2D.boundsHeight = ((long)spriteRenderer.getSpriteHeight()) << 16;

        addChild(spriteRenderer);
        addChild(collider2D);
        setRotation(spawnAngle);

        spawnAngle -= 90 << 16; //* Barrier의 이미지가 90도 회전 되어 있으므로 이를 맞추기 위한 작업임
        spawnX += FixedPointUtil.mul(spawnOffset, FixedPointUtil.cos(spawnAngle));
        spawnY += FixedPointUtil.mul(spawnOffset, FixedPointUtil.sin(spawnAngle));
        setPos(spawnX, spawnY);
    }

    @Override
    public void collidedWith(ICollider2DOwner collider) {
        if (collider instanceof EnemyBullet){
            EnemyBullet enemyBullet = (EnemyBullet)collider;
            enemyBullet.onHitByPlayerShip();
            
            decreaseHealth();

            if (isDead()){
                destroy();
            }
        }
    }
}

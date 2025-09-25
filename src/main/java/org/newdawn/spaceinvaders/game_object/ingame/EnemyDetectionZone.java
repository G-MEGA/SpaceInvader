package org.newdawn.spaceinvaders.game_object.ingame;

import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
import org.newdawn.spaceinvaders.game_object.GameObject;
import org.newdawn.spaceinvaders.game_object.GameObject2D;
import org.newdawn.spaceinvaders.game_object.collision.Collider2D;
import org.newdawn.spaceinvaders.game_object.collision.ICollider2DOwner;
import org.newdawn.spaceinvaders.game_object.ingame.bullet.EnemyBullet;
import org.newdawn.spaceinvaders.game_object.ingame.enemy.Enemy;
import org.newdawn.spaceinvaders.game_object.visual.SpriteRenderer;
import org.newdawn.spaceinvaders.loop.GameLoop;
import org.newdawn.spaceinvaders.loop.Loop;

public class EnemyDetectionZone extends GameObject2D implements ICollider2DOwner{
    private static final long detectRange = 30 << 16;
    private static final int detectScore = 1;

    public EnemyDetectionZone(GameLoop loop, PlayerShip ship) {
        super(loop);

        Collider2D collider2d = new Collider2D(loop, this);

        SpriteRenderer spriteRenderer = ship.getSpriteRenderer();
        collider2d.boundsPosX = -spriteRenderer.getSpritePivotX() - detectRange;
        collider2d.boundsPosY = -spriteRenderer.getSpritePivotY() - detectRange;
        collider2d.boundsWidth = (((long)spriteRenderer.getSpriteWidth()) << 16) + FixedPointUtil.mul(detectRange, 2 << 16);
        collider2d.boundsHeight = (((long)spriteRenderer.getSpriteHeight()) << 16) + FixedPointUtil.mul(detectRange, 2 << 16);
        
        setPos(ship.getPosX(), ship.getPosY());
    }

    @Override
    public void collidedWith(ICollider2DOwner collider) {
        if (collider instanceof Enemy || collider instanceof EnemyBullet){
            System.out.println("안녕");
            GameLoop gameLoop = (GameLoop)loop;
            gameLoop.increaseScore(detectScore);
        }
    }
    
}

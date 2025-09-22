package org.newdawn.spaceinvaders.game_object.ingame;

import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
import org.newdawn.spaceinvaders.game_object.GameCharacter;
import org.newdawn.spaceinvaders.game_object.Mover2D;
import org.newdawn.spaceinvaders.game_object.collision.Collider2D;
import org.newdawn.spaceinvaders.game_object.collision.ICollider2DOwner;
import org.newdawn.spaceinvaders.game_object.ingame.enemy.Alien;
import org.newdawn.spaceinvaders.game_object.ingame.enemy.Bullet;
import org.newdawn.spaceinvaders.game_object.ingame.enemy.Enemy;
import org.newdawn.spaceinvaders.game_object.visual.SpriteRenderer;
import org.newdawn.spaceinvaders.loop.GameLoop;
import org.newdawn.spaceinvaders.sprite.SpriteStore;

public class PlayerShip extends GameCharacter{
    /** The speed at which the player's ship should move (pixels/sec) */
    private long moveSpeed = 300L << 16;
    /** The time at which last fired a shot */
    private long lastFire = 0L;
    /** The interval between our players shot (ms) */
    private long firingInterval = FixedPointUtil.ZERO_1;
    
    // private long shieldCount = 0;
    // public void addShield(){ addShield(1); }
    // public void addShield(long count){ shieldCount += count; }
    // public long getShieldCount() { return shieldCount; }

    private Boolean isSpeedUp = false;
    private long speedUpRatio = 2 << 16 + FixedPointUtil.ZERO_5;
    private long speedUpTime = 4 << 16;
    private long speedUpElapsed = 0;
    public void requestToSpeedUp(){
        isSpeedUp = true;
        speedUpElapsed = 0;
    }

    public PlayerShip(GameLoop gameLoop) {
        super(gameLoop, 3);

        SpriteRenderer spriteRenderer = new SpriteRenderer(gameLoop);
        spriteRenderer.setSpriteRef("sprites/ship.gif");
        addChild(spriteRenderer);

        Collider2D collider2D = new Collider2D(gameLoop, this);
        collider2D.boundsPosX = -spriteRenderer.getSpritePivotX();
        collider2D.boundsPosY = -spriteRenderer.getSpritePivotY();
        collider2D.boundsWidth = ((long)spriteRenderer.getSpriteWidth()) << 16;
        collider2D.boundsHeight = ((long)spriteRenderer.getSpriteHeight()) << 16;
        addChild(collider2D);
    }

    protected void process(long deltaTime) {
        super.process(deltaTime);

        GameLoop gameLoop = (GameLoop)loop;

        if(!gameLoop.isWaitingForKeyPress()){
            //region 이동
            velocityX = 0;
            if ((gameLoop.isKeyInputPressed("left")) && (!gameLoop.isKeyInputPressed("right"))) {
                velocityX = -moveSpeed;
            } else if ((gameLoop.isKeyInputPressed("right")) && (!gameLoop.isKeyInputPressed("left"))) {
                velocityX = moveSpeed;
            }

            velocityY = 0;
            if ((gameLoop.isKeyInputPressed("up")) && (!gameLoop.isKeyInputPressed("down"))) {
                velocityY = -moveSpeed;
            } else if ((gameLoop.isKeyInputPressed("down")) && (!gameLoop.isKeyInputPressed("up"))) {
                velocityY = moveSpeed;
            }
            
            //* 만약 SpeedUp된 상태면 PlayerShip의 이동 속도를 증가 시킴
            if (isSpeedUp){
                if (speedUpElapsed >= speedUpTime){
                    isSpeedUp = false;
                    speedUpElapsed = 0;
                }
                else{
                    speedUpElapsed += deltaTime;
                    velocityX = FixedPointUtil.mul(velocityX, speedUpRatio);
                    velocityY = FixedPointUtil.mul(velocityY, speedUpRatio);
                }
            }

            //endregion

            //region 마우스를 향해 회전
            long mousePosX = FixedPointUtil.fromLong(gameLoop.getMousePosX());
            long mousePosY = FixedPointUtil.fromLong(gameLoop.getMousePosY());

            long fromMeToMouseX = mousePosX - getPosX();
            long fromMeToMouseY = mousePosY - getPosY();

            setRotation(FixedPointUtil.atan2(fromMeToMouseY,  fromMeToMouseX) + (90 << 16));
            //endregion

            // 탄 발사
            if (gameLoop.isKeyInputPressed("mouse_button_left")) {
                tryToFire();
            }
        }

        //region 화면 밖으로 나가지 못하게 제약
        if ((velocityX <= 0L) && (getPosX() < (16 << 16) + FixedPointUtil.ZERO_5)) {
            setPosX((16 << 16) + FixedPointUtil.ZERO_5);
        }
        if ((velocityX >= 0L) && (getPosX() > ((800L << 16)) - (16 << 16) + FixedPointUtil.ZERO_5)) {
            setPosX(((800L << 16)) - (16 << 16) + FixedPointUtil.ZERO_5);
        }

        if ((velocityY <= 0L) && (getPosY() < (16 << 16) + FixedPointUtil.ZERO_5)) {
            setPosY((16 << 16) + FixedPointUtil.ZERO_5);
        }
        if ((velocityY >= 0L) && (getPosY() > ((600L << 16)) - (16 << 16) + FixedPointUtil.ZERO_5)) {
            setPosY(((600L << 16)) - (16 << 16) + FixedPointUtil.ZERO_5);
        }
        //endregion
    }

    @Override
    public void collidedWith(ICollider2DOwner collider) {
        if (collider instanceof Enemy) {
            if (_health == 0){
                ((GameLoop)loop).notifyDeath();
            }
            else{
                _health -= 1;
            }
        }
    }

    /**
     * Attempt to fire a shot from the player. Its called "try"
     * since we must first check that the player can fire at this
     * point, i.e. has he/she waited long enough between shots
     */
    public void tryToFire() {
        GameLoop gameLoop = (GameLoop)loop;
        // check that we have waiting long enough to fire
        if (gameLoop.getCurrentTime() - lastFire < firingInterval) {
            return;
        }

        // if we waited long enough, create the shot entity, and record the time.
        lastFire = gameLoop.getCurrentTime();

        Bullet bullet = new Bullet(gameLoop);

        bullet.setRotation(getRotation());

        long r = bullet.getRotation() + (90 << 16);

        bullet.setPos(
                getPosX() + FixedPointUtil.mul(FixedPointUtil.cos(r), FixedPointUtil.fromLong(-30)),
                getPosY() + FixedPointUtil.mul(FixedPointUtil.sin(r), FixedPointUtil.fromLong(-30))
        );

        long bulletSpeed = FixedPointUtil.fromLong(-300);
        bullet.velocityX = FixedPointUtil.mul(FixedPointUtil.cos(r), bulletSpeed);
        bullet.velocityY = FixedPointUtil.mul(FixedPointUtil.sin(r), bulletSpeed);

        gameLoop.addGameObject(bullet);
    }
}

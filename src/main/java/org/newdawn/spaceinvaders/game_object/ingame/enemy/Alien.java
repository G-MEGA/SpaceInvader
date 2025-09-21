package org.newdawn.spaceinvaders.game_object.ingame.enemy;

import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
import org.newdawn.spaceinvaders.game_object.Mover2D;
import org.newdawn.spaceinvaders.game_object.collision.Collider2D;
import org.newdawn.spaceinvaders.game_object.collision.ICollider2DOwner;
import org.newdawn.spaceinvaders.game_object.logic.HiveMind;
import org.newdawn.spaceinvaders.game_object.logic.IHiveMindListener;
import org.newdawn.spaceinvaders.game_object.visual.SpriteRenderer;
import org.newdawn.spaceinvaders.loop.GameLoop;
import org.newdawn.spaceinvaders.sprite.Sprite;
import org.newdawn.spaceinvaders.sprite.SpriteStore;

public class Alien extends Enemy implements ICollider2DOwner, IHiveMindListener {
    private long moveSpeed = 100L << 16;

    public Alien(GameLoop gameLoop, HiveMind hiveMind){
        super(gameLoop, hiveMind);

        velocityX = -moveSpeed;
    }

    @Override
    protected void addSprites(){
        frames.add(SpriteStore.get().getSprite("sprites/alien2.gif"));
        frames.add(SpriteStore.get().getSprite("sprites/alien.gif"));
        frames.add(SpriteStore.get().getSprite("sprites/alien.gif"));
        frames.add(SpriteStore.get().getSprite("sprites/alien3.gif"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((GameLoop)loop).notifyAlienKilled();// GameLoop에 부고소식 전달
    }

    public void onBroadcast(){
        goForwardAndCheckDeath();
    }

    @Override
    protected void process(long deltaTime) {
        super.process(deltaTime);

        // if we have reached the left hand side of the screen and
        // are moving left then request a logic update
        if ((velocityX < 0) && (getPosX() < (10 << 16))) {
            onCollideWall();
        }
        // and vice vesa, if we have reached the right hand side of
        // the screen and are moving right, request a logic update
        if ((velocityX > 0) && (getPosX() > (750 << 16))) {
            onCollideWall();
        }
    }

    private void onCollideWall(){
        if(hiveMind == null) {
            goForwardAndCheckDeath();
        }
        else{
            hiveMind.requestBroadcast();
        }
    }

    private void goForwardAndCheckDeath(){
        velocityX = -velocityX;
        
        setPosY(getPosY() + (10 << 16));
        if (getPosY() > (570 << 16)) {
            ((GameLoop)loop).notifyDeath();
        }
    }
}
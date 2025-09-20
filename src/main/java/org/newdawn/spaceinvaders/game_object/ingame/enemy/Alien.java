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

public class Alien extends Mover2D implements ICollider2DOwner, IHiveMindListener {
    private long moveSpeed = 75L << 16;

    private SpriteRenderer spriteRenderer;

    private HiveMind hiveMind;

    private Sprite[] frames = new Sprite[4];
    private long lastFrameChange = 0L;
    private long frameDuration = FixedPointUtil.ZERO_25;
    /** The current frame of animation being displayed */
    private int frameNumber;

    public Alien(GameLoop gameLoop){
        super(gameLoop);

        frames[0] = SpriteStore.get().getSprite("sprites/alien.gif");
        frames[1] = SpriteStore.get().getSprite("sprites/alien2.gif");
        frames[2] = SpriteStore.get().getSprite("sprites/alien.gif");
        frames[3] = SpriteStore.get().getSprite("sprites/alien3.gif");

        spriteRenderer = new SpriteRenderer(gameLoop);
        spriteRenderer.sprite = frames[0];
        addChild(spriteRenderer);

        Collider2D collider2D = new Collider2D(gameLoop, this);
        collider2D.boundsPosX = -spriteRenderer.sprite.getPivotX();
        collider2D.boundsPosY = -spriteRenderer.sprite.getPivotY();
        collider2D.boundsWidth = ((long)spriteRenderer.sprite.getWidth()) << 16;
        collider2D.boundsHeight = ((long)spriteRenderer.sprite.getHeight()) << 16;
        addChild(collider2D);

        velocityX = -moveSpeed;
    }
    public Alien(GameLoop gameLoop, HiveMind hiveMind){
        this(gameLoop);
        this.hiveMind = hiveMind;
        this.hiveMind.addListener(this);
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

        // since the move tells us how much time has passed
        // by we can use it to drive the animation, however
        // its the not the prettiest solution
        lastFrameChange += deltaTime;

        // if we need to change the frame, update the frame number
        // and flip over the sprite in use
        if (lastFrameChange > frameDuration) {
            // reset our frame change time counter
            lastFrameChange = 0;

            // update the frame
            frameNumber++;
            if (frameNumber >= frames.length) {
                frameNumber = 0;
            }

            spriteRenderer.sprite = frames[frameNumber];
        }

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

    @Override
    public void collidedWith(ICollider2DOwner collider) {
        // collisions with aliens are handled elsewhere
    }
}
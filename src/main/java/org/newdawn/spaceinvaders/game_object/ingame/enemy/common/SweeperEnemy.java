package org.newdawn.spaceinvaders.game_object.ingame.enemy.common;

import org.newdawn.spaceinvaders.game_object.ingame.enemy.Enemy;
import org.newdawn.spaceinvaders.game_object.logic.HiveMind;
import org.newdawn.spaceinvaders.loop.GameLoop;

public abstract class SweeperEnemy extends Enemy {
    private long moveSpeed = 100L << 16;

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public SweeperEnemy(){
        super();
    }
    public SweeperEnemy(GameLoop gameLoop, HiveMind hiveMind, long initialHealth) {
        super(gameLoop, hiveMind, initialHealth);

        velocityX = -moveSpeed;
    }

    public void onBroadcast(){
        goForwardAndCheckGameOverLine();
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

        if (getPosY() > (570 << 16)) {
            onTouchGameOverLine();
        }
    }

    private void onCollideWall(){
        if(hiveMind == null) {
            goForwardAndCheckGameOverLine();
        }
        else{
            hiveMind.requestBroadcast();
        }
    }

    private void goForwardAndCheckGameOverLine(){
        velocityX = -velocityX;
        
        setPosY(getPosY() + (10 << 16));
    }

    protected void onTouchGameOverLine(){
        ((GameLoop)getLoop()).notifyLose();
    }
}

package org.newdawn.spaceinvaders.game_object.ingame.enemy.common;

import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
import org.newdawn.spaceinvaders.game_object.ingame.player.PlayerShip;
import org.newdawn.spaceinvaders.game_object.logic.HiveMind;
import org.newdawn.spaceinvaders.loop.GameLoop;

public class Raider extends SweeperEnemy{
    private Boolean _hasCharged = false;

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public Raider(){
        super();
    }
    public Raider(GameLoop gameLoop, HiveMind hiveMind) {
        super(gameLoop, hiveMind, 1);

        //* 유저를 생성시 아래 방향을 바라보도록 하는 작업
        setRotation(180);
    }

    @Override
    protected void addSprites() {
        frames.add("sprites/enemy/raider1.png");
        frames.add("sprites/enemy/raider2.png");
        frames.add("sprites/enemy/raider3.png");
    }
    @Override
    protected void addHitSprites() {
        onHitFrames.add("sprites/enemy/enemyOnHit1.png");
        onHitFrames.add("sprites/enemy/enemyOnHit2.png"); 
        onHitFrames.add("sprites/enemy/enemyOnHit3.png");
    }

    @Override
    protected void process(long deltaTime) {
        super.process(deltaTime);

        if(!_hasCharged){
            for (int aliveShipIndex=0; aliveShipIndex < ((GameLoop)getLoop()).getAliveShipCount(); aliveShipIndex++) {
                PlayerShip playerShip = ((GameLoop)getLoop()).getAliveShip(aliveShipIndex);
                if (Math.abs(playerShip.getPosX() - getPosX()) < 29 << 16){
                    _hasCharged = true;
                    velocityX = 0;
                    velocityY = FixedPointUtil.fromLong(300L);
                    break;
                }
            }
        }
    }

    @Override
    protected void onTouchGameOverLine(){
        // 얘는 화면 아래에 닿아도 게임 오버가 되지 않도록 onTouchBoundary를 오버라이딩함
        decreaseHealth(getHealth() + 9999999999L);
    }
}

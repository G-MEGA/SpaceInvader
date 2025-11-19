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

    // 상수 추출로 매직 넘버(29 << 16) 제거 및 의미 부여
    private static final long DETECTION_RANGE_X = 29L << 16;
    private static final long CHARGE_SPEED_Y = FixedPointUtil.fromLong(300L);

    @Override
    protected void process(long deltaTime) {
        super.process(deltaTime);

        // 1. 이미 돌격했다면 로직 종료 (Guard Clause)
        if (_hasCharged) return;

        // 2. 탐색 후 조건 만족 시 돌격 실행
        if (isPlayerInDetectionRange()) {
            startCharge();
        }
    }

    /**
     * 범위 내에 플레이어가 있는지 탐색합니다.
     * (탐색 로직 분리 -> 복잡도 격리)
     */
    private boolean isPlayerInDetectionRange() {
        GameLoop gameLoop = (GameLoop) getLoop();
        int aliveCount = gameLoop.getAliveShipCount();

        for (int i = 0; i < aliveCount; i++) {
            PlayerShip playerShip = gameLoop.getAliveShip(i);

            // X축 거리가 감지 범위 내인지 확인
            if (Math.abs(playerShip.getPosX() - getPosX()) < DETECTION_RANGE_X) {
                return true; // 발견 즉시 true 반환 (break 불필요)
            }
        }
        return false;
    }

    /**
     * 돌격 상태로 전환하고 속도를 설정합니다.
     * (상태 변경 로직 분리)
     */
    private void startCharge() {
        _hasCharged = true;
        velocityX = 0;
        velocityY = CHARGE_SPEED_Y;
    }

    @Override
    protected void onTouchGameOverLine(){
        // 얘는 화면 아래에 닿아도 게임 오버가 되지 않도록 onTouchBoundary를 오버라이딩함
        decreaseHealth(getHealth() + 9999999999L);
    }
}

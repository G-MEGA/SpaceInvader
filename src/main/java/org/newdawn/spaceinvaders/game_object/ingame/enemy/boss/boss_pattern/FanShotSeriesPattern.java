package org.newdawn.spaceinvaders.game_object.ingame.enemy.boss.boss_pattern;

import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
import org.newdawn.spaceinvaders.game_object.ingame.bullet.EnemyBullet;
import org.newdawn.spaceinvaders.game_object.ingame.enemy.boss.Boss;
import org.newdawn.spaceinvaders.loop.GameLoop;
import org.newdawn.spaceinvaders.loop.Loop;

public class FanShotSeriesPattern extends BossPattern {
    private boolean isExecuted = false;

    private static final long shootInterval = FixedPointUtil.ZERO_2 + FixedPointUtil.ZERO_1;
    private long shootElapsed = 0;
    private int remainShootCount = 5;

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public FanShotSeriesPattern() {
        super();
    }

    public FanShotSeriesPattern(Loop loop, Boss boss) {
        super(loop, boss);
    }

    @Override
    protected void process(long deltaTime) {
        super.process(deltaTime);

        if (!isExecuted) return;

        if (remainShootCount == 0){
            notifyPatternEnd();
            destroy();
        }
        else{
            if (shootElapsed < shootInterval){
                shootElapsed += deltaTime;
                if (shootElapsed > shootInterval){
                    shootElapsed = shootInterval;
                }
            }
            else{
                shootBullets();
                remainShootCount--;
                shootElapsed = 0;
            }
        }
    }
    
    @Override
    public void executePattern() {
        System.out.println("FanShotSeriesPattern executed");
        isExecuted = true;
    }

    private void shootBullets() {
        //TODO boss 이미지을 파츠별로 나누게하기
        EnemyBullet bullet = new EnemyBullet((GameLoop)getLoop(), boss.getRotation(), 400l << 16, 300l << 16, 20l, FixedPointUtil.fromLong(200L));
        getLoop().addGameObject(bullet);
    }
}

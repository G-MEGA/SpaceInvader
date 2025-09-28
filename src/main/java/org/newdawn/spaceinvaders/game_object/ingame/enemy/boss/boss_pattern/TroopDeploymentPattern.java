package org.newdawn.spaceinvaders.game_object.ingame.enemy.boss.boss_pattern;

import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
import org.newdawn.spaceinvaders.game_object.ingame.enemy.Enemy;
import org.newdawn.spaceinvaders.game_object.ingame.enemy.boss.Boss;
import org.newdawn.spaceinvaders.game_object.ingame.enemy.common.Guardian;
import org.newdawn.spaceinvaders.game_object.ingame.enemy.common.Raider;
import org.newdawn.spaceinvaders.loop.GameLoop;
import org.newdawn.spaceinvaders.loop.Loop;

public class TroopDeploymentPattern extends BossPattern {
    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public TroopDeploymentPattern() {
        super();
    }

    public TroopDeploymentPattern(Loop loop, Boss boss) {
        super(loop, boss);
        
    }

    @Override
    public void executePattern() {
        System.out.println("TroopDeploymentPattern executed");
        // notifyPatternEnd();

        int spawnCount = 7; //* 갯수가 홀수임을 가정함
        long spawnPosXInterval = 200l << 16;
        long startSpawnPosX = FixedPointUtil.sub(boss.getPosX(), FixedPointUtil.mul(spawnPosXInterval, (spawnCount / 2) << 16));
        long raiderSpawnPosY = boss.getPosY() + (100 << 16);
        long guardianSpawnPosY = boss.getPosY() + (300 << 16);

        for (int i = 0;i < spawnCount; i++){
            Enemy enemy;
            if (i % 2 == 0){ //TODO EnemyFactory를 싱글톤으로 바꾸기
                // enemy = ;
            }
            else{
                // enemy = ;
            }
        }

        notifyPatternEnd();
    }
}

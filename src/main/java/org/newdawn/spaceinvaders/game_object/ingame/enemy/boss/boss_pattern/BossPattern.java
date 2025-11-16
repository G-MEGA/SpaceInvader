package org.newdawn.spaceinvaders.game_object.ingame.enemy.boss.boss_pattern;

import org.newdawn.spaceinvaders.game_object.GameObject;
import org.newdawn.spaceinvaders.game_object.ingame.enemy.boss.Boss;
import org.newdawn.spaceinvaders.loop.Loop;

public abstract class BossPattern extends GameObject {
    protected Boss boss;

    // 패턴 1: 오른쪽, 왼쪽에서 플레이어를 향해 10개의 탄환을 각각 0.7초의 간격으로 발사
    public static final int SIDE_BULLET_STREAM = 1;
    // 패턴 2: 가운데에서 부채꼴 형태로 9개의 탄환을 0.3초 간격으로 5번 발사
    public static final int FAN_SHOT_SERIES = 2;
    // 패턴 3: 플레이어에게 5초간 20% 둔화
    public static final int PLAYER_SLOW = 3;
    // 패턴 4: 방패병 3명, 돌격병 4명을 각각 가로축의 4등분선, 5등분선
    public static final int TROOP_DEPLOYMENT = 4;
    // 패턴 5: 가운데에 레이저 발사
    public static final int LASER = 5;
    
    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public BossPattern() {
        super();
    }

    public BossPattern(Loop loop, Boss boss){
        super(loop);
        this.boss = boss;
    }

    public abstract void executePattern();
    protected void notifyPatternEnd() { boss.endCurrentPattern(); }
}

package org.newdawn.spaceinvaders.game_object.ingame.enemy.boss.boss_pattern;

import org.newdawn.spaceinvaders.game_object.ingame.enemy.boss.Boss;
import org.newdawn.spaceinvaders.loop.Loop;

public class SideBulletStreamPattern extends BossPattern {
    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public SideBulletStreamPattern() {
        super();
    }

    public SideBulletStreamPattern(Loop loop, Boss boss) {
        super(loop, boss);
    }

    @Override
    public void executePattern() {
    }
}

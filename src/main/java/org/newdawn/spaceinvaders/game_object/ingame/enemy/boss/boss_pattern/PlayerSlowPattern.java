package org.newdawn.spaceinvaders.game_object.ingame.enemy.boss.boss_pattern;

import org.newdawn.spaceinvaders.game_object.ingame.enemy.boss.Boss;
import org.newdawn.spaceinvaders.loop.Loop;

public class PlayerSlowPattern extends BossPattern {
    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public PlayerSlowPattern() {
        super();
    }
    
    public PlayerSlowPattern(Loop loop, Boss boss) {
        super(loop, boss);
    }

    @Override
    public void executePattern() {
        System.out.println("PlayerSlowPattern executed");
        notifyPatternEnd();
    }
}

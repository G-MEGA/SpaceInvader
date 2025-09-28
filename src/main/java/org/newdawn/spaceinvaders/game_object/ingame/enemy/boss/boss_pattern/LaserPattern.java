/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package org.newdawn.spaceinvaders.game_object.ingame.enemy.boss.boss_pattern;

import java.util.ArrayList;

import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
import org.newdawn.spaceinvaders.game_object.ingame.enemy.EnemyWarner;
import org.newdawn.spaceinvaders.game_object.ingame.enemy.boss.Boss;
import org.newdawn.spaceinvaders.loop.Loop;


public class LaserPattern extends BossPattern {
    private static final long warningTime = FixedPointUtil.ZERO_5 + (1 << 16);

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public LaserPattern() {
        super();
    }   

    public LaserPattern(Loop loop, Boss boss) {
        super(loop, boss);
    }
    
    @Override
    public void executePattern() {
        System.out.println("LaserPattern executed");

        //TODO Laser 객체를 PlayerLaser와 EnemyLaser로 나누기
        notifyPatternEnd();
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package org.newdawn.spaceinvaders.game_object.ingame.enemy.boss.boss_pattern;

import org.newdawn.spaceinvaders.game_object.ingame.enemy.boss.Boss;
import org.newdawn.spaceinvaders.loop.Loop;


public class LaserPattern extends BossPattern {
    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public LaserPattern() {
        super();
    }   

    public LaserPattern(Loop loop, Boss boss) {
        super(loop, boss);
    }
    
    @Override
    public void executePattern() {
    }

}

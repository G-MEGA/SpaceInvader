/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package org.newdawn.spaceinvaders.game_object.ingame.enemy.boss.boss_pattern;

import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
import org.newdawn.spaceinvaders.game_object.ingame.EnemyLaser;
import org.newdawn.spaceinvaders.game_object.ingame.PlayerShip;
import org.newdawn.spaceinvaders.game_object.ingame.enemy.EnemyWarner;
import org.newdawn.spaceinvaders.game_object.ingame.enemy.boss.Boss;
import org.newdawn.spaceinvaders.loop.GameLoop;
import org.newdawn.spaceinvaders.loop.Loop;


public class LaserPattern extends BossPattern {
    private boolean isExecuted = false;
    
    private static final long warningTime = FixedPointUtil.ZERO_5 + (1 << 16);
    private long warnedElapsed;
    private static final long warnerSpawnIntervel = 100 << 16;

    private long spawnPosX;
    private long spawnPosY;
    private int spawnOffset;
    private long spawnAngle;

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public LaserPattern() {
        super();
    }   

    public LaserPattern(Loop loop, Boss boss) {
        super(loop, boss);
    }

    @Override
    protected void process(long deltaTime) {
        super.process(deltaTime);

        if (!isExecuted) return;
        if (warnedElapsed < warningTime){
            warnedElapsed += deltaTime;

            if (warnedElapsed >= warningTime) { warnedElapsed = warningTime; }
        }
        else{
            shootLaser();
            destroy();
        }
    }

    
    @Override
    public void executePattern() {
        System.out.println("LaserPattern executed");
        isExecuted = true;

        PlayerShip target = ((GameLoop)getLoop()).getRandomAlivePlayerShip();
        spawnPosX = boss.getPosX();
        spawnPosY = boss.getPosY(); 
        spawnAngle = FixedPointUtil.atan2(target.getPosY() - boss.getPosY(), target.getPosX() - boss.getPosX());
        spawnOffset = 60;
        
        long currentWarnerSpawnPosX = spawnPosX + FixedPointUtil.mul(FixedPointUtil.cos(spawnAngle), warnerSpawnIntervel);
        long currentWarnerSpawnPosY = spawnPosY + FixedPointUtil.mul(FixedPointUtil.sin(spawnAngle), warnerSpawnIntervel);

        while (currentWarnerSpawnPosX > 0 && currentWarnerSpawnPosX < (800 << 16)
            && currentWarnerSpawnPosY > 0 && currentWarnerSpawnPosY < (600 << 16)) {
            EnemyWarner warner = new EnemyWarner((GameLoop)getLoop(), currentWarnerSpawnPosX, currentWarnerSpawnPosY, spawnAngle + (270<< 16), warningTime);

            currentWarnerSpawnPosX += FixedPointUtil.mul(FixedPointUtil.cos(spawnAngle), warnerSpawnIntervel);
            currentWarnerSpawnPosY += FixedPointUtil.mul(FixedPointUtil.sin(spawnAngle), warnerSpawnIntervel);
            getLoop().addGameObject(warner);
        }
    }
    
    private void shootLaser() {
        spawnAngle += 90 << 16;
        EnemyLaser laser = new EnemyLaser(getLoop(), spawnPosX, spawnPosY, spawnOffset, spawnAngle);
        
        getLoop().addGameObject(laser);
        notifyPatternEnd();
    }
}

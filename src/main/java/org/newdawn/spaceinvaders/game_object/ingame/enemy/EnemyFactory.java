package org.newdawn.spaceinvaders.game_object.ingame.enemy;

import org.newdawn.spaceinvaders.game_object.GameObject;
import org.newdawn.spaceinvaders.game_object.ingame.PlayerShip;
import org.newdawn.spaceinvaders.game_object.logic.HiveMind;
import org.newdawn.spaceinvaders.loop.GameLoop;
import org.newdawn.spaceinvaders.loop.Loop;

//TODO 싱글톤으로 바꾸기
public class EnemyFactory extends GameObject{
    PlayerShip playerShip;
    GameLoop gameLoop;

    static public final int AILEN = 0;
    static public final int ARTILLERY = 1;
    static public final int GUARDIAN = 2;
    static public final int RAIDER = 3;

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public EnemyFactory(){
        super();
    }
    public EnemyFactory(GameLoop gameLoop, PlayerShip playerShip) {
        super(gameLoop);

        this.gameLoop = gameLoop;
        this.playerShip = playerShip;
    }

    public void spawnEnemy(HiveMind hiveMind, int enemyType, long spawnPosX, long spawnPosY) {
        spawnEnemy(hiveMind, enemyType, spawnPosX, spawnPosY, 0, true);
    }
    public void spawnEnemy(HiveMind hiveMind, int enemyType, long spawnPosX, long spawnPosY, long spawnAngle) {
        spawnEnemy(hiveMind, enemyType, spawnPosX, spawnPosY, spawnAngle, true);
    }
    public void spawnEnemy(HiveMind hiveMind, int enemyType, long spawnPosX, long spawnPosY, boolean isWarning){
        spawnEnemy(hiveMind, enemyType, spawnPosX, spawnPosY, 0, isWarning);
    }

    public void spawnEnemy(HiveMind hiveMind, int enemyType, long spawnPosX, long spawnPosY, long spawnAngle, boolean isWarning) {
        Enemy enemy = null;
        
        switch (enemyType) {
            case AILEN:
                enemy = new Alien(gameLoop, hiveMind);
                break;
            case ARTILLERY:
                enemy = new Artillery(gameLoop, hiveMind, playerShip);
                break;
            case GUARDIAN:
                enemy = new Guardian(gameLoop, hiveMind);
                break;
            case RAIDER:
                enemy = new Raider(gameLoop, hiveMind, playerShip);
                break;
        }
        if (enemy == null){
            System.err.println(enemyType + "은 존재하지 않은 enemyType 입니다.");
            return;
        }
        
        enemy.setPos(spawnPosX, spawnPosY);
        enemy.setRotation(spawnAngle);
        hiveMind.addListener(enemy);
        
        if (isWarning){
            EnemyWarner enemyWarner = new EnemyWarner(enemy, gameLoop);
            gameLoop.addGameObject(enemyWarner);
            return;
        }

        gameLoop.addGameObject(enemy);
    }
}


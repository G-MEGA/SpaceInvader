package org.newdawn.spaceinvaders.loop.game_loop.game_loop_components;

import java.util.ArrayList;

import org.newdawn.spaceinvaders.game_object.ingame.enemy.Enemy;
import org.newdawn.spaceinvaders.loop.GameLoop;

public class GameLoopEnemySystem {
    private GameLoop gameLoop;

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public GameLoopEnemySystem() {
    }

    public GameLoopEnemySystem(GameLoop gameLoop) {
        this.gameLoop = gameLoop;
    }

    private boolean hasEnemy = false;
    public boolean HasEnemy() { return hasEnemy;}

    public void notifyEnemyInstantiated() { this.hasEnemy = true; }
    private ArrayList<Enemy> enemies = new ArrayList<>();
    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
        gameLoop.addGameObject(enemy);
    }
    
    public void cleanUpEnemies() {
        //* 파괴된 enemy를 enemies 리스트에서 제거
        for(int i=enemies.size() - 1 ; i > -1;i--){
            if(enemies.get(i).isDestroyed()){
                enemies.remove(i);
            }
        }
    }

    public void checkAllEnemiesDead(){
        if (enemies.isEmpty()) { hasEnemy = false; }
    }
}

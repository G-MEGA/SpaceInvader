package org.newdawn.spaceinvaders.game_object.ingame.enemy;

import org.newdawn.spaceinvaders.game_object.GameObject2D;
import org.newdawn.spaceinvaders.game_object.visual.SpriteRenderer;
import org.newdawn.spaceinvaders.loop.GameLoop;

public class EnemyWarner extends GameObject2D{
    private final String spriteRef = "sprites/testWarning.png";
    private final Long warningTime = 1L << 16;

    private SpriteRenderer spriteRenderer;

    private long spawnElapsed;
    private GameLoop gameLoop;
    private Enemy enemy;

    public EnemyWarner(Enemy enemy, GameLoop gameLoop) {
        super(gameLoop);

        spriteRenderer = new SpriteRenderer(gameLoop);
        spriteRenderer.setSpriteRef(spriteRef);
        addChild(spriteRenderer);


        spawnElapsed = 0l;
        this.gameLoop = gameLoop;
        this.enemy = enemy;

        setPos(enemy.getPosX(), enemy.getPosY());
        setRotation(enemy.getRotation());
    }

    @Override
    protected void process(long deltaTime) {
        super.process(deltaTime);

        if (spawnElapsed >= warningTime){
            gameLoop.addGameObject(enemy);
            destroy();
        }
        else{
            spawnElapsed += deltaTime;
        }
    }
}

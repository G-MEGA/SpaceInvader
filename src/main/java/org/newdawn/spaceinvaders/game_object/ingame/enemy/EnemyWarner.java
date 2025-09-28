package org.newdawn.spaceinvaders.game_object.ingame.enemy;

import org.newdawn.spaceinvaders.game_object.GameObject2D;
import org.newdawn.spaceinvaders.game_object.visual.SpriteRenderer;
import org.newdawn.spaceinvaders.loop.GameLoop;

public class EnemyWarner extends GameObject2D{
    private final static String spriteRef = "sprites/testWarning.png";
    private final static long defaultWarningTime = 1L << 16;
    private long warningTime;

    private SpriteRenderer spriteRenderer;

    private long warnElapsed;
    private GameLoop gameLoop;
    private Enemy enemy;

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public EnemyWarner(){
        super();
    }

    public EnemyWarner(Enemy enemy, GameLoop gameLoop) { this(enemy, gameLoop, defaultWarningTime); }

    public EnemyWarner(Enemy enemy, GameLoop gameLoop, long warningTime) {
        super(gameLoop);

        this.warningTime = warningTime;

        spriteRenderer = new SpriteRenderer(gameLoop);
        spriteRenderer.setSpriteRef(spriteRef);
        addChild(spriteRenderer);

        warnElapsed = 0l;
        this.gameLoop = gameLoop;
        this.enemy = enemy;

        setPos(enemy.getPosX(), enemy.getPosY());
        setRotation(enemy.getRotation());
    }

    @Override
    protected void process(long deltaTime) {
        super.process(deltaTime);

        if (warnElapsed >= warningTime){
            gameLoop.addGameObject(enemy);
            destroy();
        }
        else{
            warnElapsed += deltaTime;
        }
    }
}

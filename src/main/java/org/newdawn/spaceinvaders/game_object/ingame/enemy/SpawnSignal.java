package org.newdawn.spaceinvaders.game_object.ingame.enemy;

import org.newdawn.spaceinvaders.PositionAngleSet;
import org.newdawn.spaceinvaders.game_object.GameObject;
import org.newdawn.spaceinvaders.game_object.GameObject2D;
import org.newdawn.spaceinvaders.game_object.ingame.store.StoreSlot;
import org.newdawn.spaceinvaders.game_object.visual.SpriteRenderer;
import org.newdawn.spaceinvaders.loop.GameLoop;

public class SpawnSignal extends GameObject2D{
    public final static int EnemySignal = 0; 
    public final static int StoreItemSignal = 1; 
    private int spawnSignalType;

    private final static String enemySpawnSignalSpriteRef = "sprites/testWarning.png";
    private final static String storeItemSignalSpriteRef = "sprites/storeItemSpawnSignal.png";
    private final static long defaultWarningTime = 1L << 16;
    private long warningTime;

    private SpriteRenderer spriteRenderer;

    private long warnElapsed;
    private GameLoop gameLoop;
    private GameObject2D spawnGameObject;

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public SpawnSignal(){
        super();
    }

    public SpawnSignal(GameObject2D gameObject, GameLoop gameLoop, PositionAngleSet positionAngleSet, int spawnSignalType) 
    { this(gameObject, gameLoop, positionAngleSet, defaultWarningTime, spawnSignalType); }

    public SpawnSignal(GameObject2D gameObject, GameLoop gameLoop, PositionAngleSet positionAngleSet, long warningTime, int spawnSignalType) {
        this(gameLoop, positionAngleSet, warningTime, spawnSignalType);

        this.spawnGameObject = gameObject;
        spawnGameObject.setPosRotation(positionAngleSet.positionX, positionAngleSet.positionY, positionAngleSet.angle);
        this.warningTime = warningTime;
    }

    public SpawnSignal(GameLoop gameLoop, PositionAngleSet positionAngleSet, long warningTime, int spawnSignalType) {
        super(gameLoop);

        this.gameLoop = gameLoop;
        this.warningTime = warningTime;
        this.spawnSignalType = spawnSignalType;

        spriteRenderer = new SpriteRenderer(gameLoop);

        String spriteRef;
        switch (spawnSignalType) {
            case EnemySignal:
                spriteRef = enemySpawnSignalSpriteRef;
                break;
            case StoreItemSignal:
                spriteRef = storeItemSignalSpriteRef;
                break;
            default:
                throw new IllegalArgumentException(spawnSignalType + "는 존재하지 않은 spawnSignalType 입니다.");
        }
        spriteRenderer.setSpriteRef(spriteRef);
        addChild(spriteRenderer);

        warnElapsed = 0l;

        setPos(positionAngleSet.positionX, positionAngleSet.positionY);
        setRotation(positionAngleSet.angle);
    }

    @Override
    protected void process(long deltaTime) {
        super.process(deltaTime);

        if (warnElapsed >= warningTime){
            if (gameLoop != null && spawnGameObject != null){
                if (spawnSignalType == EnemySignal){
                    gameLoop.addEnemy((Enemy)spawnGameObject);
                }
                else if (spawnSignalType == StoreItemSignal){
                    gameLoop.addGameObject((StoreSlot)spawnGameObject);
                }
                else{
                    gameLoop.addGameObject(spawnGameObject);
                }
            }
            destroy();
        }
        else{
            warnElapsed += deltaTime;
        }
    }
}

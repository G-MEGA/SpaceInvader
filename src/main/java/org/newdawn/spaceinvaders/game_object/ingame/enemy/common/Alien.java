package org.newdawn.spaceinvaders.game_object.ingame.enemy.common;

import org.newdawn.spaceinvaders.game_object.logic.HiveMind;
import org.newdawn.spaceinvaders.loop.GameLoop;

public class Alien extends SweeperEnemy{
    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public Alien(){
        super();
    }
    public Alien(GameLoop gameLoop, HiveMind hiveMind){
        super(gameLoop, hiveMind, 3);
    }

    @Override
    protected void addSprites(){
        frames.add("sprites/enemy/alien2.gif");
        frames.add("sprites/enemy/alien.gif");
        frames.add("sprites/enemy/alien.gif");
        frames.add("sprites/enemy/alien3.gif");
    }

    @Override
    protected void addHitSprites() {
        onHitFrames.add("sprites/enemy/enemyOnHit2.png");
        onHitFrames.add("sprites/enemy/enemyOnHit1.png");
        onHitFrames.add("sprites/enemy/enemyOnHit1.png");
        onHitFrames.add("sprites/enemy/enemyOnHit3.png");
    }
}
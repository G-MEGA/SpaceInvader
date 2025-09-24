package org.newdawn.spaceinvaders.game_object.ingame.enemy;

import org.newdawn.spaceinvaders.game_object.logic.HiveMind;
import org.newdawn.spaceinvaders.loop.GameLoop;

public class Alien extends SweeperEnemy{
    public Alien(GameLoop gameLoop, HiveMind hiveMind){
        super(gameLoop, hiveMind, 5);
    }

    @Override
    protected void addSprites(){
        frames.add("sprites/alien2.gif");
        frames.add("sprites/alien.gif");
        frames.add("sprites/alien.gif");
        frames.add("sprites/alien3.gif");
    }
}
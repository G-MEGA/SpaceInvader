package org.newdawn.spaceinvaders.game_object.ingame.enemy;

import org.newdawn.spaceinvaders.game_object.Mover2D;
import org.newdawn.spaceinvaders.loop.Loop;
import org.newdawn.spaceinvaders.singleton.LootItemFactory;

public abstract class Enemy extends Mover2D {
    public Enemy(Loop loop){
        super(loop);
    }

    public void onHitByBullet(){
        destroy();

        LootItemFactory.getInstance().instantiateRandomItem(loop);
    }
}

package org.newdawn.spaceinvaders.game_object.ingame.loot_item;

import org.newdawn.spaceinvaders.game_object.ingame.player.PlayerShip;
import org.newdawn.spaceinvaders.loop.Loop;

public class ShieldItem extends LootItem {
    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public ShieldItem(){
        super();
    }

    public ShieldItem(Loop loop) {
        super(loop, "sprites/testShieldItem.png");
    }

    @Override
    protected void onCollideWithPlayerShip(PlayerShip ship){
        ship.increaseHealth();
    }
}

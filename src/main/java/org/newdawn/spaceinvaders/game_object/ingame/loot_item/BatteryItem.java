package org.newdawn.spaceinvaders.game_object.ingame.loot_item;

import org.newdawn.spaceinvaders.game_object.ingame.PlayerShip;
import org.newdawn.spaceinvaders.loop.GameLoop;
import org.newdawn.spaceinvaders.loop.Loop;

public class BatteryItem extends LootItem {
    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public BatteryItem(){
        super();
    }
    public BatteryItem(Loop loop) {
        super(loop, "sprites/testBatteryItem.png");
    }

    protected void onCollideWithPlayerShip(PlayerShip ship){
        ship.requestToSpeedUp();
    }
}

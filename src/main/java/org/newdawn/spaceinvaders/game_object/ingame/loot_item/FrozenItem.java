package org.newdawn.spaceinvaders.game_object.ingame.loot_item;

import org.newdawn.spaceinvaders.loop.Loop;

public class FrozenItem extends LootItem {
    public FrozenItem(Loop loop){
        super(loop);

        System.out.println("Frozen Item 소환");
    }
}

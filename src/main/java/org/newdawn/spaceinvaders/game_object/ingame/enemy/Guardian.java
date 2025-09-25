package org.newdawn.spaceinvaders.game_object.ingame.enemy;

import org.newdawn.spaceinvaders.game_object.collision.ICollider2DOwner;
import org.newdawn.spaceinvaders.game_object.ingame.loot_item.LootItem;
import org.newdawn.spaceinvaders.game_object.logic.HiveMind;
import org.newdawn.spaceinvaders.loop.GameLoop;
import org.newdawn.spaceinvaders.singleton.LootItemFactory;
import org.newdawn.spaceinvaders.sprite.Sprite;
import org.newdawn.spaceinvaders.sprite.SpriteStore;

public class Guardian extends SweeperEnemy{
    private Boolean hasShield = true;

    public Guardian(GameLoop gameLoop, HiveMind hiveMind){
        super(gameLoop, hiveMind,1);
    }
    
    @Override
    protected void addSprites() {
        frames.add("sprites/shieldAlien1.png");
        frames.add("sprites/shieldAlien2.png");
        frames.add("sprites/shieldAlien3.png");
    }

    @Override
    public void onHit(int damage) {
        if(isDestroyed()) return;

        if (hasShield){
            hasShield = false;
            
            frames.clear();
            frames.add("sprites/alien.gif");
            frames.add("sprites/alien2.gif");
            frames.add("sprites/alien3.gif");
            
            return;
        }

        decreaseHealth(damage);
        
        if (_health <= 0){
            destroy();
    
            LootItem item = LootItemFactory.getInstance().instantiateRandomItem(loop);
    
            if (item != null){
                item.setPos(getPosX(), getPosY());
            }
        }
    }
}

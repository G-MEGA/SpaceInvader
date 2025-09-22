package org.newdawn.spaceinvaders.game_object.ingame.enemy;

import org.newdawn.spaceinvaders.game_object.collision.ICollider2DOwner;
import org.newdawn.spaceinvaders.game_object.ingame.loot_item.LootItem;
import org.newdawn.spaceinvaders.loop.GameLoop;
import org.newdawn.spaceinvaders.singleton.LootItemFactory;
import org.newdawn.spaceinvaders.sprite.Sprite;
import org.newdawn.spaceinvaders.sprite.SpriteStore;

public class Guardian extends Enemy{
    private Boolean hasShield = true;

    public Guardian(GameLoop gameLoop){
        super(gameLoop, 1);
    }

    @Override
    public void onBroadcast() {
    }

    @Override
    protected void addSprites() {
        frames.add(SpriteStore.get().getSprite("sprites/shieldAlien1.png"));
        frames.add(SpriteStore.get().getSprite("sprites/shieldAlien2.png"));
        frames.add(SpriteStore.get().getSprite("sprites/shieldAlien3.png"));
    }

    @Override
    public void collidedWith(ICollider2DOwner collider) {
        super.collidedWith(collider);
    }

    @Override
    public void onHitByBullet() {
        if(isDestroyed()) return;

        if (hasShield){
            hasShield = false;
            
            frames.clear();
            frames.add(SpriteStore.get().getSprite("sprites/alien.gif"));
            frames.add(SpriteStore.get().getSprite("sprites/alien2.gif"));
            frames.add(SpriteStore.get().getSprite("sprites/alien3.gif"));
            
            return;
        }

        if (--_health <= 0){
            destroy();
    
            LootItem item = LootItemFactory.getInstance().instantiateRandomItem(loop);
    
            if (item != null){
                item.setPos(getPosX(), getPosY());
            }
        }
    }
}

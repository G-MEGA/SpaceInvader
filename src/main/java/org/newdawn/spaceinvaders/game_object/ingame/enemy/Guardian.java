package org.newdawn.spaceinvaders.game_object.ingame.enemy;

import org.newdawn.spaceinvaders.game_object.collision.ICollider2DOwner;
import org.newdawn.spaceinvaders.game_object.ingame.PlayerShip;
import org.newdawn.spaceinvaders.game_object.ingame.loot_item.LootItem;
import org.newdawn.spaceinvaders.game_object.logic.HiveMind;
import org.newdawn.spaceinvaders.loop.GameLoop;
import org.newdawn.spaceinvaders.singleton.LootItemFactory;
import org.newdawn.spaceinvaders.sprite.Sprite;
import org.newdawn.spaceinvaders.sprite.SpriteStore;

public class Guardian extends SweeperEnemy{
    private Boolean hasShield = true;

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public Guardian(){
        super();
    }
    public Guardian(GameLoop gameLoop, HiveMind hiveMind){
        super(gameLoop, hiveMind,1);
    }
    
    @Override
    protected void addSprites() {
        frames.add("sprites/enemy/shieldAlien1.png");
        frames.add("sprites/enemy/shieldAlien2.png");
        frames.add("sprites/enemy/shieldAlien3.png");
    }
    @Override
    protected void addHitSprites() {
        onHitFrames.add("sprites/enemy/enemyOnHit1.png");
        onHitFrames.add("sprites/enemy/enemyOnHit2.png");
        onHitFrames.add("sprites/enemy/enemyOnHit3.png");
    }

    @Override
    public void onHit(int damage) {
        if(isDestroyed()) return;

        if (hasShield){
            hasShield = false;
            
            frames.clear();
            frames.add("sprites/enemy/alien.gif");
            frames.add("sprites/enemy/alien2.gif");
            frames.add("sprites/enemy/alien3.gif");
            
            return;
        }

        decreaseHealth(damage);
        
        if (_health <= 0){
            destroy();
    
            LootItem item = LootItemFactory.getInstance().instantiateRandomItem((GameLoop)getLoop());
    
            if (item != null){
                item.setPos(getPosX(), getPosY());
            }
        }
    }
    
}

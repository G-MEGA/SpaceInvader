package org.newdawn.spaceinvaders.game_object.ingame.bullet;

import org.newdawn.spaceinvaders.game_object.collision.ICollider2DOwner;
import org.newdawn.spaceinvaders.game_object.ingame.enemy.Enemy;
import org.newdawn.spaceinvaders.loop.GameLoop;

public class PlayerBullet extends Bullet{
    public PlayerBullet(GameLoop gameLoop, long spawnAngle, long spawnCentralX, long spawnCentralY, long spawnOffset, long spawnSpeed) {
        super(gameLoop, spawnAngle, spawnCentralX, spawnCentralY, spawnOffset, spawnSpeed);
    }
    
    @Override
    public void collidedWith(ICollider2DOwner collider) {
        super.collidedWith(collider);

        // if we've hit an Enemy, kill it!
        if (collider instanceof Enemy) {
            Enemy enemy = (Enemy) collider;

            if(enemy.isDestroyed()) return;

            enemy.onHitByBullet();
            destroy();

            used = true;
        }
    }
}

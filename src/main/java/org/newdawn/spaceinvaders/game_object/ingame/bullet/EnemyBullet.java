package org.newdawn.spaceinvaders.game_object.ingame.bullet;

import org.newdawn.spaceinvaders.game_object.collision.ICollider2DOwner;
import org.newdawn.spaceinvaders.loop.GameLoop;

public class EnemyBullet extends Bullet{
    private static final String spriteRef = "sprites/enemy/enemyBullet.png";

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public EnemyBullet(){
        super();
    }

    public EnemyBullet(GameLoop gameLoop, long spawnAngle, long spawnCentralX, long spawnCentralY, long spawnOffset, long spawnSpeed) {
        super(gameLoop, spawnAngle, spawnCentralX, spawnCentralY, spawnOffset, spawnSpeed, spriteRef);
    }
    
    @Override
    public void collidedWith(ICollider2DOwner collider) {
        super.collidedWith(collider);
    }

    /**
     * {@code PlayerShip}과의 충돌을 처리하는 메소드이다.
     * <p>
     * {@code PlayerShip}과 {@code EnemyBullet}의 충돌 처리를
     * 일관성 있게 관리하기 위해, 충돌 처리의 책임은
     * {@code PlayerShip}에서 담당한다.
     * 따라서 이 메소드는 {@code PlayerShip}에서 호출된다.
     */
    public void onHitByPlayerShip() {
        used = true;
        destroy();
    }
}

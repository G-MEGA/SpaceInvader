package org.newdawn.spaceinvaders.game_object.ingame;

import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
import org.newdawn.spaceinvaders.game_object.collision.ICollider2DOwner;
import org.newdawn.spaceinvaders.loop.Loop;

public class EnemyLaser extends Laser {
    private final static String spriteRef = "sprites/enemyLaser.png";
    private final static int damage = 1;
    private final static long lifeDuration = FixedPointUtil.ZERO_5;
    public static int getDamage() { return damage; }

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public EnemyLaser() {
    super();
    }

    public EnemyLaser(Loop loop, long spawnPosX, long spawnPosY, long spawnOffset, long spawnAngle) {
        super(loop, spawnPosX, spawnPosY, spawnOffset, spawnAngle, spriteRef, damage, lifeDuration);
    }

    @Override
    public void collidedWith(ICollider2DOwner collider) {
        //* playerShip와의 충돌시 playerShip에게 줄 데미지는 PlayerShip 내에서 처리 함
    }
}

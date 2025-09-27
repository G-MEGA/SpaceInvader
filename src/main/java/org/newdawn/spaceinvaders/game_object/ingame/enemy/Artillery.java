package org.newdawn.spaceinvaders.game_object.ingame.enemy;

import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
import org.newdawn.spaceinvaders.game_object.ingame.PlayerShip;
import org.newdawn.spaceinvaders.game_object.ingame.bullet.EnemyBullet;
import org.newdawn.spaceinvaders.game_object.logic.HiveMind;
import org.newdawn.spaceinvaders.loop.GameLoop;

public class Artillery extends SweeperEnemy{
    private long fireInterval = 2 << 16;
    private long fireElapsed = 0;
    private PlayerShip _playerShip;

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public Artillery(){
        super();
    }
    public Artillery(GameLoop gameLoop, HiveMind hiveMind, PlayerShip playerShip) {
        super(gameLoop, hiveMind, 1);

        _playerShip = playerShip;
    }

    @Override
    protected void addSprites() {
        frames.add("sprites/alien.gif");
    }
    
    @Override
    protected void process(long deltaTime) {
        super.process(deltaTime);

        if (fireElapsed >= fireInterval){
            fireElapsed = 0;

            EnemyBullet enemyBullet = new EnemyBullet((GameLoop)getLoop(), getRotation(), getPosX(), getPosY(), -30, FixedPointUtil.fromLong(-300));
            getLoop().addGameObject(enemyBullet);
        }
        else{
            fireElapsed += deltaTime;
        }

        long bulletToPlayerX = _playerShip.getPosX() - getPosX();
        long bulletToPlayerY = _playerShip.getPosY() - getPosY();

        long currentAngle = FixedPointUtil.atan2(bulletToPlayerY, bulletToPlayerX) + (90 << 16);
        setRotation(currentAngle);
    }
}

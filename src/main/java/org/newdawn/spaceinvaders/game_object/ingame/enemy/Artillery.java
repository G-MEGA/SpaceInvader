package org.newdawn.spaceinvaders.game_object.ingame.enemy;

import org.newdawn.spaceinvaders.Game;
import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
import org.newdawn.spaceinvaders.game_object.ingame.PlayerShip;
import org.newdawn.spaceinvaders.game_object.ingame.bullet.EnemyBullet;
import org.newdawn.spaceinvaders.game_object.logic.HiveMind;
import org.newdawn.spaceinvaders.loop.GameLoop;

public class Artillery extends SweeperEnemy{
    private long fireInterval = 2 << 16;
    private long fireElapsed = 0;
    private PlayerShip target;

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public Artillery(){
        super();
    }
    public Artillery(GameLoop gameLoop, HiveMind hiveMind) {
        super(gameLoop, hiveMind, 1);
    }

    @Override
    protected void addSprites() {
        frames.add("sprites/enemy/artillery1.png");
        frames.add("sprites/enemy/artillery2.png");
        frames.add("sprites/enemy/artillery3.png");
    }
    @Override
    protected void addHitSprites() {
        onHitFrames.add("sprites/enemy/enemyOnHit1.png");
        onHitFrames.add("sprites/enemy/enemyOnHit2.png");
        onHitFrames.add("sprites/enemy/enemyOnHit3.png");
    }

    @Override
    protected void process(long deltaTime) {
        super.process(deltaTime);
        if(target==null){
            target = ((GameLoop)getLoop()).getRandomAlivePlayerShip();
        }

        if (fireElapsed >= fireInterval && target!=null){
            fireElapsed = 0;

            long shotAngle = getRotation();
            long spread = 30L << 16;//산포도. n도 정도로 탄이 퍼지게
            shotAngle -= spread/2;
            shotAngle += ((GameLoop)getLoop()).getRandom().nextLong(spread);
            EnemyBullet enemyBullet = new EnemyBullet((GameLoop)getLoop(), shotAngle, getPosX(), getPosY(), -30, FixedPointUtil.fromLong(-300));
            getLoop().addGameObject(enemyBullet);

            //쏘고 나서 타겟 변경
            target = ((GameLoop)getLoop()).getRandomAlivePlayerShip();
        }
        else{
            fireElapsed += deltaTime;
        }


        if(target!=null) {
            long bulletToPlayerX = target.getPosX() - getPosX();
            long bulletToPlayerY = target.getPosY() - getPosY();

            long currentAngle = FixedPointUtil.atan2(bulletToPlayerY, bulletToPlayerX) + (90 << 16);
            setRotation(currentAngle);
        }
    }

}

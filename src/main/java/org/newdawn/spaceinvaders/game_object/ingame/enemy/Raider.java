package org.newdawn.spaceinvaders.game_object.ingame.enemy;

import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
import org.newdawn.spaceinvaders.game_object.ingame.PlayerShip;
import org.newdawn.spaceinvaders.game_object.logic.HiveMind;
import org.newdawn.spaceinvaders.game_object.logic.IHiveMindListener;
import org.newdawn.spaceinvaders.loop.GameLoop;

public class Raider extends SweeperEnemy{
    private Boolean _hasCharged = false;
    private PlayerShip _playerShip;

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public Raider(){
        super();
    }
    public Raider(GameLoop gameLoop, HiveMind hiveMind, PlayerShip playerShip) {
        super(gameLoop, hiveMind, 1);

        _playerShip = playerShip;
    }

    @Override
    protected void addSprites() {
        frames.add("sprites/enemy/raiderAlien1.png");
        frames.add("sprites/enemy/raiderAlien2.png");
        frames.add("sprites/enemy/raiderAlien3.png");
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

        if (!_hasCharged && Math.abs(_playerShip.getPosX() - getPosX()) < 29 << 16){
            _hasCharged = true;
            velocityX = 0;
            velocityY = FixedPointUtil.fromLong(300L);
        }
    }
}

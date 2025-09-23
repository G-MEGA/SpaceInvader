package org.newdawn.spaceinvaders.game_object.ingame.enemy;

import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
import org.newdawn.spaceinvaders.game_object.ingame.PlayerShip;
import org.newdawn.spaceinvaders.game_object.logic.HiveMind;
import org.newdawn.spaceinvaders.game_object.logic.IHiveMindListener;
import org.newdawn.spaceinvaders.loop.GameLoop;

public class Raider extends SweeperEnemy{
    private Boolean _hasCharged = false;
    private PlayerShip _playerShip;
    
    public Raider(GameLoop gameLoop, HiveMind hiveMind, PlayerShip playerShip) {
        super(gameLoop, hiveMind, 1);

        _playerShip = playerShip;
    }

    @Override
    protected void addSprites() {
        frames.add("sprites/raiderAlien1.png");
        frames.add("sprites/raiderAlien2.png");
        frames.add("sprites/raiderAlien3.png");
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

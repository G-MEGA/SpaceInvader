package org.newdawn.spaceinvaders.game_object.ingame.store;

import org.newdawn.spaceinvaders.game_object.ingame.PlayerShip;
import org.newdawn.spaceinvaders.loop.GameLoop;
import org.newdawn.spaceinvaders.sprite.Sprite;

public interface IStoreItem {
    public String getName();
    public String getSpriteRef();
    /**
     * 유저가 아이템을 구매할 때 실행되는 메소드입니다.
     * <p>
     * 구매 조건이 충족되면 획득 로직을 실행 후 {@code true}를 반환하며,  
     * 조건이 충족되지 않아 구매에 실패하면 {@code false}를 반환합니다.
     *
     * @return 구매 성공 시 {@code true}, 실패 시 {@code false}
     */
    public boolean onAcquire(GameLoop gameLoop);
}

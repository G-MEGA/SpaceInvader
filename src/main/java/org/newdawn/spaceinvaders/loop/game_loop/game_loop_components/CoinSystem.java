package org.newdawn.spaceinvaders.loop.game_loop.game_loop_components;

import org.newdawn.spaceinvaders.loop.GameLoop;

public class CoinSystem {
    GameLoop gameLoop;

    public CoinSystem(){
        // Kryo 역직렬화를 위한 매개변수 없는 생성자
    }

    public CoinSystem(GameLoop gameLoop) {
        this.gameLoop = gameLoop;
    }

    private long coinCount = 0;
    public long getCoinCount() { return coinCount; }
    public void increaseCoin(){ increaseCoin(1); }
    public void increaseCoin(long count){ coinCount += count; }

    /**
     * 현재 코인의 개수를 1만큼 감소시킵니다.
     * <p>
     * 내부적으로 {@link #decreaseCoin(long)}을 호출하며,
     * 감소가 가능하다면 코인을 차감하고 {@code true}를 반환합니다.
     * 만약 코인의 개수가 부족하여 차감이 불가능하다면 {@code false}를 반환합니다.
     *
     * @return 코인을 정상적으로 감소시켰다면 {@code true}, 그렇지 않다면 {@code false}
     */
    public boolean decreaseCoin() { return decreaseCoin(1); }
    /**
     * 현재 코인의 개수를 지정된 수치만큼 감소시킵니다.
     * <p>
     * {@code coinCount}가 감소시키려는 값 이상일 경우 차감이 가능하며,
     * 차감 후 {@code true}를 반환합니다.  
     * 만약 차감이 불가능하다면 코인의 개수는 변하지 않고 {@code false}를 반환합니다.
     *
     * @param count 감소시키려는 코인의 개수 (양수)
     * @return 코인을 정상적으로 감소시켰다면 {@code true}, 그렇지 않다면 {@code false}
     */
    public boolean decreaseCoin(long count) { 
        if (coinCount - count >= 0) {
            coinCount -= count;
            return true;
        }
        return false;    
    }
}

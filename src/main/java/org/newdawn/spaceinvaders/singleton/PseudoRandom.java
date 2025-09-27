package org.newdawn.spaceinvaders.singleton;

import java.util.Random;

import org.newdawn.spaceinvaders.loop.GameLoop;
import org.newdawn.spaceinvaders.loop.Loop;

public class PseudoRandom {
    private Loop loop;
    private Random random;

    public void setLoop(Loop loop) {
        this.loop = loop;
    }

    private static PseudoRandom instance = new PseudoRandom();

    public static PseudoRandom getInstance() {
        return instance;
    }

    public PseudoRandom() {
        this.random = new Random();
    }

    /**
     * 현재 게임 상태를 기반으로 결정론적(deterministic) 난수를 생성합니다.
     * 동일한 게임 상태에서는 항상 동일한 난수를 반환합니다.
     *
     * @return 0.0 이상 1.0 미만의 double 타입 난수
     */
    //TODO 뭔가 랜덤이 이상함
    public double random() {
        if (loop == null) {
            throw new IllegalStateException("존재하지 않은 세계선이라서 랜덤을 뽑을 수 없습니다.");
        }

        // 해시 코드 생성 방식과 유사하게 시드를 계산하여 신뢰도를 높입니다.
        // 1. 시작은 0이 아닌 소수(prime number)로 시작합니다.
        long seed = 17;

        // 2. 각 게임 상태 값을 다른 소수(주로 31)와 곱하고 더해줍니다.
        //    이렇게 하면 각 변수의 변화가 전체 시드 값에 큰 영향을 미치게 됩니다.
        seed = 31 * seed + loop.getGameObjcetsCount();
        seed = 31 * seed + loop.getGameObjectsInProcessingCount();
        seed = 31 * seed + loop.getCollidersCount();

        if (loop instanceof GameLoop) {
            GameLoop gameLoop = (GameLoop) loop;
            seed = 31 * seed + gameLoop.getCoinCount();
            seed = 31 * seed + gameLoop.getScore();
        }

        random.setSeed(seed);

        return random.nextDouble();
    }
}
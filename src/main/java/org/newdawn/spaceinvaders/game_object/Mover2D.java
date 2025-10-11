package org.newdawn.spaceinvaders.game_object;

import org.newdawn.spaceinvaders.Game;
import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
import org.newdawn.spaceinvaders.loop.Loop;

public abstract class Mover2D extends GameObject2D{
    public long velocityX;
    public long velocityY;

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public  Mover2D(){
        super();
    }
    public Mover2D(Loop loop) {
        super(loop);
    }

    @Override
    protected void process(long deltaTime) {
        super.process(deltaTime);

        setPosX(getPosX() + FixedPointUtil.mul(velocityX, deltaTime));
        setPosY(getPosY() + FixedPointUtil.mul(velocityY, deltaTime));
    }
}

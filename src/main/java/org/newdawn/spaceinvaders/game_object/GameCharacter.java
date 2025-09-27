package org.newdawn.spaceinvaders.game_object;

import org.newdawn.spaceinvaders.game_object.collision.ICollider2DOwner;
import org.newdawn.spaceinvaders.loop.Loop;

public abstract class GameCharacter extends Mover2D implements ICollider2DOwner{
    protected long _health;

    public void increaseHealth(long amount) { _health += amount; }
    public void increaseHealth() { increaseHealth(1); }
    public void decreaseHealth(long amount) { 
        _health = Math.max(0, _health - amount);
    }
    public void decreaseHealth() { decreaseHealth(1); }
    public long getHealth() { return _health; }

    public boolean isDead() { return _health <= 0; }

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public  GameCharacter(){
        super();
    }
    public GameCharacter(Loop loop, long intitalHealth){
        super(loop);

        _health = intitalHealth;
    }
}

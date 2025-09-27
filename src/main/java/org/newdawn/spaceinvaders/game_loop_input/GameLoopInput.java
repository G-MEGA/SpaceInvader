package org.newdawn.spaceinvaders.game_loop_input;



public abstract class GameLoopInput  {
    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public GameLoopInput() {}

    public abstract String toSaveData();
}

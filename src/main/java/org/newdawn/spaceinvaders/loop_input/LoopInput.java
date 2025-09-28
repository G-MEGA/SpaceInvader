package org.newdawn.spaceinvaders.loop_input;



public abstract class LoopInput {
    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public LoopInput() {}

    public abstract String toSaveData();
}

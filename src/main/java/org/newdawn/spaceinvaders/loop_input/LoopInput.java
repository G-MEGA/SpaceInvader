package org.newdawn.spaceinvaders.loop_input;

public abstract class LoopInput {
    public int playerID = -1;

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public LoopInput() {}

    LoopInput(int playerID) {
        this.playerID = playerID;
    }

    public String toSaveData(){
        return playerID + " ";
    }
}

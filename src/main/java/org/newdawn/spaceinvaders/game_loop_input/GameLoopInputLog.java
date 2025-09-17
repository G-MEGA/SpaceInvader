package org.newdawn.spaceinvaders.game_loop_input;

import java.util.ArrayList;

public class GameLoopInputLog {
    public long inputFrame;
    public ArrayList<GameLoopInput> inputs;

    private StringBuilder stringBuilder = new StringBuilder();

    public GameLoopInputLog(long inputFrame, ArrayList<GameLoopInput> inputs){
        this.inputFrame = inputFrame;
        this.inputs = inputs;
    }
    public GameLoopInputLog(String data){
        String[] splited = data.trim().split("\t");
        inputFrame = Long.parseLong(splited[0]);

        inputs = new ArrayList<>();
        for(int i = 1; i < splited.length; i++){
            if(splited[i].startsWith("KEY")){
                inputs.add(new GameLoopInputKey(splited[i]));
            }
        }
    }

    public String toSaveData() {
        stringBuilder.setLength(0);

        stringBuilder.append(String.valueOf(inputFrame));
        stringBuilder.append("\t");

        for(GameLoopInput input : inputs){
            stringBuilder.append(input.toSaveData());
            stringBuilder.append("\t");
        }

        return stringBuilder.toString();
    }
}

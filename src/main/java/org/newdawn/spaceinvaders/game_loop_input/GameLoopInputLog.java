package org.newdawn.spaceinvaders.game_loop_input;

import java.io.Serializable;
import java.util.ArrayList;

public class GameLoopInputLog implements Serializable {
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
            if(splited[i].startsWith("K")){
                inputs.add(new GameLoopInputKey(splited[i]));
                continue;
            }
            else if(splited[i].startsWith("M")){
                inputs.add(new GameLoopInputMouseMove(splited[i]));
                continue;
            }

            throw new RuntimeException();
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

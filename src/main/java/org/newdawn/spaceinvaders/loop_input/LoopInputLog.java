package org.newdawn.spaceinvaders.loop_input;


import java.util.ArrayList;

public class LoopInputLog {
    public long inputFrame;
    public ArrayList<LoopInput> inputs;

    private StringBuilder stringBuilder = new StringBuilder();

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public LoopInputLog(){
        super();
    }
    public LoopInputLog(long inputFrame, ArrayList<LoopInput> inputs){
        this.inputFrame = inputFrame;
        this.inputs = inputs;
    }
    public LoopInputLog(String data){
        String[] splited = data.trim().split("\t");
        inputFrame = Long.parseLong(splited[0]);

        inputs = new ArrayList<>();
        for(int i = 1; i < splited.length; i++){
            if(splited[i].startsWith("K")){
                inputs.add(new LoopInputKey(splited[i]));
                continue;
            }
            else if(splited[i].startsWith("M")){
                inputs.add(new LoopInputMouseMove(splited[i]));
                continue;
            }

            throw new RuntimeException();
        }
    }

    public String toSaveData() {
        stringBuilder.setLength(0);

        stringBuilder.append(String.valueOf(inputFrame));
        stringBuilder.append("\t");

        for(LoopInput input : inputs){
            stringBuilder.append(input.toSaveData());
            stringBuilder.append("\t");
        }

        return stringBuilder.toString();
    }
}

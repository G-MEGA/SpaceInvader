package org.newdawn.spaceinvaders.loop_input;


import java.util.ArrayList;

public class LoopInputLog {
    public long inputFrame;
    // 현재는 게임 플레이 중 입력을 받는 모든 객체들이 isKeyInputPressed()등으로 입력에 접근하므로
    // inputs 내에서의 순서는 로직에 영향을 끼치지 않음.
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
            char type = splited[i].charAt(splited[i].indexOf(" ") + 1);

            if(type == 'K'){
                inputs.add(new LoopInputKey(splited[i]));
                continue;
            }
            else if(type == 'M'){
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

package org.newdawn.spaceinvaders.loop_input;

public class LoopInputMouseMove extends LoopInput {
    public int posX;
    public int posY;

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public LoopInputMouseMove(){
        super();
    }
    public LoopInputMouseMove(int posX, int posY){
        this.posX = posX;
        this.posY = posY;
    }
    public LoopInputMouseMove(String data){
        String[] splited = data.trim().split(" ");

        if(!splited[0].equals("M")){
            throw new IllegalArgumentException();
        }
        posX=Integer.parseInt(splited[1]);
        posY=Integer.parseInt(splited[2]);
    }

    @Override
    public String toSaveData(){
        return "M " + posX + " " + posY;
    }
}

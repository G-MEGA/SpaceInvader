package org.newdawn.spaceinvaders.loop_input;

public class LoopInputMouseMove extends LoopInput {
    public int posX;
    public int posY;

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public LoopInputMouseMove(){
        super();
    }
    public LoopInputMouseMove(int playerID, int posX, int posY){
        super(playerID);
        this.posX = posX;
        this.posY = posY;
    }
    public LoopInputMouseMove(String data){
        String[] splited = data.trim().split(" ");

        playerID = Integer.parseInt(splited[0]);

        if(!splited[1].equals("M")){
            throw new IllegalArgumentException();
        }
        posX=Integer.parseInt(splited[2]);
        posY=Integer.parseInt(splited[3]);
    }

    @Override
    public String toSaveData(){
        return super.toSaveData() + "M " + posX + " " + posY;
    }
}

package org.newdawn.spaceinvaders.game_loop_input;

public class GameLoopInputMouseMove extends GameLoopInput {
    public int posX;
    public int posY;
    public GameLoopInputMouseMove(int posX, int posY){
        this.posX = posX;
        this.posY = posY;
    }
    public GameLoopInputMouseMove(String data){
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

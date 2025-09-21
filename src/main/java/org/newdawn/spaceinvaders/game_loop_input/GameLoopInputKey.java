package org.newdawn.spaceinvaders.game_loop_input;

public class GameLoopInputKey extends GameLoopInput{
    public String name;
    public boolean pressed;

    public GameLoopInputKey(String name, boolean pressed){
        this.name = name;
        this.pressed = pressed;
    }
    public GameLoopInputKey(String data){
        String[] splited = data.trim().split(" ");

        if(!splited[0].equals("K")){
            throw new IllegalArgumentException();
        }
        name = splited[1];
        pressed = !splited[2].equals("0");
    }

    @Override
    public String toSaveData() {
        return "K "+ name + (pressed ?" 1":" 0");
    }

}

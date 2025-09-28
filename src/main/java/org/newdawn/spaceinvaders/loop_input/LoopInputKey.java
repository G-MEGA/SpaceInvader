package org.newdawn.spaceinvaders.loop_input;

public class LoopInputKey extends LoopInput {
    public String name;
    public boolean pressed;

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public LoopInputKey(){
        super();
    }
    public LoopInputKey(String name, boolean pressed){
        this.name = name;
        this.pressed = pressed;
    }
    public LoopInputKey(String data){
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

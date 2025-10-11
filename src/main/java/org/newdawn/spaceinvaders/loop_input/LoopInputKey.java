package org.newdawn.spaceinvaders.loop_input;

public class LoopInputKey extends LoopInput {
    public String name;
    public boolean pressed;

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public LoopInputKey(){
        super();
    }
    public LoopInputKey(int playerID, String name, boolean pressed){
        super(playerID);
        this.name = name;
        this.pressed = pressed;
    }
    public LoopInputKey(String data){
        String[] splited = data.trim().split(" ");

        playerID = Integer.parseInt(splited[0]);

        if(!splited[1].equals("K")){
            throw new IllegalArgumentException();
        }
        name = splited[2];
        pressed = !splited[3].equals("0");
    }

    @Override
    public String toSaveData() {
        return super.toSaveData() + "K "+ name + (pressed ?" 1":" 0");
    }

}

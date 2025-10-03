package org.newdawn.spaceinvaders.map_load.map_load_commands;

import org.newdawn.spaceinvaders.enums.SpecialCommandType;

public class SpecialCommand extends MapLoadCommand{
    private SpecialCommandType _specialCommandType;

    public SpecialCommandType getSpecialCommandType() { return _specialCommandType; }


    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public SpecialCommand(){}
    public SpecialCommand(SpecialCommandType specialCommandType){
        super();

        _specialCommandType = specialCommandType;
    }
}

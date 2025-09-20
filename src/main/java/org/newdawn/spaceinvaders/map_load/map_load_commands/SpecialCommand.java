package org.newdawn.spaceinvaders.map_load.map_load_commands;

import org.newdawn.spaceinvaders.enums.SpecialCommandType;

public class SpecialCommand {
    private SpecialCommandType _specialCommandType;

    public SpecialCommandType get_specialCommandType() { return _specialCommandType; }

    public SpecialCommand(SpecialCommandType specialCommandType){
        super();
        
        _specialCommandType = specialCommandType;
    }
}

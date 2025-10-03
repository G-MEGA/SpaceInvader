package org.newdawn.spaceinvaders.map_load.map_load_commands;

import org.newdawn.spaceinvaders.enums.SectionType;

public class SectionCommand extends MapLoadCommand{
    private SectionType _sectionCommandType;

    public SectionType getSectionCommandType() { return _sectionCommandType; }

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public SectionCommand(){}
    public SectionCommand(SectionType sectionCommandType){
        super();

        _sectionCommandType = sectionCommandType;
    }
}
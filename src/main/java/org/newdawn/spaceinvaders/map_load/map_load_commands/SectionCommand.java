package org.newdawn.spaceinvaders.map_load.map_load_commands;

import org.newdawn.spaceinvaders.enums.SectionCommandType;

public class SectionCommand extends MapLoadCommand{
    private SectionCommandType _sectionCommandType;

    public SectionCommandType getSectionCommandType() { return _sectionCommandType; }

    public SectionCommand(SectionCommandType sectionCommandType){
        super();

        _sectionCommandType = sectionCommandType;
    }
}
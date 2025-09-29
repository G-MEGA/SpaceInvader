package org.newdawn.spaceinvaders.map_load.map_load_commands;

import org.newdawn.spaceinvaders.enums.SectionType;

public class SectionCommand extends MapLoadCommand{
    private SectionType _sectionCommandType;

    public SectionType getSectionCommandType() { return _sectionCommandType; }

    public SectionCommand(SectionType sectionCommandType){
        super();

        _sectionCommandType = sectionCommandType;
    }
}
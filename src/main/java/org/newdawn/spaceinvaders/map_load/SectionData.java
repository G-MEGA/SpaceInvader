package org.newdawn.spaceinvaders.map_load;

import java.util.LinkedList;
import java.util.Queue;

import org.newdawn.spaceinvaders.enums.SectionType;
import org.newdawn.spaceinvaders.map_load.map_load_commands.InstantiateCommand;
import org.newdawn.spaceinvaders.map_load.map_load_commands.SectionCommand;

public class SectionData {
    private SectionCommand sectionCommand;
    private Queue<InstantiateCommand> remainInstantiateCommands = new LinkedList<>();


    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public SectionData(){}
    public SectionData(SectionCommand sectionCommand, Queue<InstantiateCommand> remainInstantiateCommands) {
        this.sectionCommand = sectionCommand;
        this.remainInstantiateCommands = remainInstantiateCommands;
    }
    
    public SectionType getSectionType() { return sectionCommand.getSectionCommandType(); }
    public long getNextInstantiateCommandInstantiateTime() { return remainInstantiateCommands.peek().getInstantiateTime(); }
    public InstantiateCommand pollNextInstantiateCommand() { return remainInstantiateCommands.poll(); }
    public boolean hasMoreInstantiateCommands() { return !remainInstantiateCommands.isEmpty(); }
}

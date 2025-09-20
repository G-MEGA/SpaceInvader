package org.newdawn.spaceinvaders.map_load;

import java.util.ArrayList;
import java.util.Arrays;

import org.newdawn.spaceinvaders.enums.GameObjectType;
import org.newdawn.spaceinvaders.enums.SectionCommandType;
import org.newdawn.spaceinvaders.enums.SpecialCommandType;
import org.newdawn.spaceinvaders.map_load.map_load_commands.InstantiateCommand;
import org.newdawn.spaceinvaders.map_load.map_load_commands.MapLoadCommand;
import org.newdawn.spaceinvaders.map_load.map_load_commands.SectionCommand;
import org.newdawn.spaceinvaders.map_load.map_load_commands.SpecialCommand;

public class MapDataParser {
    public ArrayList<MapLoadCommand> parseMapData(String plainData){
        ArrayList<MapLoadCommand> commands = new ArrayList<>();
        String[] plainFile = Arrays.stream(plainData.trim().split("\n"))
                            .map(String::toLowerCase) // 모든 영문자를 소문자로 바꿈
                            .filter(s -> !s.isBlank()) // 빈 줄 제거
                            .toArray(String[]::new);

        for (String plainCommand : plainFile){
                MapLoadCommand command = null;

                if (plainCommand.startsWith(">")){ // section command로 판단
                    command = parseSectionCommand(plainData);
                }
                else if (plainCommand.startsWith(":")){ // special command로 판단
                    command = parseSpecialCommand(plainData);
                }
                else{ // command 맨 앞에 특정한 표시자가 없으면 instantiate command로 판단
                    command = parseInstantiateCommand(plainData);
                }
                
                commands.add(command);
            }

        return commands;
    }

    private MapLoadCommand parseInstantiateCommand(String plainData){
        String[] attributes = plainData.split("\\s+");

        if (attributes.length < 5) { throw new RuntimeException("생성 커맨드의 필수 속성값이 빠졌습니다."); }

        try {
            long instantiateTime = Long.parseLong(attributes[0]);
            int instantiateX = Integer.parseInt(attributes[1]);
            int instantiateY = Integer.parseInt(attributes[2]);
            GameObjectType gameObjectType = GameObjectType.fromValue(attributes[3]);
            String gameObjectName = attributes[4];

            ArrayList<String> extra = new ArrayList<>();
            for (int i = 5; i < attributes.length; i++){
                extra.add(attributes[i]);
            }

            return new InstantiateCommand(instantiateTime, instantiateX, instantiateY, gameObjectType, gameObjectName, extra);
        } catch (NumberFormatException  e) {
            throw new NumberFormatException("'생성 시간', '생성 x좌표', '생성 y좌표' 속성 중 하나가 숫자가 아닙니다.");
        }
    }

    private MapLoadCommand parseSectionCommand(String plainData){
        String commandType = plainData.substring(1);

        return new SectionCommand(SectionCommandType.fromValue(commandType));
    }

    private MapLoadCommand parseSpecialCommand(String plainData){
        String commandType = plainData.substring(1);

        return new SpecialCommand(SpecialCommandType.fromValue(commandType));
    }
}

package org.newdawn.spaceinvaders.singleton;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import org.newdawn.spaceinvaders.enums.GameObjectType;
import org.newdawn.spaceinvaders.enums.SectionType;
import org.newdawn.spaceinvaders.enums.SpecialCommandType;
import org.newdawn.spaceinvaders.map_load.SectionData;
import org.newdawn.spaceinvaders.map_load.map_load_commands.InstantiateCommand;
import org.newdawn.spaceinvaders.map_load.map_load_commands.MapLoadCommand;
import org.newdawn.spaceinvaders.map_load.map_load_commands.SectionCommand;
import org.newdawn.spaceinvaders.map_load.map_load_commands.SpecialCommand;

public class MapDataParser {
    private final static MapDataParser INSTANCE = new MapDataParser();
    public static MapDataParser getInstance()
    {
        return INSTANCE;
    }

    public Queue<SectionData> parseMapData(String plainData){
        Queue<MapLoadCommand> commands = parseCommands(plainData);

        return organizeCommandToSection(commands);
    }
    private Queue<MapLoadCommand> parseCommands(String plainData) {
        // 1. 데이터 전처리 (Stream Pipeline)
        String[] lines = Arrays.stream(plainData.trim().split("\n"))
                .map(String::toLowerCase)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toArray(String[]::new);

        Queue<MapLoadCommand> commands = new LinkedList<>();

        // 2. 변환 및 수집 (단순 반복)
        for (String line : lines) {
            MapLoadCommand command = convertLineToCommand(line);

            // null이 아닌 경우(유효한 커맨드인 경우)에만 추가
            if (command != null) {
                commands.add(command);
            }
        }

        return commands;
    }

    private MapLoadCommand convertLineToCommand(String line) {
        return switch (line.charAt(0)) {
            case '/' -> null;                     // 메타데이터
            case '>' -> parseSectionCommand(line);
            case ':' -> parseSpecialCommand(line);
            default  -> parseInstantiateCommand(line);
        };
    }
    private Queue<SectionData> organizeCommandToSection(Queue<MapLoadCommand> commands) {
        // 상태와 로직을 캡슐화한 객체 생성
        SectionAccumulator accumulator = new SectionAccumulator();

        // 조건: 커맨드가 남아있고 && 게임 종료 신호가 오지 않았을 때
        while (!commands.isEmpty() && !accumulator.isFinished()) {
            accumulator.process(commands.poll());
        }

        return accumulator.getSections();
    }

// --- 상태와 로직을 관리하는 헬퍼 클래스 (Inner Class) ---

    private static class SectionAccumulator {
        private final Queue<SectionData> sections = new LinkedList<>();
        private SectionCommand currentSectionCommand = null;
        private Queue<InstantiateCommand> currentInstantiateCommands = new LinkedList<>();
        private boolean finished = false;

        public void process(MapLoadCommand command) {
            if (command instanceof SectionCommand) {
                flush(); // 이전 섹션 저장
                currentSectionCommand = (SectionCommand) command;
                currentInstantiateCommands = new LinkedList<>();
            }
            else if (command instanceof InstantiateCommand) {
                currentInstantiateCommands.add((InstantiateCommand) command);
            }
            else if (isGameEndCommand(command)) {
                flush(); // 현재 섹션 저장
                finished = true; // 종료 플래그 설정
            }
        }

        public boolean isFinished() {
            return finished;
        }

        public Queue<SectionData> getSections() {
            return sections;
        }

        private void flush() {
            if (currentSectionCommand != null) {
                sections.add(new SectionData(currentSectionCommand, currentInstantiateCommands));
            }
        }

        private boolean isGameEndCommand(MapLoadCommand command) {
            return command instanceof SpecialCommand
                    && ((SpecialCommand) command).getSpecialCommandType() == SpecialCommandType.GAME_END;
        }
    }

// --- 추출된 헬퍼 메서드들 ---

    // 섹션 데이터를 리스트에 추가하는 로직 (중복 제거)
    private void flushCurrentSection(Queue<SectionData> sections,
                                     SectionCommand sectionCmd,
                                     Queue<InstantiateCommand> instantiateCmds) {
        if (sectionCmd != null) {
            sections.add(new SectionData(sectionCmd, instantiateCmds));
        }
    }

    // 종료 커맨드인지 확인하는 로직 (복잡한 조건식 캡슐화)
    private boolean isGameEndCommand(MapLoadCommand command) {
        return command instanceof SpecialCommand
                && ((SpecialCommand) command).getSpecialCommandType() == SpecialCommandType.GAME_END;
    }
        

    private MapLoadCommand parseInstantiateCommand(String plainData){
        String[] attributes = plainData.split("\\s+");

        if (attributes.length < 5) { throw new RuntimeException("생성 커맨드의 필수 속성값이 빠졌습니다."); }

        try {
            long instantiateTime = Long.parseLong(attributes[0]) << 16;
            long instantiateX = Long.parseLong(attributes[1]) << 16;
            long instantiateY = Long.parseLong(attributes[2]) << 16;
            GameObjectType gameObjectType = GameObjectType.fromValue(attributes[3]);
            int gameObjectName = Integer.parseInt(attributes[4]);

            ArrayList<String> extra = new ArrayList<>();
            for (int i = 5; i < attributes.length; i++){
                extra.add(attributes[i]);
            }

            return new InstantiateCommand(instantiateTime, instantiateX, instantiateY, gameObjectType, gameObjectName, extra);
        } catch (NumberFormatException  e) {
            throw new NumberFormatException("'생성 시간', '생성 x좌표', '생성 y좌표', '게임 오브젝트 Id' 속성 중 하나가 숫자가 아닙니다.");
        }
    }

    private MapLoadCommand parseSectionCommand(String plainData){
        String commandType = plainData.substring(1);

        return new SectionCommand(SectionType.fromValue(commandType));
    }

    private MapLoadCommand parseSpecialCommand(String plainData){
        String commandType = plainData.substring(1);

        return new SpecialCommand(SpecialCommandType.fromValue(commandType));
    }

    public static void main(String[] args) {
        Path filePath = Paths.get("src/main/resources/maps/map1.map"); // 파일 경로

        try {
            String content = Files.readString(filePath); // 파일 전체를 String으로 읽음
            new MapDataParser().parseMapData(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

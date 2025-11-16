package org.newdawn.spaceinvaders.loop.game_loop.game_loop_components;

import java.util.Queue;

import org.newdawn.spaceinvaders.PositionAngleSet;
import org.newdawn.spaceinvaders.enums.SectionType;
import org.newdawn.spaceinvaders.game_object.ingame.enemy.EnemyFactory;
import org.newdawn.spaceinvaders.game_object.ingame.player.PlayerShip;
import org.newdawn.spaceinvaders.game_object.ingame.store.StoreSlotFactory;
import org.newdawn.spaceinvaders.game_object.logic.HiveMind;
import org.newdawn.spaceinvaders.loop.GameLoop;
import org.newdawn.spaceinvaders.loop.game_loop.EventStoreSectionEnded;
import org.newdawn.spaceinvaders.map_load.SectionData;
import org.newdawn.spaceinvaders.map_load.map_load_commands.InstantiateCommand;
import org.newdawn.spaceinvaders.singleton.MapDataParser;

public class MapLoader {
    public static final int STORE_SECTION_DURATION = 15 << 16;
    
    private GameLoop gameLoop;

    private EnemyFactory enemyFactory;
    public EnemyFactory getEnemyFactory() { return enemyFactory; }
    private StoreSlotFactory storeSlotFactory;
    public StoreSlotFactory getStoreSlotFactory() { return storeSlotFactory; }

    private Queue<SectionData> sections;
    private boolean hasCurrentSectionEnd = true;
    private SectionData currentSection;
    public SectionData getCurrentSection() { return currentSection; }
    private long sectionElapsed = 0;
    public long getSectionElapsed() { return sectionElapsed; }
    public void addSectionElapsed(long deltaTime) { sectionElapsed += deltaTime; }
    
    private HiveMind enemyHiveMind = new HiveMind();
    public HiveMind getEnemyHiveMind() { return enemyHiveMind; }
    
    public MapLoader(GameLoop gameLoop){
        this.gameLoop = gameLoop;
        
        enemyFactory = new EnemyFactory(gameLoop);
        storeSlotFactory = new StoreSlotFactory(gameLoop);
    }

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public MapLoader(){
    }

    public void initEntities(){
        enemyHiveMind.cancelBroadcast();
    }

    public void parseMapData(String rawMapData){ sections = MapDataParser.getInstance().parseMapData(rawMapData); }
    
    public void executeMapCommand() {
        if (hasCurrentSectionInstantiateEnd()){
            if (hasCurrentSectionEnd){ startNextSection(); }
            else{ checkCurrentSectionEnd(); }
        }
        else{
            while (hasAvailableInstantiateCommand()){
                InstantiateCommand instantiateCommand = currentSection.pollNextInstantiateCommand();
                executeInstantiateCommand(instantiateCommand);
            }
        }
    }
    private void startNextSection(){
        currentSection = sections.poll();
        hasCurrentSectionEnd = false;

        //* 다음 section이 존재하지 않는다면 stage 클리어로 처리
        if (currentSection == null) {
            gameLoop.notifyWin();
            return;
        }
        
        if (currentSection.getSectionType() == SectionType.NEW_WAVE){
            for(PlayerShip ship: gameLoop.getPlayerShips()){
                ship.onWaveStart();
            }
        }
    }
    private void checkCurrentSectionEnd() {
        //* 현재 Section이 New Wave 타입이라면, 적이 모두 파괴되었을 때 다음 Section으로 넘어감
        if (currentSection.getSectionType() == SectionType.NEW_WAVE){
            if (!gameLoop.HasEnemy()){
                hasCurrentSectionEnd = true;
                sectionElapsed = 0;
            }
        }
        //* 현재 Section이 Store 타입이라면, Scection 시작 15초 후에 Section 종료
        if (currentSection.getSectionType() == SectionType.STORE){
            if (sectionElapsed >= STORE_SECTION_DURATION){
                hasCurrentSectionEnd = true;
                sectionElapsed = 0;

                gameLoop.publish(new EventStoreSectionEnded());
            }
        }
    }
    private boolean hasCurrentSectionInstantiateEnd() { return currentSection == null || !currentSection.hasMoreInstantiateCommands(); }
    private boolean hasAvailableInstantiateCommand() { return currentSection.hasMoreInstantiateCommands() && currentSection.getNextInstantiateCommandInstantiateTime() >= sectionElapsed; }

    private void executeInstantiateCommand(InstantiateCommand command) {
        PositionAngleSet positionAngleSet = new PositionAngleSet(command.getInstantiateX(), command.getInstantiateY());
        switch (command.getGameObjectType()) {
            case ENEMY:
                enemyFactory.spawnEnemy(enemyHiveMind, command.getGameObjectId(), positionAngleSet);
                break;
            case PASSIVE_SKILL:
                storeSlotFactory.createPassiveSkillItemSlot(command.getGameObjectId(), positionAngleSet, gameLoop.getPlayerShip(gameLoop.getMyPlayerID()));
                break;
            case ACTIVE_SKILL:
                storeSlotFactory.createActiveSkillItemSlot(command.getGameObjectId(), positionAngleSet);
                break;
            default:
                System.err.println("Invalid GameObjectType Detected");
                break;
        }
    }
}

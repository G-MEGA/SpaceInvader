package org.newdawn.spaceinvaders.loop;

import org.newdawn.spaceinvaders.Game;
import org.newdawn.spaceinvaders.enums.GameLoopResultType;
import org.newdawn.spaceinvaders.enums.GameObjectType;
import org.newdawn.spaceinvaders.enums.IndicatorTextType;
import org.newdawn.spaceinvaders.enums.PlayerPassiveSkillType;
import org.newdawn.spaceinvaders.enums.SectionType;
import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
import org.newdawn.spaceinvaders.loop_input.LoopInput;
import org.newdawn.spaceinvaders.loop_input.LoopInputLog;
import org.newdawn.spaceinvaders.map_load.SectionData;
import org.newdawn.spaceinvaders.map_load.map_load_commands.InstantiateCommand;
import org.newdawn.spaceinvaders.map_load.map_load_commands.MapLoadCommand;
import org.newdawn.spaceinvaders.map_load.map_load_commands.SectionCommand;
import org.newdawn.spaceinvaders.singleton.MapDataParser;
import org.newdawn.spaceinvaders.game_object.GameObject;
import org.newdawn.spaceinvaders.game_object.ingame.PlayerShip;
import org.newdawn.spaceinvaders.game_object.logic.HiveMind;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import org.newdawn.spaceinvaders.game_object.gui.TextRenderer;
import org.newdawn.spaceinvaders.game_object.ingame.player_skill.PassiveSkill;
import org.newdawn.spaceinvaders.game_object.ingame.player_skill.active_skill.BombSkill;
import org.newdawn.spaceinvaders.game_object.ingame.player_skill.active_skill.LaserSkill;
import org.newdawn.spaceinvaders.game_object.ingame.store.StoreSlot;
import org.newdawn.spaceinvaders.game_object.ingame.store.StoreSlotFactory;
import org.newdawn.spaceinvaders.game_object.ingame.enemy.Enemy;
import org.newdawn.spaceinvaders.game_object.ingame.enemy.EnemyFactory;
import random.SerializableRandom;

public class GameLoop extends Loop {
    long currentFrame;
    ArrayList<LoopInputLog> inputLogs = new ArrayList<>();

    private EnemyFactory enemyFactory;
    public EnemyFactory getEnemyFactory() { return enemyFactory; }
    private StoreSlotFactory storeSlotFactory;
    public StoreSlotFactory getStoreSlotFactory() { return storeSlotFactory; }

    SerializableRandom random;
    public SerializableRandom getRandom() {
        return random;
    }

    private int score = 0;
    public int getScore() { return score; }
    public void increaseScore() {increaseScore(100); }
    public void increaseScore(int amount) {
        if(gameResult != GameLoopResultType.InGame)return;
        score += amount;}
    private final long scoringTimeInterval = FixedPointUtil.ZERO_01;
    private final int scoringScore = 1;
    private long scoredTimeElapsed = 0;

    int myPlayerID = -1;

    /** The entity representing the player */
    private ArrayList<PlayerShip> ships = new ArrayList<>();
    public ArrayList<PlayerShip> getPlayerShips() { return ships; }
    public PlayerShip getPlayerShip(int id) { return ships.get(id); }

    private ArrayList<PlayerShip> aliveShips = new ArrayList<>();
    public PlayerShip getAliveShip(int index) { return aliveShips.get(index); }
    public int getAliveShipCount() { return aliveShips.size(); }
    public PlayerShip getRandomAlivePlayerShip(){
        if(aliveShips.isEmpty()){
            return null;
        } else if (aliveShips.size() == 1) {
            return aliveShips.get(0);
        } else{
            return aliveShips.get(getRandom().nextInt(aliveShips.size()));
        }
    }
    private HiveMind enemyHiveMind = new HiveMind();
    public HiveMind getEnemyHiveMind() { return enemyHiveMind; }
    private ArrayList<Enemy> enemies = new ArrayList<>();
    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
        addGameObject(enemy);
    }

    private GameLoopResultType gameResult = GameLoopResultType.InGame;
    public GameLoopResultType getGameResult() {
        return gameResult;
    }
    private void setGameResult(GameLoopResultType gameResult) {
        if(this.gameResult !=  gameResult) {
            this.gameResult = gameResult;

            onGameResultChanged();
        }
    }
    private void onGameResultChanged() {
        // 이를 GameLoopPlayerLoop에서 옵저버패턴으로 받아서 하이스코어 등록
    }

    //* 게임 화면에 존재하는 Text UI들
    private TextRenderer scoreText;
    private TextRenderer coinCountText;
    private TextRenderer playerHealthText;
    private TextRenderer activeSkillText;
    private TextRenderer passiveSkillHeaderText;
    private HashMap<PlayerPassiveSkillType, TextRenderer> passiveSkillsTexts;
    private void updatePassiveSkillText(){
        for (PlayerPassiveSkillType type : PlayerPassiveSkillType.values()) {
            TextRenderer text = passiveSkillsTexts.get(type);
            if (text == null){
                text = new TextRenderer(this, "", 10);
                passiveSkillsTexts.put(type, text);

                addGameObject(text);
            }

            text.setText(type.getName() + " : " + ships.get(myPlayerID).getPassiveSkillLevel(type));
        }
    }

    private TextRenderer indicatorText;
    private boolean isIndicatorShown = false;
    private final long indicatorShowTime = 2 << 16;
    private long indicatorShownElapsed = 0;
    public void showIndicatorText(String text) { showIndicatorText(text, IndicatorTextType.Default);}
    public void showIndicatorText(String text, IndicatorTextType type){ showIndicatorText(text, type.getColor(), type.getFontStyle()); }
    public void showIndicatorText(String text, Color color, int fontStyle){
        indicatorText.setText(text);
        indicatorText.setColor(color);
        indicatorText.setFontStyle(fontStyle);

        isIndicatorShown = true;
    }

    private final int bombDamage = 100;

    private long coinCount = 0;
    public long getCoinCount() { return coinCount; }
    public void increaseCoin(){ increaseCoin(1); }
    public void increaseCoin(long count){ coinCount += count; }
    /**
     * 현재 코인의 개수를 1만큼 감소시킵니다.
     * <p>
     * 내부적으로 {@link #decreaseCoin(long)}을 호출하며,
     * 감소가 가능하다면 코인을 차감하고 {@code true}를 반환합니다.
     * 만약 코인의 개수가 부족하여 차감이 불가능하다면 {@code false}를 반환합니다.
     *
     * @return 코인을 정상적으로 감소시켰다면 {@code true}, 그렇지 않다면 {@code false}
     */
    public boolean decreaseCoin() { return decreaseCoin(1); }
    /**
     * 현재 코인의 개수를 지정된 수치만큼 감소시킵니다.
     * <p>
     * {@code coinCount}가 감소시키려는 값 이상일 경우 차감이 가능하며,
     * 차감 후 {@code true}를 반환합니다.  
     * 만약 차감이 불가능하다면 코인의 개수는 변하지 않고 {@code false}를 반환합니다.
     *
     * @param count 감소시키려는 코인의 개수 (양수)
     * @return 코인을 정상적으로 감소시켰다면 {@code true}, 그렇지 않다면 {@code false}
     */
    public boolean decreaseCoin(long count) { 
        if (coinCount - count >= 0) {
            coinCount -= count;
            return true;
        }
        return false;    
    }

    //TODO NULL CHECKING 확인하기
    private Queue<SectionData> sections;
    private void parseMapData(String rawMapData){ sections = MapDataParser.getInstance().parseMapData(rawMapData); }
    private boolean hasSectionEnd = true;
    private SectionData currentSection;
    private long sectionElapsed = 0;

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public GameLoop(){
        super();
    }
    public GameLoop(Game game, int randomSeed, int playerCount, int myPlayerID, String rawMapData){
        super(game, playerCount);

        this.myPlayerID = myPlayerID;

        random = new SerializableRandom(17L * randomSeed); // 소수 17을 곱해서 더 랜덤하게
        parseMapData(rawMapData);

        startGame();
    }

    private void initText() {
        //* 좌측 상단 Text 관련 초기화
        passiveSkillsTexts = new HashMap<>();
        
        scoreText = new TextRenderer(this, "Score : 0", 15);
        scoreText.setFontStyle(1);
        coinCountText = new TextRenderer(this, "Coin : " + Long.toString(coinCount), 15);
        playerHealthText = new TextRenderer(this, "Health : " + Long.toString(ships.get(myPlayerID).getHealth()), 15);
        activeSkillText = new TextRenderer(this, "Active Skill : " + ships.get(myPlayerID).getActiveSkillName(), 15);
        passiveSkillHeaderText = new TextRenderer(this, "(Passive Skills)", 15);

        addGameObject(scoreText);
        addGameObject(coinCountText);
        addGameObject(playerHealthText);
        addGameObject(activeSkillText);
        addGameObject(passiveSkillHeaderText);

        updatePassiveSkillText();
        
        scoreText.setPos(0, 0);
        coinCountText.setPos(0 , 15 << 16);
        playerHealthText.setPos(0, 35 << 16);
        activeSkillText.setPos(0, 55 << 16);
        passiveSkillHeaderText.setPos(0, 75 << 16);
        int index = 0;
        for (TextRenderer text : passiveSkillsTexts.values()) {
            text.setPos(0, (95 + index++ * 10) << 16);
        }

        //* Indicator Text 관련 초기화
        indicatorText = new TextRenderer(this, "", 20);
        indicatorText.alignment = 1;
        indicatorText.setPos(400 << 16, 50 << 16);
        addGameObject(indicatorText);
    }

    private void updateText() {
        scoreText.setText("Score : " + score);
        coinCountText.setText("Coin : " + Long.toString(coinCount));
        playerHealthText.setText("Health : " + Long.toString(ships.get(myPlayerID).getHealth()) +
        (ships.get(myPlayerID).getCurrentShield() == 0 ? "" : " ( " + Integer.toString(ships.get(myPlayerID).getCurrentShield())  + " ) "));

        String activeSkillTextContent = "Active Skill : " + ships.get(myPlayerID).getActiveSkillName();
        activeSkillTextContent += ships.get(myPlayerID).isActiveSkillActable() ? "" : "( " + Long.toString(ships.get(myPlayerID).getRemainCoolTime() >> 16) + " )";
        activeSkillText.setText(activeSkillTextContent);
        updatePassiveSkillText();
    }
    /**
     * Start a fresh game, this should clear out any old data and
     * create a new set.
     */
    private void startGame() {
        // clear out any existing entities and intialise a new set
        for (GameObject gameObject : getGameObjects()) {
            gameObject.destroy();
        }
        score = 0;
        clearGameObjects();
        initEntities();
        initText();
        for(PlayerShip ship:ships){
            ship.onWaveStart();
        }
    }

    /**
     * Initialise the starting state of the entities (ship and aliens). Each
     * entitiy will be added to the overall list of entities in the game.
     */
    private void initEntities() {
        // create the player ship and place it roughly in the center of the screen
        ships.clear();

        //region 플레이어십 생성
        long shipSpawnDistance = 100L << 16;//플레이어십 간의 거리
        long shipSpawnAreaWidth = (getPlayerCount()-1) * shipSpawnDistance;
        long shipSpawnAreaPosX = (400L << 16) - shipSpawnAreaWidth/2;

        for (int playerID = 0; getPlayerCount() > playerID; playerID++) {
            PlayerShip ship = new PlayerShip(this, playerID);
            ships.add(ship);
            aliveShips.add(ship);
            addGameObject(ship);

            ship.setPos(shipSpawnAreaPosX + shipSpawnDistance * playerID, 550 << 16);
        }
        //endregion

        enemyFactory = new EnemyFactory(this);
        storeSlotFactory = new StoreSlotFactory(this);

        // enemyCount++; //TODO enemyCount를 배열 크기로바꾸기

        // create a block of aliens (5 rows, by 12 aliens, spaced evenly)
        // enemyCount = 0;
        // for (long row=0L;row<5L;row++) {
        //     Enemy enemy = null;
        //     for (long x=0L;x<12L;x++) {
        //         if (row == 4L){
        //             enemy = enemyFactory.spawnEnemy(enemyHiveMind, EnemyFactory.GUARDIAN, (100 << 16)+(x*(50 << 16)), (50 << 16) + (row << 16) * 30);
        //             enemy.setRotation(180 << 16);
        //         }
        //         else if (row == 3L){
        //             enemy = enemyFactory.spawnEnemy(enemyHiveMind, EnemyFactory.ARTILLERY, (100 << 16)+(x*(50 << 16)), (50 << 16) + (row << 16) * 30);
        //         }
        //         else if (row == 2L){
        //             enemy = enemyFactory.spawnEnemy(enemyHiveMind, EnemyFactory.RAIDER, (100 << 16)+(x*(50 << 16)), (50 << 16) + (row << 16) * 30);
        //             enemy.setRotation(180 << 16);
        //         }
        //         else{
        //             enemy = enemyFactory.spawnEnemy(enemyHiveMind, EnemyFactory.AILEN, (100 << 16)+(x*(50 << 16)), (50 << 16) + (row << 16) * 30);
        //         }
        //         enemyCount++;
        //         enemies.add(enemy);
        //     }
        // }

        // Enemy boss = enemyFactory.spawnEnemy(enemyHiveMind, EnemyFactory.BOSS, 400 << 16, 63 << 16);
        // enemyCount++;
        // enemies.add(boss);

        //* 상점 아이템 생성 슬롯
        BombSkill bombSkill = new BombSkill(this);
        StoreSlot storeSlot = new StoreSlot(this, 0, bombSkill, 600 << 16, 300 << 16);
        addGameObject(storeSlot);

        LaserSkill laserSkill = new LaserSkill(this);
        storeSlot = new StoreSlot(this, 0, laserSkill, 700 << 16, 300 << 16);
        addGameObject(storeSlot);

        // (타입, x, y) 정보를 담은 배열
        Object[][] skillData = {
            { PlayerPassiveSkillType.DamageUp, 100 << 16, 500 << 16 },
            { PlayerPassiveSkillType.DamageUp, 200 << 16, 500 << 16 },
            { PlayerPassiveSkillType.DamageUp, 300 << 16, 500 << 16 },
            { PlayerPassiveSkillType.DamageUp, 400 << 16, 500 << 16 },
        };

        // 반복문으로 생성
        for (Object[] data : skillData) {
            PlayerPassiveSkillType type = (PlayerPassiveSkillType) data[0];
            int x = (int) data[1];
            int y = (int) data[2];

            PassiveSkill passiveSkill = new PassiveSkill(type, this);
            storeSlot = new StoreSlot(this, 0, passiveSkill, x, y);
            addGameObject(storeSlot);
        }

        enemyHiveMind.cancelBroadcast();
        System.gc();
    }

    public long getCurrentTime(){
        return getGame().fixedDeltaTime * currentFrame;
    }

    //TODO notify류 메소드는 이벤트 버스 패턴으로 리펙토링하자...
    public void notifyBomb(){
        System.out.println("펑");
        for (Enemy enemy : enemies) {
            enemy.decreaseHealth(bombDamage);
        }
        
        cleanUpEnemies();
    }

    /**
     * Notification that the player has died.
     */
    public void notifyPlayerShipDeath() {
        aliveShips.clear();

        for(PlayerShip ship : ships){
            if(!ship.isDead()){aliveShips.add(ship);}
        }

        // 다 죽었으면 패배
        if(aliveShips.size()==0){setGameResult(GameLoopResultType.Lose);}
    }
    public void notifyLose(){
        setGameResult(GameLoopResultType.Lose);
    }

    /**
     * Notification that the player has won since all the aliens
     * are dead.
     */
    public void notifyWin() {
        setGameResult(GameLoopResultType.Win);
    }

    /**
     * Notification that an alien has been killed
     */
    public void notifyAlienKilled() {
        cleanUpEnemies();
        increaseScore();

        if (enemies.isEmpty()) {
            notifyWin();
        }

        //TODO Enemies 배열로 바꾸기
        for(GameObject gameObject : getGameObjects()){
            if(gameObject instanceof Enemy){
                ((Enemy) gameObject).velocityX = FixedPointUtil.mul(
                        ((Enemy) gameObject).velocityX,
                        FixedPointUtil.ONE + FixedPointUtil.ZERO_02);
            }
        }
    }

    public void notifyPlayerShipsSlowDown(long slowDownRatio, long slowDownTime){
        for (PlayerShip ship : ships) {
            ship.notifySlowDown(slowDownRatio, slowDownTime);
        }
    }

    public String getReplayData(){
//            리플레이 저장 입력 및 처리를 이 클래스에서 안하니 리플레이 녹화 버튼 입력이 리플레이 데이터에 들어가도 괜찮음
//            inputLogs.remove(inputLogs.size() - 1);  // 녹화버튼 입력 제외
//            inputLogs.remove(inputLogs.size() - 1);  // 녹화버튼 입력 제외

        // 현재 시간에 대한 인풋 하나 더 넣어서 마지막 입력으로부터 리플레이 저장 시점까지도 기록하게 함
        inputLogs.add(new LoopInputLog(currentFrame, new ArrayList<LoopInput>()));

        StringBuilder sb = new StringBuilder();
        for(LoopInputLog loopInputLog : inputLogs){
            String loopInputLogData = loopInputLog.toSaveData();

//            리플레이 저장 입력 및 처리를 이 클래스에서 안하니 리플레이 녹화 버튼 입력이 리플레이 데이터에 들어가도 괜찮음
//                if(loopInputLogData.contains("record"))
//                    continue;

            sb.append(loopInputLogData).append("\n");
        }

        String data = sb.toString();

        return data;
    }

    //* LootItem을 먹었을때, 나타나는 효과를 호출하는 메소드
    public void requestToSlowDownEnemies(){
        for (Enemy enemy : enemies){
            enemy.requestSlowDown();
        }
    }

    public void process(ArrayList<LoopInput> inputs){
        super.process(inputs);

        //section 실행 관련 로직
        if (currentSection == null || !currentSection.hasMoreInstantiateCommands()){
            if (hasSectionEnd){
                currentSection = sections.poll();
                //* 다음 section이 존재하지 않는다면 stage 클리어로 처리
                //TODO 근데 이러면 :game-end을 두는 의미가 없어지네
                if (currentSection == null) {
                    notifyWin();
                }
            }
            else{
                //* 현재 Section이 New Wave 타입이라면, 적이 모두 파괴되었을 때 다음 Section으로 넘어감
                if (currentSection.getSectionType() == SectionType.NewWave){
                    if (enemies.isEmpty()){
                        hasSectionEnd = true;
                        sectionElapsed = 0;
                    }
                }
                //* 현재 Section이 Store 타입이라면, Scection 시작 15초 후에 Section 종료
                if (currentSection.getSectionType() == SectionType.Store){  
                    if (sectionElapsed >= (15 << 16)){
                        hasSectionEnd = true;
                        sectionElapsed = 0;
                    }
                }
            }
        }
        else{
            System.out.println(currentSection.hasMoreInstantiateCommands());
            System.out.println(currentSection.getNextInstantiateCommandInstantiateTime() <= sectionElapsed);
            while (currentSection.hasMoreInstantiateCommands() && currentSection.getNextInstantiateCommandInstantiateTime() <= sectionElapsed){
                InstantiateCommand instantiateCommand = currentSection.pollNextInstantiateCommand();

                ExecuteInstantiateCommand(instantiateCommand);
            }
        }


        // 프레임별 입력 기록
        if(inputs != null && !inputs.isEmpty()){
            inputLogs.add(new LoopInputLog(currentFrame, inputs));
        }

        enemyHiveMind.broadcastIfRequested();

        processGameObjects();

        //* TextUI 관련 로직
        updateText();
        if (isIndicatorShown){
            if (indicatorShownElapsed > indicatorShowTime){
                isIndicatorShown = false;
                indicatorShownElapsed = 0;
                indicatorText.setText("");
            }
            else { indicatorShownElapsed += getGame().fixedDeltaTime; } //? 흠 deltaTime 아니긴 한데 괜찮겠지?
        }

        //endreigon
        if (gameResult ==  GameLoopResultType.InGame){
            if (scoredTimeElapsed >= scoringTimeInterval){
                increaseScore(scoringScore);
                scoredTimeElapsed = 0;
            }
            else{
                scoredTimeElapsed += getGame().fixedDeltaTime;
            }
        }

        currentFrame++;
        sectionElapsed += getGame().fixedDeltaTime;
    }

    private void ExecuteInstantiateCommand(InstantiateCommand command) {
        switch (command.getGameObjectType()) {
            case Enemy:
                enemyFactory.spawnEnemy(enemyHiveMind, command.getGameObjectId(), command.getInstantiateX(), command.getInstantiateY());
                break;
            case PassiveSkill:
                storeSlotFactory.createPassiveSkillItemSlot(command.getGameObjectId(), command.getInstantiateX(), command.getInstantiateY());
                break;
            case ActiveSkill:
                storeSlotFactory.createActiveSkillItemSlot(command.getGameObjectId(), command.getInstantiateX(), command.getInstantiateY());
                break;
        }
    }

    public void draw(Graphics2D g) {
        super.draw(g);
    }

    private void cleanUpEnemies() {
        //* 파괴된 enemy를 enemies 리스트에서 제거
        for(int i=enemies.size() - 1 ; i > -1;i--){
            if(enemies.get(i).isDestroyed()){
                enemies.remove(i);
            }
        }
    }
}
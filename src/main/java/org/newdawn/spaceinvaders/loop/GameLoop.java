package org.newdawn.spaceinvaders.loop;

import org.newdawn.spaceinvaders.Game;
import org.newdawn.spaceinvaders.enums.PlayerPassiveSkillType;
import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
import org.newdawn.spaceinvaders.game_loop_input.GameLoopInput;
import org.newdawn.spaceinvaders.game_loop_input.GameLoopInputLog;
import org.newdawn.spaceinvaders.game_object.GameObject;
import org.newdawn.spaceinvaders.game_object.ingame.PlayerShip;
import org.newdawn.spaceinvaders.game_object.logic.HiveMind;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFileChooser;

import org.newdawn.spaceinvaders.game_object.gui.TextRenderer;
import org.newdawn.spaceinvaders.game_object.ingame.enemy.Alien;
import org.newdawn.spaceinvaders.game_object.ingame.enemy.Artillery;
import org.newdawn.spaceinvaders.game_object.ingame.player_skill.PassiveSkill;
import org.newdawn.spaceinvaders.game_object.ingame.player_skill.active_skill.BasicActiveSkill;
import org.newdawn.spaceinvaders.game_object.ingame.player_skill.active_skill.LaserSkill;
import org.newdawn.spaceinvaders.game_object.ingame.store.StoreSlot;
import org.newdawn.spaceinvaders.game_object.ingame.enemy.Enemy;

public class GameLoop extends Loop {
    long currentFrame;
    ArrayList<GameLoopInputLog> inputLogs = new ArrayList<>();

    /** The entity representing the player */
    private PlayerShip ship;
    /** The number of enemies left on the screen */
    private int enemyCount;
    private HiveMind enemyHiveMind = new HiveMind();
    private ArrayList<Enemy> enemies = new ArrayList<>();

    /** The message to display which waiting for a key press */
    private String message = "";
    /** True if we're holding up game play until a key has been pressed */
    private boolean waitingForKeyPress = true;

    boolean forReplay = false;

    //* 게임 화면에 존재하는 Text UI들
    private TextRenderer coinCountText;
    private TextRenderer playerHealthText;
    private TextRenderer activeSkill;
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

            text.setText(type.getName() + " : " + ship.getPassiveSkillLevel(type));
        }
    }

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

    public GameLoop(Game game){
        super(game);
        // initialise the entities in our game so there's something
        // to see at startup
        initEntities();
        initText();
    }

    private void initText() {
        passiveSkillsTexts = new HashMap<>();
        
        coinCountText = new TextRenderer(this, "Coin : " + Long.toString(coinCount), 15);
        playerHealthText = new TextRenderer(this, "Health : " + Long.toString(ship.getHealth()), 15);
        activeSkill = new TextRenderer(this, "Active Skill : " + ship.getActiveSkillName(), 15);
        passiveSkillHeaderText = new TextRenderer(this, "(Passive Skills)", 15);

        gameObjects.add(coinCountText);
        gameObjects.add(playerHealthText);
        gameObjects.add(activeSkill);
        gameObjects.add(passiveSkillHeaderText);
        updatePassiveSkillText();

        coinCountText.setPos(0 , 10 << 16);
        playerHealthText.setPos(0, 30 << 16);
        activeSkill.setPos(0, 50 << 16);
        passiveSkillHeaderText.setPos(0, 70 << 16);
        int index = 0;
        for (TextRenderer text : passiveSkillsTexts.values()) {
            text.setPos(0, (90 + index++ * 10) << 16);
        }
    }

    private void updateText() {
        coinCountText.setText("Coin : " + Long.toString(coinCount));
        playerHealthText.setText("Health : " + Long.toString(ship.getHealth()));
        activeSkill.setText("Active Skill : " + ship.getActiveSkillName());
        updatePassiveSkillText();
    }

    public GameLoop(Game game, boolean forReplay){
        this(game);

        this.forReplay = forReplay;
    }
    /**
     * Start a fresh game, this should clear out any old data and
     * create a new set.
     */
    private void startGame() {
        // clear out any existing entities and intialise a new set
        for (GameObject gameObject : gameObjects) {
            gameObject.destroy();
        }
        gameObjects.clear();
        initEntities();
        initText();
    }

    /**
     * Initialise the starting state of the entities (ship and aliens). Each
     * entitiy will be added to the overall list of entities in the game.
     */
    private void initEntities() {
        // create the player ship and place it roughly in the center of the screen
        ship = new PlayerShip(this);
        ship.setPos(400 << 16, 550 << 16);
        gameObjects.add(ship);

        // create a block of aliens (5 rows, by 12 aliens, spaced evenly)
        enemyCount = 0;
        for (long row=0L;row<5L;row++) {
            for (long x=0L;x<12L;x++) {
                Enemy enemy;
                if (row <= 3L){
                    enemy = new Alien(this, enemyHiveMind);
                }
                else{
                    enemy = new Artillery(this, enemyHiveMind, ship);
                }
                enemy.setPos((100 << 16)+(x*(50 << 16)), (50 << 16) + (row << 16) * 30);
                gameObjects.add(enemy);
                enemies.add(enemy);
                enemyHiveMind.addListener(enemy);
                enemyCount++;
            }
        }

        //* 상점 아이템 생성 슬롯
        BasicActiveSkill basicActiveSkill = new BasicActiveSkill(ship, this);
        StoreSlot storeSlot = new StoreSlot(this, 0, basicActiveSkill, 600 << 16, 300 << 16);
        gameObjects.add(storeSlot);

        LaserSkill laserSkill = new LaserSkill(ship, this);
        storeSlot = new StoreSlot(this, 0, laserSkill, 700 << 16, 300 << 16);
        gameObjects.add(storeSlot);

        // (타입, x, y) 정보를 담은 배열
        Object[][] skillData = {
            { PlayerPassiveSkillType.AttackSpeed, 100 << 16, 300 << 16 },
            { PlayerPassiveSkillType.AttackSpeed, 200 << 16, 300 << 16 },
            { PlayerPassiveSkillType.AttackSpeed, 300 << 16, 300 << 16 },
            { PlayerPassiveSkillType.AttackSpeed, 400 << 16, 300 << 16 },
            { PlayerPassiveSkillType.AdditionalEngine, 100 << 16, 400 << 16 },
            { PlayerPassiveSkillType.AdditionalEngine, 200 << 16, 400 << 16 }
        };

        // 반복문으로 생성
        for (Object[] data : skillData) {
            PlayerPassiveSkillType type = (PlayerPassiveSkillType) data[0];
            int x = (int) data[1];
            int y = (int) data[2];

            PassiveSkill passiveSkill = new PassiveSkill(type, ship, this);
            storeSlot = new StoreSlot(this, 0, passiveSkill, x, y);
            gameObjects.add(storeSlot);
        }

        // PassiveSkill passiveSkill = new PassiveSkill("Fuck", "sprites/testPassiveSkill.png", ship, this, "응애");
        // storeSlot = new StoreSlot(this,1, passiveSkill, 500 << 16, 300 << 16);
        // gameObjects.add(storeSlot);

        enemyHiveMind.cancelBroadcast();
        System.gc();
    }

    public long getCurrentTime(){
        return getGame().fixedDeltaTime * currentFrame;
    }
    public boolean isWaitingForKeyPress(){
        return waitingForKeyPress;
    }

    /**
     * Notification that the player has died.
     */
    public void notifyDeath() {
        message = "Oh no! They got you, try again?";
        waitingForKeyPress = true;
    }

    /**
     * Notification that the player has won since all the aliens
     * are dead.
     */
    public void notifyWin() {
        message = "Well done! You Win!";
        waitingForKeyPress = true;
    }

    /**
     * Notification that an alien has been killed
     */
    public void notifyAlienKilled() {
        // reduce the alient count, if there are none left, the player has won!
        enemyCount--;

        if (enemyCount == 0) {
            notifyWin();
        }

        for(GameObject gameObject : gameObjects){
            if(gameObject instanceof Enemy){
                ((Enemy) gameObject).velocityX = FixedPointUtil.mul(
                        ((Enemy) gameObject).velocityX,
                        FixedPointUtil.ONE + FixedPointUtil.ZERO_02);
            }
        }
    }

    //* LootItem을 먹었을때, 나타나는 효과를 호출하는 메소드
    public void addShieldOnPlayerShip() { ship.increaseHealth(); }
    public void requestToSpeedUpOnPlayerShip() { ship.requestToSpeedUp(); }
    public void requestToSlowDownEnemies(){
        for (Enemy enemy : enemies){
            enemy.requestSlowDown();
        }
    }

    public void process(ArrayList<GameLoopInput> inputs){
        super.process(inputs);

        // 프레임별 입력 기록
        if(inputs != null && !inputs.isEmpty()){
            inputLogs.add(new GameLoopInputLog(currentFrame, inputs));
        }

        //region 로직
        if (waitingForKeyPress && isKeyInputJustPressed("accept")) {
            GameLoopInputLog lastestLog = inputLogs.get(inputLogs.size()-1);
            inputLogs.clear();

            currentFrame = 0;

            lastestLog.inputFrame = currentFrame;

            inputLogs.add(lastestLog);

            startGame();
            waitingForKeyPress = false;
        }
        if(isKeyInputJustPressed("escape")) {
            getGame().changeLoop(new MainMenuLoop(getGame()));
        }

        if(!forReplay && isKeyInputJustPressed("record")) {
//            forReplay 변수가 있으니 리플레이 녹화 버튼 입력이 리플레이 데이터에 들어가도 괜찮음
//            inputLogs.remove(inputLogs.size() - 1);  // 녹화버튼 입력 제외
//            inputLogs.remove(inputLogs.size() - 1);  // 녹화버튼 입력 제외

            StringBuilder sb = new StringBuilder();
            for(GameLoopInputLog gameLoopInputLog : inputLogs){
                String gameLoopInputLogData = gameLoopInputLog.toSaveData();

//            forReplay 변수가 있으니 리플레이 녹화 버튼 입력이 리플레이 데이터에 들어가도 괜찮음
//                if(gameLoopInputLogData.contains("record"))
//                    continue;

                sb.append(gameLoopInputLogData).append("\n");
            }

            String data = sb.toString();

            // JFileChooser 객체 생성
            JFileChooser chooser = new JFileChooser();

            // 파일 저장 다이얼로그를 현재 프레임(this) 중앙에 띄움
            int result = chooser.showSaveDialog(getGame());

            // 사용자가 '저장' 버튼을 눌렀는지 확인
            if (result == JFileChooser.APPROVE_OPTION) {
                // 저장할 파일 경로를 File 객체로 받아옴
                File fileToSave = chooser.getSelectedFile();

                String filePath = fileToSave.getAbsolutePath(); // 저장할 파일 경로

                if (!filePath.endsWith(".txt")) {
                    fileToSave = new File(fileToSave.getAbsolutePath() + ".txt");
                    filePath = fileToSave.getAbsolutePath();
                }

                try {
                    // Path 객체 생성
                    Path path = Paths.get(filePath);

                    // 파일에 문자열 쓰기 (기본 인코딩은 UTF-8)
                    Files.writeString(path, data);

                    System.out.println("파일 저장 성공");

                } catch (IOException e) {
                    System.err.println("파일 저장 중 오류가 발생");
                    e.printStackTrace();
                }
            }

            inputLogs.clear();

            // 쓸데없는 예외가 생길 가능성을 봉쇄하기 위하여
            // 리플레이 저장 즉시 메인메뉴로 사출
            getGame().changeLoop(new MainMenuLoop(getGame()));
        }

        if(!waitingForKeyPress){
            enemyHiveMind.broadcastIfRequested();

            processGameObjects();
        }

        updateText();
        //endreigon

        currentFrame++;
    }

    public void draw(Graphics2D g) {
        super.draw(g);
        // if we're waiting for an "any key" press then draw the
        // current message
        if (waitingForKeyPress) {
            g.setColor(Color.white);
            g.drawString(message,(800-g.getFontMetrics().stringWidth(message))/2,250);
            g.drawString("Press 'Accept' key",(800-g.getFontMetrics().stringWidth("Press 'Accept' key"))/2,300);
        }
        
        // String coinText = "Coin : " + Long.toString(coinCount);
        // g.setColor(Color.white);
        // g.drawString(coinText,0,10);

        // String shieldText = "Health : " + Long.toString(ship.getHealth());
        // g.setColor(Color.white);
        // g.drawString(shieldText,0,30);

        // String activeSkillText = "Active Skill : " + ship.getActiveSkillName();
        // g.setColor(Color.white);
        // g.drawString(activeSkillText,0,50);

        // String passiveSkillsText = "(Passive Skills)";
        // g.setColor(Color.white);
        // g.drawString(passiveSkillsText,0,70);
        // int i = 1;
        // for (PlayerPassiveSkillType type : PlayerPassiveSkillType.values()) {
        //     passiveSkillsText = type.getName() + " : " + ship.getPassiveSkillLevel(type) + "\n";
        //     g.drawString(passiveSkillsText,0,70 + 20 * i++);
        // }
    }

}
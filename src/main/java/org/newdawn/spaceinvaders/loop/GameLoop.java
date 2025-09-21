package org.newdawn.spaceinvaders.loop;

import org.newdawn.spaceinvaders.Game;
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

import javax.swing.JFileChooser;
import org.newdawn.spaceinvaders.game_object.ingame.enemy.Alien;
import org.newdawn.spaceinvaders.game_object.ingame.enemy.Bullet;

public class GameLoop extends Loop {
    long currentFrame;
    ArrayList<GameLoopInputLog> inputLogs = new ArrayList<>();

    /** The entity representing the player */
    private PlayerShip ship;
    /** The number of aliens left on the screen */
    private int alienCount;
    private HiveMind alienHiveMind = new HiveMind();

    /** The message to display which waiting for a key press */
    private String message = "";
    /** True if we're holding up game play until a key has been pressed */
    private boolean waitingForKeyPress = true;

    boolean forReplay = false;

    //* Coin 관련 로직 */
    private long coinCount = 0;
    public void addCoin(){ addCoin(1); }
    public void addCoin(long count){ coinCount += count; }

    public GameLoop(Game game){
        super(game);
        // initialise the entities in our game so there's something
        // to see at startup
        initEntities();
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
        alienCount = 0;
        for (long row=0L;row<5L;row++) {
            for (long x=0L;x<12L;x++) {
                Alien alien = new Alien(this, alienHiveMind);
                alien.setPos((100 << 16)+(x*(50 << 16)), (50 << 16) + (row << 16) * 30);
                gameObjects.add(alien);
                alienHiveMind.addListener(alien);
                alienCount++;
            }
        }

        alienHiveMind.cancelBroadcast();
        System.gc();
    }

    public long getCurrentTime(){
        return game.fixedDeltaTime * currentFrame;
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
        alienCount--;

        if (alienCount == 0) {
            notifyWin();
        }

        for(GameObject gameObject : gameObjects){
            if(gameObject instanceof Alien){
                ((Alien) gameObject).velocityX = FixedPointUtil.mul(
                        ((Alien) gameObject).velocityX,
                        FixedPointUtil.ONE + FixedPointUtil.ZERO_02);
            }
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
            game.changeLoop(new MainMenuLoop(game));
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
            int result = chooser.showSaveDialog(game);

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
            game.changeLoop(new MainMenuLoop(game));
        }

        if(!waitingForKeyPress){
            alienHiveMind.broadcastIfRequested();

            processGameObjects();
        }

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
        
        String coinText = "Coin : " + Long.toString(coinCount);
        g.setColor(Color.white);
        g.drawString(coinText,0,10);
    }
}
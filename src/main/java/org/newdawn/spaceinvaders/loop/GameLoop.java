package org.newdawn.spaceinvaders.loop;

import org.newdawn.spaceinvaders.Game;
import org.newdawn.spaceinvaders.game_loop_input.GameLoopInput;
import org.newdawn.spaceinvaders.game_loop_input.GameLoopInputLog;
import org.newdawn.spaceinvaders.game_object.GameObject;
import org.newdawn.spaceinvaders.game_object.ingame.Alien;
import org.newdawn.spaceinvaders.game_object.ingame.Bullet;
import org.newdawn.spaceinvaders.game_object.ingame.PlayerShip;
import org.newdawn.spaceinvaders.game_object.logic.HiveMind;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class GameLoop extends Loop {
    long currentFrame;
    ArrayList<GameLoopInputLog> inputLogs = new ArrayList<>();

    /** The entity representing the player */
    private PlayerShip ship;
    /** The speed at which the player's ship should move (pixels/sec) */
    private double moveSpeed = 300;
    /** The time at which last fired a shot */
    private double lastFire = 0;
    /** The interval between our players shot (ms) */
    private double firingInterval = 0.1f;
    /** The number of aliens left on the screen */
    private int alienCount;
    private HiveMind alienHiveMind = new HiveMind();

    /** The message to display which waiting for a key press */
    private String message = "";
    /** True if we're holding up game play until a key has been pressed */
    private boolean waitingForKeyPress = true;

    boolean forReplay = false;

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
        ship.setPosition(370, 550);
        gameObjects.add(ship);

        // create a block of aliens (5 rows, by 12 aliens, spaced evenly)
        alienCount = 0;
        for (int row=0;row<5;row++) {
            for (int x=0;x<12;x++) {
                Alien alien = new Alien(this, alienHiveMind);
                alien.setPosition(100+(x*50), (50)+row*30);
                gameObjects.add(alien);
                alienHiveMind.addListener(alien);
                alienCount++;
            }
        }

        alienHiveMind.cancelBroadcast();
        System.gc();
    }

    public double getCurrentTime(){
        return game.fixedDeltaTime * currentFrame;
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
                ((Alien) gameObject).velocityX = ((Alien) gameObject).velocityX * 1.02;
            }
        }
    }

    /**
     * Attempt to fire a shot from the player. Its called "try"
     * since we must first check that the player can fire at this
     * point, i.e. has he/she waited long enough between shots
     */
    public void tryToFire() {
        // check that we have waiting long enough to fire
        if (getCurrentTime() - lastFire < firingInterval) {
            return;
        }

        // if we waited long enough, create the shot entity, and record the time.
        lastFire = getCurrentTime();
        Bullet bullet = new Bullet(this);
        bullet.setPosition(ship.getX()+10, ship.getY()-30);
        gameObjects.add(bullet);
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

            //propagate 프로세스
            for(GameObject gameObject : gameObjects){
                if(!gameObject.isDestroyed()) gameObject.propagateProcess(game.fixedDeltaTime);
            }

            // 충돌 처리
            processCollision2D();

            //propagate 포스트 프로세스
            for(GameObject gameObject : gameObjects){
                if(!gameObject.isDestroyed()) gameObject.propagatePostProcess(game.fixedDeltaTime);
            }

            //destroy 된 것 있으면 GameObject 목록에서 제거
            for(int i=gameObjects.size() - 1 ; i > -1;i--){
                if(gameObjects.get(i).isDestroyed()){
                    gameObjects.remove(i);
                }
            }

            // resolve the movement of the ship. First assume the ship
            // isn't moving. If either cursor key is pressed then
            // update the movement appropraitely
            ship.velocityX = 0;

            if ((isKeyInputPressed("left")) && (!isKeyInputPressed("right"))) {
                ship.velocityX = -moveSpeed;
            } else if ((isKeyInputPressed("right")) && (!isKeyInputPressed("left"))) {
                ship.velocityX = moveSpeed;
            }

            // if we're pressing fire, attempt to fire
            if (isKeyInputPressed("fire")) {
                tryToFire();
            }
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
    }
}
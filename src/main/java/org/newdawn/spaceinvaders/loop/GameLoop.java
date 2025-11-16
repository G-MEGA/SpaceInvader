package org.newdawn.spaceinvaders.loop;

import event_bus.EventBus;
import networking.rudp.Connection;
import networking.rudp.IRUDPPeerListener;
import networking.rudp.PacketData.PacketData;
import networking.rudp.RUDPPeer;
import org.newdawn.spaceinvaders.Game;
import org.newdawn.spaceinvaders.enums.GameLoopResultType;
import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
import org.newdawn.spaceinvaders.loop.game_loop.IGameLoopGameResultListener;
import org.newdawn.spaceinvaders.loop.game_loop.game_loop_components.CoinSystem;
import org.newdawn.spaceinvaders.loop.game_loop.game_loop_components.GameLoopEnemySystem;
import org.newdawn.spaceinvaders.loop.game_loop.game_loop_components.GameLoopPlayerShipSystem;
import org.newdawn.spaceinvaders.loop.game_loop.game_loop_components.GameLoopTextSystem;
import org.newdawn.spaceinvaders.loop.game_loop.game_loop_components.MapLoader;
import org.newdawn.spaceinvaders.loop.game_loop.game_loop_components.ScoreSystem;
import org.newdawn.spaceinvaders.loop_input.LoopInput;
import org.newdawn.spaceinvaders.loop_input.LoopInputLog;
import org.newdawn.spaceinvaders.network.LoopRUDPPeerListener;

import org.newdawn.spaceinvaders.game_object.GameObject;

import java.awt.Graphics2D;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.newdawn.spaceinvaders.game_object.ingame.enemy.Enemy;
import org.newdawn.spaceinvaders.game_object.ingame.player.PlayerShip;

import random.SerializableRandom;

public class GameLoop extends Loop {
    long currentFrame;
    ArrayList<LoopInputLog> inputLogs = new ArrayList<>();

    public MapLoader mapLoader;
    public ScoreSystem scoreSystem;
    public CoinSystem coinSystem;
    public GameLoopTextSystem textSystem;
    public GameLoopPlayerShipSystem playerShipSystem;
    public GameLoopEnemySystem enemySystem;

    int mapID = -1;

    long randomSeed = -1;
    SerializableRandom random;
    public SerializableRandom getRandom() {
        return random;
    }

    private EventBus eventBus = new EventBus();
    public EventBus getEventBus() { return eventBus; }

    public GameLoopResultType gameResult = GameLoopResultType.IN_GAME;
    public GameLoopResultType getGameResult() {
        return gameResult;
    }
    private void setGameResult(GameLoopResultType gameResult) {
        if(this.gameResult != GameLoopResultType.IN_GAME)return;  // 낙장불입

        if(this.gameResult !=  gameResult) {
            this.gameResult = gameResult;

            onGameResultChanged();
        }
    }
    private void onGameResultChanged() {
        if(gameResultListener == null) return;

        gameResultListener.onGameResultChanged(gameResult);
    }
    public transient IGameLoopGameResultListener gameResultListener;

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public GameLoop(){
        super();
    }
    public GameLoop(Game game, long randomSeed, int playerCount, int myPlayerID, int mapID){
        super(game, playerCount);

        this.randomSeed = randomSeed;
        this.mapID = mapID;
        
        random = new SerializableRandom(17L * randomSeed); // 소수 17을 곱해서 더 랜덤하게
        
        //mapID에 따른 맵 불러오기
        Path filePath = game.getMapList().getList().get(mapID).getPath(); // 파일 경로
        String rawMapData = "";
        try {
            rawMapData = Files.readString(filePath); // 파일 전체를 String으로 읽음
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        mapLoader = new MapLoader(this);
        mapLoader.parseMapData(rawMapData);
        
        scoreSystem = new ScoreSystem(this);
        coinSystem = new CoinSystem(this);
        
        textSystem = new GameLoopTextSystem(this);
        
        playerShipSystem = new GameLoopPlayerShipSystem(this, myPlayerID);
        
        enemySystem = new GameLoopEnemySystem(this);
        startGame();
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
        
        scoreSystem.initScore();
        clearGameObjects();
        playerShipSystem.initPlayerShips();
        textSystem.initText();
    }

    public long getCurrentTime(){
        return getGame().fixedDeltaTime * currentFrame;
    }

    /**
     * Notification that the player has died.
     */
    public void notifyPlayerShipDeath() {
        playerShipSystem.notifyPlayerShipDeath();

        // 다 죽었으면 패배
        if(playerShipSystem.HasAllDied()){setGameResult(GameLoopResultType.LOSE);}
    }
    public void notifyLose(){
        setGameResult(GameLoopResultType.LOSE);
    }

    /**
     * Notification that the player has won since all the aliens
     * are dead.
     */
    public void notifyWin() {
        setGameResult(GameLoopResultType.WIN);
    }

    /**
     * Notification that an alien has been killed
     */
    public void notifyAlienKilled() {
        enemySystem.cleanUpEnemies();
        scoreSystem.increaseScore();

        //TODO Enemies 배열로 바꾸기
        for(GameObject gameObject : getGameObjects()){
            if(gameObject instanceof Enemy){
                ((Enemy) gameObject).velocityX = FixedPointUtil.mul(
                        ((Enemy) gameObject).velocityX,
                        FixedPointUtil.ONE + FixedPointUtil.ZERO_02);
            }
        }

        enemySystem.checkAllEnemiesDead();
    }

    public void notifyPlayerShipsSlowDown(long slowDownRatio, long slowDownTime){
        for (PlayerShip ship : playerShipSystem.getAliveShips()) {
            ship.notifySlowDown(slowDownRatio, slowDownTime);
        }
    }

    public void notifySkillStoreItemAcquired() {
        //* 흠
    }
    
    public void requestToSlowDownEnemies(){
        for (Enemy enemy : enemySystem.getEnemies()){
            enemy.requestSlowDown();
        }
    }

    public String getReplayData(){
        StringBuilder sb = new StringBuilder();

        //region GameLoop 구성 데이터 저장
        sb.append("GameLoop::randomSeed=").append(randomSeed).append("\n");
        sb.append("GameLoop::playerCount=").append(getPlayerCount()).append("\n");
        sb.append("GameLoop::myPlayerID=").append(playerShipSystem.getMyPlayerID()).append("\n");
        sb.append("GameLoop::mapID=").append(mapID).append("\n");
        //endregion

        //region 인풋 데이터 저장
        // 현재 시간에 대한 인풋 하나 더 넣어서 마지막 입력으로부터 리플레이 저장 시점까지도 기록하게 함
        inputLogs.add(new LoopInputLog(currentFrame, new ArrayList<LoopInput>()));
        // 모든 인풋 데이터 저장
        for(LoopInputLog loopInputLog : inputLogs){
            String loopInputLogData = loopInputLog.toSaveData();

            sb.append(loopInputLogData).append("\n");
        }
        //endregion

        return sb.toString();
    }

    public void process(ArrayList<LoopInput> inputs){
        super.process(inputs);

        // 이 클래스는 직접적으로 네트워킹 하지 않으니 주석처리
//        getGame().getRudpPeer().processReceivedData();

        if (gameResult == GameLoopResultType.IN_GAME){
            mapLoader.executeMapCommand();
        }

        // 이 클래스는 직접적으로 네트워킹 하지 않으니 주석처리
//        getGame().getRudpPeer().processReceivedData();

        // 프레임별 입력 기록
        if(inputs != null && !inputs.isEmpty()){
            inputLogs.add(new LoopInputLog(currentFrame, inputs));
        }

        mapLoader.getEnemyHiveMind().broadcastIfRequested();

        processGameObjects();

        //* TextUI 관련 로직
        textSystem.updateText();
        textSystem.updateIndicatorText(getGame().fixedDeltaTime);
        
        scoreSystem.requestTimeScoring(gameResult, getGame().fixedDeltaTime);
        currentFrame++;
        mapLoader.addSectionElapsed(getGame().fixedDeltaTime);
    }

    public void draw(Graphics2D g) {
        super.draw(g);
    }

    @Override
    protected IRUDPPeerListener generateIRUDPPeerListener() {
        return new LoopRUDPPeerListener() {
            @Override
            public boolean onConnected(RUDPPeer peer, Connection connection) {
                return false;
            }

            @Override
            public boolean onReceived(RUDPPeer peer, Connection connection, PacketData data) {
                return false;
            }
        };
    }
}
package org.newdawn.spaceinvaders.loop;

import networking.rudp.Connection;
import networking.rudp.IRUDPPeerListener;
import networking.rudp.PacketData.PacketData;
import networking.rudp.RUDPPeer;
import org.newdawn.spaceinvaders.Game;
import org.newdawn.spaceinvaders.loop_input.LoopInput;
import org.newdawn.spaceinvaders.loop_input.LoopInputLog;
import org.newdawn.spaceinvaders.network.LoopRUDPPeerListener;

import java.awt.*;
import java.util.ArrayList;

public class ReplayerLoop extends Loop{
    String replaySaveData;

    long currentFrame;
    int currentLogIndex = 0;

    ArrayList<LoopInputLog> inputLogs = new ArrayList<>();
    GameLoop gameLoop;
    int playSpeed = 1;
    boolean paused = false;

    long rollbackFrame = -1;
    int rollbackLogIndex = -1;

    byte[] rollbackSnapshot;

    public ReplayerLoop(Game game, String replaySaveData) {
        super(game);

        this.replaySaveData = replaySaveData;

        String[] splited = replaySaveData.trim().split("\n");

        long randomSeed = -1;
        int playerCount = -1;
        int myPlayerID = -1;
        int mapID = -1;

        for(String s: splited){
            if(s.startsWith("GameLoop::randomSeed=")){
                randomSeed = Long.parseLong(s.split("=")[1]);
            }
            else if(s.startsWith("GameLoop::playerCount=")){
                playerCount = Integer.parseInt(s.split("=")[1]);
            }
            else if(s.startsWith("GameLoop::myPlayerID=")){
                myPlayerID = Integer.parseInt(s.split("=")[1]);
            }
            else if(s.startsWith("GameLoop::mapID=")){
                mapID = Integer.parseInt(s.split("=")[1]);
            }
            else{
                inputLogs.add(new LoopInputLog(s));
            }
        }

        gameLoop = new GameLoop(game, randomSeed, playerCount , myPlayerID , mapID );
        currentFrame = gameLoop.currentFrame;
    }

    @Override
    public void process(ArrayList<LoopInput> inputs) {
        super.process(inputs);
        getGame().getRudpPeer().processReceivedData();
        handleInputs();

        // 복잡한 루프 로직을 별도 메서드로 위임하여 흐름을 단순화
        runSimulationLoop();
    }

    /**
     * 정해진 배속(playSpeed)만큼 시뮬레이션을 반복 실행합니다.
     */
    private void runSimulationLoop() {
        int stepsRemaining = playSpeed;

        // while 조건에 후위 연산자를 사용하여 내부의 'leftLoop--' 제거 및 라인 단축
        while (!paused && stepsRemaining-- > 0) {
            // 1. 로그가 끝났으면 루프 종료 (Guard Clause)
            if (inputLogs.size() <= currentLogIndex) {
                return;
            }

            // 2. 단일 프레임 처리 (메서드 추출)
            processSingleFrame();
        }
    }

    /**
     * 한 프레임의 로직을 처리합니다.
     */
    private void processSingleFrame() {
        LoopInputLog currentLog = inputLogs.get(currentLogIndex);

        // 1. 유효성 검사 (Guard Clause)
        validateFrameOrder(currentLog);

        // 2. 게임 로직 수행 (분기 로직)
        applyGameProcess(currentLog);

        // 3. 프레임 증가
        currentFrame++;
    }

    /**
     * 현재 로그가 유효한 순서인지 검사합니다.
     */
    private void validateFrameOrder(LoopInputLog currentLog) {
        if (currentLog.inputFrame < gameLoop.currentFrame) {
            throw new IllegalStateException("currentLog.inputFrame < gameLoop.currentFrame");
        }
    }

    /**
     * 조건에 따라 게임 루프를 진행합니다.
     * (순수 비즈니스 로직)
     */
    private void applyGameProcess(LoopInputLog currentLog) {
        if (currentLog.inputFrame == gameLoop.currentFrame) {
            gameLoop.process(currentLog.inputs);
            currentLogIndex++;
        } else {
            gameLoop.process();
        }
    }
    private void handleInputs(){
        if(isKeyInputJustPressed(0, "escape")) {
            getGame().changeLoop(new MainMenuLoop(getGame()));
        }

        if(isKeyInputJustPressed(0, "right")) {
            playSpeed++;
        }
        if(isKeyInputJustPressed(0, "left")) {
            playSpeed--;
            if(playSpeed<0){
                playSpeed = 0;
            }
        }
        if(isKeyInputJustPressed(0, "accept")) {
            paused = !paused;
        }
        // Replay 다시 재생
        if(isKeyInputJustPressed(0, "record")) {
            getGame().changeLoop(new ReplayerLoop(getGame(), replaySaveData));
        }
    }

    @Override
    protected IRUDPPeerListener generateIRUDPPeerListener() {
        return new  LoopRUDPPeerListener() {
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

    @Override
    public void draw(Graphics2D g) {
        gameLoop.draw(g);

        super.draw(g);

        Font font = g.getFont();
        g.setFont(new Font(font.getFontName(), Font.BOLD, 20));

        String message;
        int messageY = 590;

        message = "● Replay - 좌우 방향키로 배속조절, 엔터로 일시정지";
        g.setColor(Color.red);
        g.drawString(message,5,messageY);

        message = "PlaySpeed : x" + String.valueOf(playSpeed);
        g.setColor(Color.red);
        g.drawString(message,600,messageY);

        messageY -= 20;

        message = "Frame Number : " + String.valueOf(currentFrame);
        g.setColor(Color.red);
        g.drawString(message,5,messageY);
        messageY -= 20;

//        message = "PlaySpeed : x" + String.valueOf(playSpeed);
//        g.setColor(Color.red);
//        g.drawString(message,5,messageY);
//        messageY -= 20;
    }
}

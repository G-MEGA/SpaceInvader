package org.newdawn.spaceinvaders.loop;

import networking.Network;
import networking.rudp.Connection;
import networking.rudp.IRUDPPeerListener;
import networking.rudp.PacketData.PacketData;
import networking.rudp.RUDPPeer;
import org.newdawn.spaceinvaders.Game;
import org.newdawn.spaceinvaders.loop_input.LoopInput;
import org.newdawn.spaceinvaders.loop_input.LoopInputLog;
import serializer.GameLoopSerializer;

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

        int randomSeed = -1;
        int playerCount = -1;
        int myPlayerID = -1;
        int mapID = -1;

        for(String s: splited){
            if(s.startsWith("GameLoop::randomSeed=")){
                randomSeed = Integer.parseInt(s.split("=")[1]);
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
            return;
        }

        //TODO Rollback Test
        if (isKeyInputPressed(0, "mouse_button_left")) {
            long startTime = System.nanoTime();

            rollbackFrame = currentFrame;
            rollbackLogIndex = currentLogIndex;

            // 직렬화
            rollbackSnapshot = GameLoopSerializer.getInstance().serialize(gameLoop);

            long endTime = System.nanoTime();
            // 4. 소요 시간 계산 및 출력
            long durationNs = endTime - startTime; // 나노초 단위
            double durationMs = durationNs / 1_000_000.0; // 밀리초 단위로 변환
            System.out.println("직렬화된 데이터 크기: " + rollbackSnapshot.length + " bytes");
            System.out.println("직렬화에 걸린 시간: " + durationNs + " ns");
            System.out.println("직렬화에 걸린 시간: " + String.format("%.6f", durationMs) + " ms");
        }
        else if(isKeyInputJustPressed(0, "mouse_button_right") && rollbackFrame >= 0 && rollbackSnapshot != null) {
            long startTime = System.nanoTime();

            currentFrame = rollbackFrame;
            currentLogIndex = rollbackLogIndex;

            // 역직렬화
            gameLoop = GameLoopSerializer.getInstance().deserialize(rollbackSnapshot);;
            gameLoop.setGame(getGame());

            long endTime = System.nanoTime();
            // 소요 시간 계산 및 출력
            long durationNs = endTime - startTime; // 나노초 단위
            double durationMs = durationNs / 1_000_000.0; // 밀리초 단위로 변환
            System.out.println("REVERSE 직렬화에 걸린 시간: " + durationNs + " ns");
            System.out.println("REVERSE 직렬화에 걸린 시간: " + String.format("%.6f", durationMs) + " ms");
        }


        int leftLoop = playSpeed;

        while(!paused && leftLoop>0){
            leftLoop --;

            if(inputLogs.size() <= currentLogIndex){  // 마지막 입력까지 시뮬레이션 완료함
                return;
            }

            LoopInputLog currentLog = inputLogs.get(currentLogIndex);

            if(currentLog.inputFrame < gameLoop.currentFrame){
                throw new IllegalStateException("currentLog.inputFrame < gameLoop.currentFrame");
            }

            if(currentLog.inputFrame == gameLoop.currentFrame){
                gameLoop.process(currentLog.inputs);
                currentLogIndex++;
            }
            else{
                gameLoop.process();
            }


            currentFrame++;
        }
    }

    @Override
    protected IRUDPPeerListener generateIRUDPPeerListener() {
        return new  IRUDPPeerListener() {
            @Override
            public boolean onConnected(RUDPPeer peer, Connection connection) {
                return false;
            }

            @Override
            public boolean onDisconnected(RUDPPeer peer, Connection connection) {
                if (connection.getAddress().getAddress().getHostAddress().equals(Network.SERVER_IP)) {
                    System.out.println(connection.getAddress().getAddress().getHostAddress() + " disconnected");
                    System.exit(0);
                }
                return true;
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

        String message;
        int messageY = 590;

        message = "● Replay - 좌우 방향키로 배속조절, 엔터로 일시정지";
        g.setColor(Color.red);
        g.drawString(message,5,messageY);

        message = "PlaySpeed : x" + String.valueOf(playSpeed);
        g.setColor(Color.red);
        g.drawString(message,660,messageY);

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

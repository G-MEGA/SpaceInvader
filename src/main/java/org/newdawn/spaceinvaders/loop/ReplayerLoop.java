package org.newdawn.spaceinvaders.loop;

import networking.Network;
import networking.rudp.Connection;
import networking.rudp.IRUDPPeerListener;
import networking.rudp.PacketData.PacketData;
import networking.rudp.RUDPPeer;
import org.newdawn.spaceinvaders.Game;
import org.newdawn.spaceinvaders.loop_input.LoopInput;
import org.newdawn.spaceinvaders.loop_input.LoopInputLog;
import org.newdawn.spaceinvaders.network.LoopRUDPPeerListener;
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

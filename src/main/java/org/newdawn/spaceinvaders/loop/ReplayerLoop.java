package org.newdawn.spaceinvaders.loop;

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

        //TODO 시드 반영
        gameLoop = new GameLoop(game, 37, 1, "");

        String[] splited = replaySaveData.trim().split("\n");

        for(String s: splited){
            inputLogs.add(new LoopInputLog(s));
        }

        currentFrame = gameLoop.currentFrame;
    }

    @Override
    public void process(ArrayList<LoopInput> inputs) {
        super.process(inputs);

        if(isKeyInputJustPressed("escape")) {
            getGame().changeLoop(new MainMenuLoop(getGame()));
        }

        if(isKeyInputJustPressed("right")) {
            playSpeed++;
        }
        if(isKeyInputJustPressed("left")) {
            playSpeed--;
            if(playSpeed<0){
                playSpeed = 0;
            }
        }
        if(isKeyInputJustPressed("accept")) {
            paused = !paused;
        }
        // Replay 다시 재생
        if(isKeyInputJustPressed("record")) {
            getGame().changeLoop(new ReplayerLoop(getGame(), replaySaveData));
            return;
        }

        //TODO Rollback Test
        if (isKeyInputPressed("mouse_button_left")) {
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
        else if(isKeyInputJustPressed("mouse_button_right") && rollbackFrame >= 0 && rollbackSnapshot != null) {
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

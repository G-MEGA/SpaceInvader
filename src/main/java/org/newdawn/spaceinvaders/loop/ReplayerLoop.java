package org.newdawn.spaceinvaders.loop;

import org.newdawn.spaceinvaders.Game;
import org.newdawn.spaceinvaders.game_loop_input.GameLoopInput;
import org.newdawn.spaceinvaders.game_loop_input.GameLoopInputLog;

import java.awt.*;
import java.util.ArrayList;

public class ReplayerLoop extends Loop{
    String replaySaveData;

    long currentFrame;
    int currentLogIndex = 0;

    ArrayList<GameLoopInputLog> inputLogs = new ArrayList<>();
    GameLoop gameLoop;
    int playSpeed = 1;
    boolean paused = false;

    public ReplayerLoop(Game game, String replaySaveData) {
        super(game);

        this.replaySaveData = replaySaveData;

        gameLoop = new GameLoop(game, true);

        String[] splited = replaySaveData.trim().split("\n");

        for(String s: splited){
            inputLogs.add(new GameLoopInputLog(s));
        }

        currentFrame = gameLoop.currentFrame;
    }

    @Override
    public void process(ArrayList<GameLoopInput> inputs) {
        super.process(inputs);

        if(isKeyInputJustPressed("escape")) {
            game.changeLoop(new MainMenuLoop(game));
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
        if(isKeyInputJustPressed("fire")) {
            paused = !paused;
        }
        // Replay 다시 재생
        if(isKeyInputJustPressed("record")) {
            game.changeLoop(new ReplayerLoop(game, replaySaveData));
            return;
        }


        int leftLoop = playSpeed;

        while(!paused && leftLoop>0){
            leftLoop --;

            if(inputLogs.size() <= currentLogIndex){  // 마지막 입력까지 시뮬레이션 완료함
                return;
            }

            GameLoopInputLog currentLog = inputLogs.get(currentLogIndex);

            assert currentLog.inputFrame <= gameLoop.currentFrame;

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
        int messageY = 20;

        message = "● Replay - 좌우 방향키로 배속조절, 스페이스바로 일시정지";
        g.setColor(Color.red);
        g.drawString(message,5,messageY);
        messageY += 15;

        message = "Frame Number : " + String.valueOf(currentFrame);
        g.setColor(Color.red);
        g.drawString(message,5,messageY);
        messageY += 15;

        message = "PlaySpeed : x" + String.valueOf(playSpeed);
        g.setColor(Color.red);
        g.drawString(message,5,messageY);
        messageY += 15;
    }
}

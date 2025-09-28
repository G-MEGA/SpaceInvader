package org.newdawn.spaceinvaders.loop;

import org.newdawn.spaceinvaders.Game;
import org.newdawn.spaceinvaders.enums.GameLoopResultType;
import org.newdawn.spaceinvaders.game_loop_input.GameLoopInput;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class GameLoopPlayerLoop extends Loop{
    GameLoop gameLoop;

    public GameLoopPlayerLoop(Game game, int randomSeed, int playerCount, String rawMapData){
        super(game);
        gameLoop = new GameLoop(game, randomSeed, playerCount, rawMapData);
    }

    @Override
    public void process(ArrayList<GameLoopInput> inputs) {
        super.process(inputs);

//          재시작
//        if (result != GameLoopResultType.InGame && isKeyInputJustPressed("accept")) {
//            GameLoopInputLog lastestLog = inputLogs.get(inputLogs.size()-1);
//            inputLogs.clear();
//
//            currentFrame = 0;
//
//            lastestLog.inputFrame = currentFrame;
//
//            inputLogs.add(lastestLog);
//
//            startGame();
//            result = GameLoopResultType.InGame;
//        }

        // 게임 종료 시
        if(gameLoop.getGameResult() != GameLoopResultType.InGame){
            //메인메뉴로 나가기
            if(isKeyInputJustPressed("escape")) {
                getGame().changeLoop(new MainMenuLoop(getGame()));
            }
            //리플레이 저장하고 메인메뉴로 나가기
            else if(isKeyInputJustPressed("record")) {
                String data = gameLoop.getReplayData();

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

                // 리플레이 저장 즉시 메인메뉴로 사출
                getGame().changeLoop(new MainMenuLoop(getGame()));
            }
        }

        gameLoop.process(inputs);
    }


    @Override
    public void draw(Graphics2D g) {
        gameLoop.draw(g);

        super.draw(g);

        if (gameLoop.getGameResult() != GameLoopResultType.InGame) {
            String message = "";
            if (gameLoop.getGameResult() == GameLoopResultType.Win) {
                message = "Well done! You Win!";
                g.setColor(Color.yellow);
            }
            else if (gameLoop.getGameResult() == GameLoopResultType.Lose) {
                message = "Oh no! They got you...";
                g.setColor(Color.red);
            }
            g.drawString(message,(800-g.getFontMetrics().stringWidth(message))/2,250);

            message = "Press ESC to exit";
            g.setColor(Color.white);
            g.drawString(message,(800-g.getFontMetrics().stringWidth(message))/2,300);
            message = "Press R to save Replay";
            g.setColor(Color.white);
            g.drawString(message,(800-g.getFontMetrics().stringWidth(message))/2,325);
        }

    }
}

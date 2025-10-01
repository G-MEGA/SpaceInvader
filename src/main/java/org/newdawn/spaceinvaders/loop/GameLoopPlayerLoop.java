package org.newdawn.spaceinvaders.loop;

import networking.Network;
import networking.rudp.Connection;
import networking.rudp.IRUDPPeerListener;
import networking.rudp.PacketData.PacketData;
import networking.rudp.RUDPPeer;
import org.newdawn.spaceinvaders.Game;
import org.newdawn.spaceinvaders.enums.GameLoopResultType;
import org.newdawn.spaceinvaders.loop_input.LoopInput;

import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JFileChooser;

import org.newdawn.spaceinvaders.Game;
import org.newdawn.spaceinvaders.enums.GameLoopResultType;
import org.newdawn.spaceinvaders.loop_input.LoopInput;

//플레이어 루프
//	- 진입 후 수신이든 발신이든 모든 통신을 시작하기까지 1초정도 대기시간 둬야할 듯.
//클라: 입력할 때마다 server제외 입력 브로드캐스팅, 게임 끝나면 server에게 결과 전송, 게임 끝나고 나갈 때 server제외 disconnect
//서버:

public class GameLoopPlayerLoop extends Loop{
    GameLoop gameLoop;

    public GameLoopPlayerLoop(Game game){
        super(game);
        //TODO 멀티플레이 정보에 따라서 시드, 플레이어 카운트, 마이 픓레이어 아이디, 맵데이터 넣어줘야함

        //* 테스트로 맵 파일을 불러옴
        Path filePath = Paths.get("src/main/resources/maps/map1.map"); // 파일 경로
        String content = "";
        try {
            content = Files.readString(filePath); // 파일 전체를 String으로 읽음
        } catch (IOException e) {
            e.printStackTrace();
        }
        gameLoop = new GameLoop(game, 37, 4, 0, content);
    }

    @Override
    public void process(ArrayList<LoopInput> inputs) {
        super.process(inputs);

        //TODO 네트워크 플레이 적용할 때 이 메서드 어디 위치시킬지 잘 따져야한다
        getGame().getRudpPeer().processReceivedData();

//          재시작
//        if (result != GameLoopResultType.InGame && isKeyInputJustPressed("accept")) {
//            LoopInputLog lastestLog = inputLogs.get(inputLogs.size()-1);
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
            //TODO 로비로 나가도록 변경하기
            if(isKeyInputJustPressed(0, "escape")) {
                getGame().changeLoop(new MainMenuLoop(getGame()));
            }
            //TODO 로비로 나가도록 변경하기
            else if(isKeyInputJustPressed(0, "record")) {
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
}

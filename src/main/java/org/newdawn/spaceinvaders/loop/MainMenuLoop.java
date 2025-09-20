package org.newdawn.spaceinvaders.loop;

import org.newdawn.spaceinvaders.Game;
import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
import org.newdawn.spaceinvaders.game_loop_input.GameLoopInput;
import org.newdawn.spaceinvaders.game_object.visual.SpriteRenderer;
import org.newdawn.spaceinvaders.sprite.SpriteStore;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class MainMenuLoop extends Loop {
    long t = 0L;
    SpriteRenderer spriteRenderer;

    public MainMenuLoop(Game game) {
        super(game);

        spriteRenderer = new SpriteRenderer(this);
        spriteRenderer.sprite = SpriteStore.get().getSprite("sprites/ship.gif");
        spriteRenderer.setPos(250L << 16, 400 << 16);
        spriteRenderer.setRotation(37 << 16);
        spriteRenderer.setScale(20 << 16);
        gameObjects.add(spriteRenderer);

        SpriteRenderer spriteRenderer2 = new SpriteRenderer(this);
        spriteRenderer2.sprite = SpriteStore.get().getSprite("sprites/ship.gif");
        spriteRenderer2.setPos(400 << 16, 400 << 16);
        gameObjects.add(spriteRenderer2);

    }

    @Override
    public void process(ArrayList<GameLoopInput> inputs) {
        super.process(inputs);

        t += game.fixedDeltaTime;
        spriteRenderer.setRotation(
                FixedPointUtil.fromDouble(
                        Math.toDegrees(
                                Math.sin(FixedPointUtil.toDouble(t) * Math.PI * 2) * Math.PI * 0.125 +
                                        Math.PI*0.25)));

        // 게임 진입
        if(isKeyInputJustPressed("accept")){
            game.changeLoop(new GameLoop(game));
        }

        // 게임 끄기
        if(isKeyInputJustPressed("escape")) {
            System.exit(0);
        }

        // Replay 재생
        if(isKeyInputJustPressed("record")) {
            // 1. JFileChooser로 파일 선택하기
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("텍스트 파일 (*.txt, *.java)", "txt", "java");
            chooser.setFileFilter(filter); // 파일 필터 설정

            int result = chooser.showOpenDialog(game);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = chooser.getSelectedFile();
                Path filePath = selectedFile.toPath();

                // 2. 선택한 파일 내용을 String으로 읽기
                try {
                    // Java 11 이상: 가장 간단한 방법
                    String data = Files.readString(filePath, StandardCharsets.UTF_8);

                    /*
                    // Java 7, 8 환경일 경우: byte 배열로 읽은 후 String으로 변환
                    byte[] fileBytes = Files.readAllBytes(filePath);
                    String content = new String(fileBytes, StandardCharsets.UTF_8);
                    */

                    game.changeLoop(new ReplayerLoop(game, data));

                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(game,
                            "파일을 읽는 중 오류가 발생했습니다: " + ex.getMessage(),
                            "읽기 오류",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

        }
    }

    public void draw(Graphics2D g) {
        super.draw(g);

        Font font = g.getFont();

        String message;
        int messageY = 200;

        g.setFont(new Font(font.getFontName(), Font.BOLD, font.getSize()*10));

        message = "메인 화면";
        g.setColor(Color.white);
        g.drawString(message,(800-g.getFontMetrics().stringWidth(message))/2,messageY);
        messageY += 50;

        g.setFont(new Font(font.getFontName(), Font.BOLD, font.getSize()*2));
        message = "Enter 게임 시작, R 리플레이 재생";
        g.drawString(message,(800-g.getFontMetrics().stringWidth(message))/2,messageY);
    }
}

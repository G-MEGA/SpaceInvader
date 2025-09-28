package org.newdawn.spaceinvaders.loop;

import org.newdawn.spaceinvaders.Game;
import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
import org.newdawn.spaceinvaders.game_loop_input.GameLoopInput;
import org.newdawn.spaceinvaders.game_object.gui.Button;
import org.newdawn.spaceinvaders.game_object.gui.IButtonListener;
import org.newdawn.spaceinvaders.game_object.gui.TextRenderer;
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
        spriteRenderer.setSpriteRef("sprites/ship.gif");
        spriteRenderer.setPos(250L << 16, 400 << 16);
        spriteRenderer.setRotation(37 << 16);
        spriteRenderer.setScale(20 << 16);
        addGameObject(spriteRenderer);

        SpriteRenderer spriteRenderer2 = new SpriteRenderer(this);
        spriteRenderer2.setSpriteRef("sprites/ship.gif");
        spriteRenderer2.setPos(400 << 16, 400 << 16);
        addGameObject(spriteRenderer2);

        TextRenderer titleText = new TextRenderer(this, "우-주- 침략자", 120, Color.WHITE, 1);
        titleText.setPos(400L << 16, 0L << 16);
        titleText.alignment = 1;
        addGameObject(titleText);

        TextRenderer subTitleText = new TextRenderer(this, "~이천만 인민을 위한 김정은 수령의 불꽃같은 특공~", 25, Color.WHITE, 2);
        subTitleText.setPos(400L << 16, titleText.getPosY() + (200L << 16));
        subTitleText.alignment = 1;
        addGameObject(subTitleText);

        // 게임 시작 버튼
        Button startButton = new Button(this, new IButtonListener() {
            @Override
            public void buttonPressed() {
                game.changeLoop(new GameLoopPlayerLoop(game, 37, 1, ""));
            }
        }, 250, 50);
        startButton.setPos(400L << 16, subTitleText.getPosY() + (100L << 16));
        startButton.alignment = 1;
        addGameObject(startButton);
        startButton.addTextRenderer("게임 시작", 25, Color.WHITE, 0);

        // 리플레이 버튼
        Button replayButton = new Button(this, new IButtonListener() {
            @Override
            public void buttonPressed() {
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
        }, 250, 50);
        replayButton.setPos(400L << 16, startButton.getPosY() + (60L << 16));
        replayButton.alignment = 1;
        addGameObject(replayButton);
        replayButton.addTextRenderer("리플레이", 25, Color.WHITE, 0);

        // 나가기 버튼
        Button exitButton = new Button(this, new IButtonListener() {
            @Override
            public void buttonPressed() {
                System.exit(0);
            }
        }, 250, 50);
        exitButton.setPos(400L << 16, replayButton.getPosY() + (60L << 16));
        exitButton.alignment = 1;
        addGameObject(exitButton);
        exitButton.addTextRenderer("나가기", 25, Color.WHITE, 0);
    }

    @Override
    public void process(ArrayList<GameLoopInput> inputs) {
        super.process(inputs);

        t += getGame().fixedDeltaTime;

        spriteRenderer.setRotation(
                FixedPointUtil.fromDouble(
                        Math.toDegrees(
                                Math.sin(FixedPointUtil.toDouble(t) * Math.PI * 2) * Math.PI * 0.125 +
                                        Math.PI*0.25)));

        processGameObjects();
    }
}

package org.newdawn.spaceinvaders.loop.game_loop.game_loop_components;

import java.awt.Color;
import java.util.HashMap;

import org.newdawn.spaceinvaders.enums.IndicatorTextType;
import org.newdawn.spaceinvaders.enums.PlayerPassiveSkillType;
import org.newdawn.spaceinvaders.enums.SectionType;
import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
import org.newdawn.spaceinvaders.game_object.gui.TextRenderer;
import org.newdawn.spaceinvaders.loop.GameLoop;

public class GameLoopTextSystem {
    GameLoop gameLoop;

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public GameLoopTextSystem(){
    }

    public GameLoopTextSystem(GameLoop gameLoop) {
        this.gameLoop = gameLoop;
    }

    private TextRenderer scoreText;
    private TextRenderer coinCountText;
    private TextRenderer playerHealthText;
    private TextRenderer activeSkillText;
    private TextRenderer passiveSkillHeaderText;
    private TextRenderer storeRemainTimeShower;
    private HashMap<PlayerPassiveSkillType, TextRenderer> passiveSkillsTexts;
    private void updatePassiveSkillText(){
        for (PlayerPassiveSkillType type : PlayerPassiveSkillType.values()) {
            TextRenderer text = passiveSkillsTexts.get(type);
            if (text == null){
                text = new TextRenderer(gameLoop, "", 10);
                text.setSortingLayer(101);
                passiveSkillsTexts.put(type, text);

                gameLoop.addGameObject(text);
            }

            text.setText(type.getName() + " : " + gameLoop.playerShipSystem.getPlayerShips().get(gameLoop.playerShipSystem.getMyPlayerID()).getPassiveSkillLevel(type));
        }
    }

    private TextRenderer indicatorText;
    private boolean isIndicatorShown = false;
    private final long indicatorShowTime = 2 << 16;
    private long indicatorShownElapsed = 0;
    public void showIndicatorText(String text) { showIndicatorText(text, IndicatorTextType.DEFAULT);}
    public void showIndicatorText(String text, IndicatorTextType type){ showIndicatorText(text, type.getColor(), type.getFontStyle()); }
    public void showIndicatorText(String text, Color color, int fontStyle){
        indicatorText.setText(text);
        indicatorText.setColor(color);
        indicatorText.setFontStyle(fontStyle);

        isIndicatorShown = true;
    }

    public void initText() {
        //* 좌측 상단 Text 관련 초기화
        passiveSkillsTexts = new HashMap<>();
        
        scoreText = new TextRenderer(gameLoop, "Score : 0", 15);
        scoreText.setFontStyle(1);
        coinCountText = new TextRenderer(gameLoop, "Coin : " + Long.toString(gameLoop.coinSystem.getCoinCount()), 15);
        playerHealthText = new TextRenderer(gameLoop, "Health : " + Long.toString(gameLoop.playerShipSystem.getPlayerShips().get(gameLoop.playerShipSystem.getMyPlayerID()).getHealth()), 15);
        activeSkillText = new TextRenderer(gameLoop, "Active Skill : " + gameLoop.playerShipSystem.getPlayerShips().get(gameLoop.playerShipSystem.getMyPlayerID()).getActiveSkillName(), 15);
        passiveSkillHeaderText = new TextRenderer(gameLoop, "(Passive Skills)", 15);
        indicatorText = new TextRenderer(gameLoop, "", 20);
        indicatorText.alignment = 1;
        storeRemainTimeShower = new TextRenderer(gameLoop, "", 13, Color.yellow);
        storeRemainTimeShower.alignment = 1;

        scoreText.setSortingLayer(100);
        coinCountText.setSortingLayer(100);
        playerHealthText.setSortingLayer(100);
        activeSkillText.setSortingLayer(100);
        passiveSkillHeaderText.setSortingLayer(100);
        indicatorText.setSortingLayer(101);
        storeRemainTimeShower.setSortingLayer(100);
        
        gameLoop.addGameObject(scoreText);
        gameLoop.addGameObject(coinCountText);
        gameLoop.addGameObject(playerHealthText);
        gameLoop.addGameObject(activeSkillText);
        gameLoop.addGameObject(passiveSkillHeaderText);
        gameLoop.addGameObject(indicatorText);
        gameLoop.addGameObject(storeRemainTimeShower);
        
        updatePassiveSkillText();
        
        scoreText.setPos(0, 0);
        coinCountText.setPos(0 , 15 << 16);
        playerHealthText.setPos(0, 35 << 16);
        activeSkillText.setPos(0, 55 << 16);
        passiveSkillHeaderText.setPos(0, 75 << 16);
        indicatorText.setPos(400 << 16, 50 << 16);
        storeRemainTimeShower.setPos(400 << 16, 30 << 16);
        int index = 0;
        for (TextRenderer text : passiveSkillsTexts.values()) {
            text.setPos(0, (95 + index++ * 10) << 16);
        }
        
    }

    public void updateText() {
        scoreText.setText("Score : " + gameLoop.scoreSystem.getScore());
        coinCountText.setText("Coin : " + Long.toString(gameLoop.coinSystem.getCoinCount()));
        playerHealthText.setText("Health : " + Long.toString(gameLoop.playerShipSystem.getPlayerShips().get(gameLoop.playerShipSystem.getMyPlayerID()).getHealth()) +
        (gameLoop.playerShipSystem.getPlayerShips().get(gameLoop.playerShipSystem.getMyPlayerID()).getCurrentShield() == 0 ? "" : " ( " + Integer.toString(gameLoop.playerShipSystem.getPlayerShips().get(gameLoop.playerShipSystem.getMyPlayerID()).getCurrentShield())  + " ) "));

        String activeSkillTextContent = "Active Skill : " + gameLoop.playerShipSystem.getPlayerShips().get(gameLoop.playerShipSystem.getMyPlayerID()).getActiveSkillName();
        activeSkillTextContent += gameLoop.playerShipSystem.getPlayerShips().get(gameLoop.playerShipSystem.getMyPlayerID()).isActiveSkillActable() ? "" : "( " + Long.toString(gameLoop.playerShipSystem.getPlayerShips().get(gameLoop.playerShipSystem.getMyPlayerID()).getRemainCoolTime() >> 16) + " )";
        activeSkillText.setText(activeSkillTextContent);

        if (gameLoop.mapLoader.getCurrentSection() != null && gameLoop.mapLoader.getCurrentSection().getSectionType() == SectionType.STORE){
            storeRemainTimeShower.setText("Remain Store Time : " + Integer.toString(FixedPointUtil.toInt(FixedPointUtil.sub(MapLoader.STORE_SECTION_DURATION, gameLoop.mapLoader.getSectionElapsed()))));
        }
        else{
            storeRemainTimeShower.setText("");
        }


        updatePassiveSkillText();
    }

    public void updateIndicatorText(long deltaTime) {
        if (isIndicatorShown){
            if (indicatorShownElapsed > indicatorShowTime){
                isIndicatorShown = false;
                indicatorShownElapsed = 0;
                indicatorText.setText("");
            }
            else { indicatorShownElapsed += deltaTime; } //? 흠 deltaTime 아니긴 한데 괜찮겠지?
        }
    }
}

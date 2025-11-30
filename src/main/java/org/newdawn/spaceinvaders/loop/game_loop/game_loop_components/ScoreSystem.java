package org.newdawn.spaceinvaders.loop.game_loop.game_loop_components;

import org.newdawn.spaceinvaders.enums.GameLoopResultType;
import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
import org.newdawn.spaceinvaders.loop.GameLoop;


public class ScoreSystem {
    private GameLoop gameLoop;
    
    public static final long SCORING_TIME_INTERVAL = FixedPointUtil.ZERO_01;
    public static final int SCORING_SCORE = 1;
    private long scoredTimeElapsed = 0;

    private int score = 0;
    public int getScore() { return score; }
    public void initScore() { score = 0; }
    public void increaseScore() {increaseScore(100); }
    public void increaseScore(int amount) {
        if(gameLoop.gameResult != GameLoopResultType.IN_GAME)return;
        score += amount;
    }
    
    public ScoreSystem(GameLoop gameLoop) {
        this.gameLoop = gameLoop;
    }

    public ScoreSystem(){
        // Kryo 역직렬화를 위한 매개변수 없는 생성자
    }
    
    public void requestTimeScoring(GameLoopResultType gameResult, long deltaTime){
        if (gameResult ==  GameLoopResultType.IN_GAME){
            if (scoredTimeElapsed >= SCORING_TIME_INTERVAL){
                increaseScore(SCORING_SCORE);
                scoredTimeElapsed = 0;
            }
            else{
                scoredTimeElapsed += deltaTime;
            }
        }
    }
}

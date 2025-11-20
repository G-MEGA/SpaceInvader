package org.newdawn.spaceinvaders.game_object.ingame.player_skill;

import org.newdawn.spaceinvaders.enums.IndicatorTextType;
import org.newdawn.spaceinvaders.enums.PlayerPassiveSkillType;
import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
import org.newdawn.spaceinvaders.game_object.ingame.player.PlayerShip;
import org.newdawn.spaceinvaders.loop.GameLoop;

public abstract class PassiveSkill{
    private PlayerPassiveSkillType type;
    public PlayerPassiveSkillType getType() { return type; }
    private int level = 0;
    private int maxLevel;
    protected PlayerShip playerShip;

    public int getLevel() {
        return level;
    }

    public void setPassiveSkillLevel(int i){
      if((i>0)&&(i<=maxLevel)){
        level = i;
        activate(level);
      }
    }

    public void upgradePassiveSkill(){
        setPassiveSkillLevel(level+1);
    }

    public boolean isPasiveSkillMaxLevel(){
        return level == maxLevel;
    }

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public PassiveSkill(){
        super();
    }
    public PassiveSkill(PlayerShip playerShip,int maxLevel) {
        this.playerShip = playerShip;
        this.maxLevel = maxLevel;
    }


    public abstract void activate(int level); 
    

}
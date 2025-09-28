package org.newdawn.spaceinvaders.game_object.ingame.player_skill.active_skill;

import org.newdawn.spaceinvaders.game_object.ingame.PlayerShip;
import org.newdawn.spaceinvaders.game_object.ingame.player_skill.PlayerSkill;
import org.newdawn.spaceinvaders.loop.GameLoop;

public abstract class ActiveSkill extends PlayerSkill{
    private long coolTime;
    public long getCoolTime() { return coolTime; }

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public ActiveSkill(){
        super();
    }
    public ActiveSkill(String skillName, String skillSpriteRef, long coolTime, GameLoop gameLoop) {
        super(skillName, skillSpriteRef, gameLoop);
        this.coolTime = coolTime;
    }

    @Override
    public boolean onAcquire(GameLoop gameLoop, PlayerShip playerShip) {
        playerShip.setActiveSkill(this);
        this.playerShip = playerShip;
        return true;
    }
    
    public abstract void activate();
}

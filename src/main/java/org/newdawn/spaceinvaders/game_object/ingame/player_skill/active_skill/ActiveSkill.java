package org.newdawn.spaceinvaders.game_object.ingame.player_skill.active_skill;

import org.newdawn.spaceinvaders.game_object.ingame.PlayerShip;
import org.newdawn.spaceinvaders.game_object.ingame.player_skill.PlayerSkill;
import org.newdawn.spaceinvaders.loop.GameLoop;

public abstract class ActiveSkill extends PlayerSkill{
    private long coolTime;
    public long getCoolTime() { return coolTime; }

    public ActiveSkill(String skillName, String skillSpriteRef, long coolTime, PlayerShip playerShip, GameLoop gameLoop) {
        super(skillName, skillSpriteRef, playerShip, gameLoop);
        this.coolTime = coolTime;
    }

    @Override
    public boolean onAcquire(GameLoop gameLoop) {
        playerShip.setActiveSkill(this);

        return true;
    }
    
    public abstract void activate();
}

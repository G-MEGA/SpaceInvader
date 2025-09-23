package org.newdawn.spaceinvaders.game_object.player_skill.active_skill;

import org.newdawn.spaceinvaders.game_object.player_skill.PlayerSkill;
import org.newdawn.spaceinvaders.loop.GameLoop;

public abstract class ActiveSkill extends PlayerSkill{
    public ActiveSkill(String skillName, GameLoop gameLoop) {
        super(skillName, gameLoop);
    }
}

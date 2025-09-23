package org.newdawn.spaceinvaders.game_object.ingame.player_skill.active_skill;

import org.newdawn.spaceinvaders.game_object.ingame.player_skill.PlayerSkill;
import org.newdawn.spaceinvaders.loop.GameLoop;

public abstract class ActiveSkill extends PlayerSkill{
    public ActiveSkill(String skillName, String skillSpriteRef, GameLoop gameLoop) {
        super(skillName, skillSpriteRef, gameLoop);
    }
}

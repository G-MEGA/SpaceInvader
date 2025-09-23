package org.newdawn.spaceinvaders.game_object.ingame.player_skill.active_skill;

import org.newdawn.spaceinvaders.loop.GameLoop;

public class BasicActiveSkill extends ActiveSkill{
    private static final String skillSpriteRef = "sprites/testActiveSkill.png";
    private static final String skillName = "Test Active Skill";

    public BasicActiveSkill(GameLoop gameLoop) {
        super(skillName, skillSpriteRef, gameLoop);
    }

    @Override
    public void acquire(GameLoop gameLoop) {
        System.out.println("active : " + getName() + ", 아이템 먹음");
    }
}

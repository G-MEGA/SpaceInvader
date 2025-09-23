package org.newdawn.spaceinvaders.game_object.ingame.player_skill.active_skill;

import org.newdawn.spaceinvaders.game_object.ingame.PlayerShip;
import org.newdawn.spaceinvaders.loop.GameLoop;

public class LaserSkill extends ActiveSkill{
    private static final String skillSpriteRef = "sprites/testActiveSkill.png";
    private static final String skillName = "Laser Skill";

    public LaserSkill(PlayerShip playerShip, GameLoop gameLoop) {
        super(skillName, skillSpriteRef, playerShip, gameLoop);
    }

    @Override
    public void activate() {
        System.out.println("레이저 뿌쓩빵쌍");
    }
    
}

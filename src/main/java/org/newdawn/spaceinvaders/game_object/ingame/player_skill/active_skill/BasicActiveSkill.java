package org.newdawn.spaceinvaders.game_object.ingame.player_skill.active_skill;

import org.newdawn.spaceinvaders.game_object.ingame.PlayerShip;
import org.newdawn.spaceinvaders.loop.GameLoop;

public class BasicActiveSkill extends ActiveSkill{
    private static final String skillSpriteRef = "sprites/testActiveSkill.png";
    private static final String skillName = "Test Active Skill";

    public BasicActiveSkill(PlayerShip playerShip, GameLoop gameLoop) {
        super(skillName, skillSpriteRef, playerShip, gameLoop);
        //TODO Auto-generated constructor stub
    }
    
    

    @Override
    public void activate() {
        System.out.println(skillName + " 발동");
    }
}

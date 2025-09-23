package org.newdawn.spaceinvaders.game_object.ingame.player_skill.passive_skill;

import org.newdawn.spaceinvaders.game_object.ingame.player_skill.PlayerSkill;
import org.newdawn.spaceinvaders.loop.GameLoop;

public class PassiveSkill extends PlayerSkill{
    private String type; //? 아마 enum이나 static으로 바꿔야할려냐..??

    public PassiveSkill(String skillName, String skillSpriteRef, GameLoop gameLoop, String type) {
        super(skillName, skillSpriteRef, gameLoop);
        this.type = type;
    }

    @Override
    public void acquire(GameLoop playerShip) {
        //playerShip.plusPassiveSkill(type);
        System.out.println("passive : " + type + ", 아이템 먹음");
    }
}

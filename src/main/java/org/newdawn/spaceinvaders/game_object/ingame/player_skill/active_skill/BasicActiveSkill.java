package org.newdawn.spaceinvaders.game_object.ingame.player_skill.active_skill;

import org.newdawn.spaceinvaders.game_object.ingame.PlayerShip;
import org.newdawn.spaceinvaders.loop.GameLoop;

public class BasicActiveSkill extends ActiveSkill{
    private static final String skillSpriteRef = "sprites/testActiveSkill.png";
    private static final String skillName = "Test Active Skill";
    private static final long coolTime = 5 << 16;

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public BasicActiveSkill(){
        super();
    }
    public BasicActiveSkill(GameLoop gameLoop) {
        super(skillName, skillSpriteRef, coolTime, gameLoop);
    }
    
    @Override
    public void activate() {
        System.out.println(skillName + " 발동");
    }
}

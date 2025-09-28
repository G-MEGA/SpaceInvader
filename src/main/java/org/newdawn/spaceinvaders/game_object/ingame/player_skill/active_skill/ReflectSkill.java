package org.newdawn.spaceinvaders.game_object.ingame.player_skill.active_skill;

import org.newdawn.spaceinvaders.game_object.ingame.PlayerShip;
import org.newdawn.spaceinvaders.loop.GameLoop;

public class ReflectSkill extends ActiveSkill{
    private static final String skillSpriteRef = "sprites/testActiveSkill.png";
    private static final String skillName = "Reflect Skill";
    private static final long coolTime = 10 << 16;

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public ReflectSkill(){
        super();
    }
    public ReflectSkill(GameLoop gameLoop) {
        super(skillName, skillSpriteRef, coolTime, gameLoop);
    }

    @Override
    public void activate() {
        playerShip.notifyReflectionionEvent();
    }
}

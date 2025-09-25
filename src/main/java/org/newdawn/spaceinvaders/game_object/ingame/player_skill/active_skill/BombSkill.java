package org.newdawn.spaceinvaders.game_object.ingame.player_skill.active_skill;

import org.newdawn.spaceinvaders.game_object.ingame.PlayerShip;
import org.newdawn.spaceinvaders.loop.GameLoop;

public class BombSkill extends ActiveSkill {
    private static final String skillSpriteRef = "sprites/testActiveSkill.png";
    private static final String skillName = "Bomb Skill";
    private static final long coolTime = 5 << 16;

    public BombSkill(PlayerShip playerShip, GameLoop gameLoop) {
        super(skillName, skillSpriteRef, coolTime, playerShip, gameLoop);
    }

    @Override
    public void activate() {
        gameLoop.notifyBomb();
    }
}

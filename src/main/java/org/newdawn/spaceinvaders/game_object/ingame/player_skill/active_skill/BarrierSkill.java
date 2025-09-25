package org.newdawn.spaceinvaders.game_object.ingame.player_skill.active_skill;

import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
import org.newdawn.spaceinvaders.game_object.ingame.Barrier;
import org.newdawn.spaceinvaders.game_object.ingame.PlayerShip;
import org.newdawn.spaceinvaders.loop.GameLoop;

public class BarrierSkill extends ActiveSkill{
    private static final String skillSpriteRef = "sprites/testActiveSkill.png";
    private static final String skillName = "Barier Skill";
    private static final long coolTime = 5 << 16;

    public BarrierSkill(PlayerShip playerShip, GameLoop gameLoop) {
        super(skillName, skillSpriteRef, coolTime, playerShip, gameLoop);
    }

    @Override
    public void activate() {
        long spawnAngle = playerShip.getRotation();
        long spawnX = playerShip.getPosX();
        long spawnY = playerShip.getPosY();
        Barrier barrier = new Barrier(gameLoop, spawnX, spawnY, 30L<< 16, spawnAngle);
        gameLoop.addGameObject(barrier);
        System.out.println("배리어 소환~");
    }
}

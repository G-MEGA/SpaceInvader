package org.newdawn.spaceinvaders.game_object.ingame.player_skill.active_skill;

import org.newdawn.spaceinvaders.game_object.ingame.Laser;
import org.newdawn.spaceinvaders.game_object.ingame.PlayerShip;
import org.newdawn.spaceinvaders.loop.GameLoop;
import org.newdawn.spaceinvaders.loop.Loop;

public class LaserSkill extends ActiveSkill{
    private static final String skillSpriteRef = "sprites/testActiveSkill.png";
    private static final String skillName = "Laser Skill";    
    private static final long coolTime = 5 << 16;

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public LaserSkill(){
        super();
    }
    public LaserSkill(PlayerShip playerShip, GameLoop gameLoop) {
        super(skillName, skillSpriteRef, coolTime, playerShip, gameLoop);
    }

    @Override
    public void activate() {
        long spawnPosX = playerShip.getPosX();
        long spawnPosY = playerShip.getPosY();
        long spawnAngle = playerShip.getRotation();
        Laser laser = new Laser(gameLoop, spawnPosX, spawnPosY, 20L, spawnAngle);
        gameLoop.addGameObject(laser);
    }
}

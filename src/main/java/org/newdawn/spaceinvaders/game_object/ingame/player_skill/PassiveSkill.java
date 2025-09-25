package org.newdawn.spaceinvaders.game_object.ingame.player_skill;

import org.newdawn.spaceinvaders.enums.IndicatorTextType;
import org.newdawn.spaceinvaders.enums.PlayerPassiveSkillType;
import org.newdawn.spaceinvaders.game_object.ingame.PlayerShip;
import org.newdawn.spaceinvaders.loop.GameLoop;

public class PassiveSkill extends PlayerSkill{
    private PlayerPassiveSkillType type;

    public PassiveSkill(PlayerPassiveSkillType type, PlayerShip playerShip, GameLoop gameLoop) {
        super(type.getName(), type.getSpriteRef(), playerShip, gameLoop);

        this.type = type;
    }

    @Override
    public boolean onAcquire(GameLoop gameLoop) {
        if (playerShip.isSkillMaxLevel(type)) {
            gameLoop.showIndicatorText("'" + type.name() + "'" + " 패시브 스킬은 현재 최대 레벨 입니다.", IndicatorTextType.Warning);
            return false;
        }
        playerShip.upgradePassiveSkill(type);
        return true;
    }
}

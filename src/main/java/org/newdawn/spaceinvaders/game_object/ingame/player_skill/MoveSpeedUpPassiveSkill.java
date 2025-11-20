package org.newdawn.spaceinvaders.game_object.ingame.player_skill;

import org.newdawn.spaceinvaders.enums.PlayerPassiveSkillType;
import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
import org.newdawn.spaceinvaders.game_object.ingame.player.PlayerShip;
import org.newdawn.spaceinvaders.loop.GameLoop;

public class MoveSpeedUpPassiveSkill extends PassiveSkill {

     public static final int MAX_LEVEL = 3;

    public MoveSpeedUpPassiveSkill(){
        super();
    }

    public MoveSpeedUpPassiveSkill(PlayerShip playerShip) {
        super(playerShip,MAX_LEVEL);
    }

    @Override
    public void activate(int level) {
      long moveSpeedMultiplier = 1 << 16;
        switch (level) {
            case 1:
                moveSpeedMultiplier = (1 << 16) + FixedPointUtil.ZERO_15;
                break;
            case 2:
                moveSpeedMultiplier = (1 << 16) + FixedPointUtil.ZERO_3;
                break;
            case 3:
                moveSpeedMultiplier = (1 << 16) + FixedPointUtil.ZERO_5;
                break;
            default:
                moveSpeedMultiplier = 1 << 16;
                break;
        }
        playerShip.setMoveSpeed(FixedPointUtil.mul(playerShip.getDefaultMoveSpeed(), moveSpeedMultiplier));
    }


}

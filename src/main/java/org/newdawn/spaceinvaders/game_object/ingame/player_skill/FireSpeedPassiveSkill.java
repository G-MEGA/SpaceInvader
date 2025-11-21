package org.newdawn.spaceinvaders.game_object.ingame.player_skill;
import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
import org.newdawn.spaceinvaders.game_object.ingame.player.PlayerShip;

public class FireSpeedPassiveSkill extends PassiveSkill {

     public static final int MAX_LEVEL = 3;

    public FireSpeedPassiveSkill(){
        super();
    }

    public FireSpeedPassiveSkill(PlayerShip playerShip) {
        super(playerShip,MAX_LEVEL);
    }

    @Override
    public void activate(int level) {
     long fireSpeedMultiplier;
        switch (level) {
                case 1:
                    fireSpeedMultiplier = (1 << 16) + FixedPointUtil.ZERO_3;
                    break;
                case 2:
                    fireSpeedMultiplier = (1 << 16) + FixedPointUtil.ZERO_8;
                    break;
                case 3:
                    fireSpeedMultiplier = (2 << 16) + FixedPointUtil.ZERO_5;
                    break;
                default:
                    fireSpeedMultiplier = 1 << 16;
                    break;
            }
            playerShip.setFiringInterval(FixedPointUtil.div(playerShip.getDefaultFiringIntreval(), fireSpeedMultiplier));
    }

    
}

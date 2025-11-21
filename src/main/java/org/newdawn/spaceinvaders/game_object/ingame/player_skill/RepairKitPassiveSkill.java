package org.newdawn.spaceinvaders.game_object.ingame.player_skill;
import org.newdawn.spaceinvaders.game_object.ingame.player.PlayerShip;

public class RepairKitPassiveSkill extends PassiveSkill {

     public static final int MAX_LEVEL = 3;

    public RepairKitPassiveSkill(){
        super();
    }

    public RepairKitPassiveSkill(PlayerShip playerShip) {
        super(playerShip,MAX_LEVEL);
    }

    @Override
    public void activate(int level) {
             int initialShield = 1;
        switch (level) {
            case 1:
                initialShield = 1;
                break;
            case 2:
                initialShield = 1;
                break;
            case 3:
                initialShield = 2;
                break;
            default:
                initialShield = 1;
                break;
        }
        playerShip.setWaveInitialShield(initialShield);
    }

}

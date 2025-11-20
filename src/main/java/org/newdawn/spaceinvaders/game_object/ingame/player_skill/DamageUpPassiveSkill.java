package org.newdawn.spaceinvaders.game_object.ingame.player_skill;
import org.newdawn.spaceinvaders.game_object.ingame.player.PlayerShip;

public class DamageUpPassiveSkill extends PassiveSkill {

    public static final int MAX_LEVEL = 5;

    public DamageUpPassiveSkill(){
        super();
    }

    public DamageUpPassiveSkill(PlayerShip playerShip) {
        super(playerShip,MAX_LEVEL);
    }

    @Override
    public void activate(int level) {
        int newBulletDamage = 1;
        switch (level) {
            case 1:
                newBulletDamage = 2; 
                break;
            case 2:
                newBulletDamage = 3; 
                break;
            case 3:
                newBulletDamage = 4; 
                break;
            case 4:
                newBulletDamage = 5; 
                break;
            case 5:
                newBulletDamage = 6; 
                break;
            default:
                newBulletDamage = 1;
                break;
        }
        playerShip.setBulletDamage(newBulletDamage);
    }
    
}

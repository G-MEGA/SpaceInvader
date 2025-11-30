package org.newdawn.spaceinvaders.game_object.ingame.player_skill;

import org.newdawn.spaceinvaders.enums.IndicatorTextType;
import org.newdawn.spaceinvaders.enums.PlayerPassiveSkillType;
import org.newdawn.spaceinvaders.game_object.ingame.player.PlayerShip;
import org.newdawn.spaceinvaders.game_object.ingame.store.IStoreItem;
import org.newdawn.spaceinvaders.loop.GameLoop;

public class StorePassiveItem implements IStoreItem{

    private PlayerPassiveSkillType type;

    public StorePassiveItem(){
        // Kryo 역직렬화를 위한 매개변수 없는 생성자
    }
    public StorePassiveItem(PlayerPassiveSkillType type){
        this.type = type;
    }

    @Override
    public String getName() {
        return type.getName();
    }

    @Override
    public String getSpriteRef() {
        return type.getSpriteRef();
    }

    @Override
    public boolean onAcquire(GameLoop gameLoop, PlayerShip playerShip) {
         if (playerShip.isPasiveSkillMaxLevel(type)) {
            gameLoop.showIndicatorText("'" + type.name() + "'" + " 패시브 스킬은 현재 최대 레벨 입니다.", IndicatorTextType.WARNING);
            return false;
        }
        playerShip.upgradePassiveSkill(type);
        gameLoop.notifySkillStoreItemAcquired();
        return true;
    }

    @Override
    public int getPrice(PlayerShip playerShip) {
        return type.getPrice();
    }

    @Override
    public String getPriceString(PlayerShip playerShip) {
       if (playerShip.isPasiveSkillMaxLevel(type)){
            return "?";
        }
        else{
            return Integer.toString(getPrice(playerShip));
        }
    }
    
}

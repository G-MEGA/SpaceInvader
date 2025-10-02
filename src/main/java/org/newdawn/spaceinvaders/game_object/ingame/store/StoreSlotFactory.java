package org.newdawn.spaceinvaders.game_object.ingame.store;

import org.newdawn.spaceinvaders.enums.PlayerPassiveSkillType;
import org.newdawn.spaceinvaders.game_object.ingame.player.PlayerShip;
import org.newdawn.spaceinvaders.game_object.ingame.player_skill.PassiveSkill;
import org.newdawn.spaceinvaders.game_object.ingame.player_skill.active_skill.ActiveSkill;
import org.newdawn.spaceinvaders.game_object.ingame.player_skill.active_skill.BarrierSkill;
import org.newdawn.spaceinvaders.game_object.ingame.player_skill.active_skill.BombSkill;
import org.newdawn.spaceinvaders.game_object.ingame.player_skill.active_skill.LaserSkill;
import org.newdawn.spaceinvaders.game_object.ingame.player_skill.active_skill.ReflectSkill;
import org.newdawn.spaceinvaders.loop.GameLoop;

public class StoreSlotFactory {
    private GameLoop gameLoop;

    //* passive skill ID
    public static final int PS_FIRE_SPEED = 0;
    public static final int PS_DAMAGE_UP = 1;
    public static final int PS_ADDITIONAL_ENGINE = 2;
    public static final int PS_REPAIR_KIT = 3;
    
    //* active skill ID
    public static final int AS_BARRIER = 0;
    public static final int AS_BOMB = 1;
    public static final int AS_LASER = 2;
    public static final int AS_REFLECT = 3;

    public StoreSlotFactory(GameLoop gameLoop) {
        this.gameLoop = gameLoop;
    }

    public void createPassiveSkillItemSlot(int skillId, long spawnPosX, long spawnPosY, PlayerShip playerShip){
        PassiveSkill passiveSkill = null;
        Integer skillLevel = 0;
        //TODO 스킬 레벨에 따른 가격 변동 추가
        switch (skillId) {
            case PS_FIRE_SPEED:
                passiveSkill = new PassiveSkill(PlayerPassiveSkillType.FireSpeed, gameLoop);
                skillLevel = playerShip.getPassiveSkillLevel(PlayerPassiveSkillType.FireSpeed);
                break;
            case PS_DAMAGE_UP:
                passiveSkill = new PassiveSkill(PlayerPassiveSkillType.DamageUp, gameLoop);
                skillLevel = playerShip.getPassiveSkillLevel(PlayerPassiveSkillType.DamageUp);
                break;
            case PS_ADDITIONAL_ENGINE:
                passiveSkill = new PassiveSkill(PlayerPassiveSkillType.AdditionalEngine, gameLoop);
                skillLevel = playerShip.getPassiveSkillLevel(PlayerPassiveSkillType.AdditionalEngine);
                break;
            case PS_REPAIR_KIT:
                passiveSkill = new PassiveSkill(PlayerPassiveSkillType.RepairKit, gameLoop);
                skillLevel = playerShip.getPassiveSkillLevel(PlayerPassiveSkillType.RepairKit);
                break;
        }
        if (passiveSkill == null){
            System.err.println(skillId + "은 존재하지 않은 passiveSkill ID 입니다.");
            return;
        }

        StoreSlot storeSlot = new StoreSlot(gameLoop, skillLevel.intValue() + 1, passiveSkill, spawnPosX, spawnPosY);
        gameLoop.addStoreSlot(storeSlot);
    }

    public void setPassiveSkillItemPrice(StoreSlot storeSlot, PlayerPassiveSkillType type, PlayerShip playerShip) {
        if (playerShip.isPasiveSkillMaxLevel(type)){
            storeSlot.setPriceUnknown(true);
        }
        else{
            storeSlot.setPrice(playerShip.getPassiveSkillLevel(type) + 1);
        }
    }
    
    public void createActiveSkillItemSlot(int skillId, long spawnPosX, long spawnPosY){
        ActiveSkill activeSkill = null;
        switch (skillId) {
            case AS_BARRIER:
                activeSkill = new BarrierSkill(gameLoop);
                break;
            case AS_BOMB:
                activeSkill = new BombSkill(gameLoop);
                break;
            case AS_LASER:
                activeSkill = new LaserSkill(gameLoop);
                break;
            case AS_REFLECT:
                activeSkill = new ReflectSkill(gameLoop);
                break;
        }
        if (activeSkill == null){
            System.err.println(skillId + "은 존재하지 않은 activeSkill ID 입니다.");
            return;
        }

        StoreSlot storeSlot = new StoreSlot(gameLoop, 3, activeSkill, spawnPosX, spawnPosY);
        gameLoop.addStoreSlot(storeSlot);
    }

}

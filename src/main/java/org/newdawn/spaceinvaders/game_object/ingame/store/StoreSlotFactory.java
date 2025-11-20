package org.newdawn.spaceinvaders.game_object.ingame.store;

import org.newdawn.spaceinvaders.PositionAngleSet;
import org.newdawn.spaceinvaders.enums.PlayerPassiveSkillType;
import org.newdawn.spaceinvaders.game_object.ingame.enemy.SpawnSignal;
import org.newdawn.spaceinvaders.game_object.ingame.player_skill.StorePassiveItem;
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

    public StoreSlotFactory(){}
    public StoreSlotFactory(GameLoop gameLoop) {
        this.gameLoop = gameLoop;
    }

    public void createPassiveSkillItemSlot(int skillId, PositionAngleSet positionAngleSet){
        StorePassiveItem passiveSkill = null;
        PassiveSkill passiveSkill = generatePassiveSkill(skillId);

        if (passiveSkill != null) {
            spawnToGameLoop(passiveSkill, pos);
        switch (skillId) {
            case PS_FIRE_SPEED:
                passiveSkill = new StorePassiveItem(PlayerPassiveSkillType.FIRE_SPEED);
                break;
            case PS_DAMAGE_UP:
                passiveSkill = new StorePassiveItem(PlayerPassiveSkillType.DAMAGE_UP);
                break;
            case PS_ADDITIONAL_ENGINE:
                passiveSkill = new StorePassiveItem(PlayerPassiveSkillType.ADDITIONAL_ENGINE);
                break;
            case PS_REPAIR_KIT:
                passiveSkill = new StorePassiveItem(PlayerPassiveSkillType.REPAIR_KIT);
                break;
            default:
                System.err.println(skillId + "은 존재하지 않은 passiveSkill ID 입니다.");
                return;
        }
    }

    // [Refactoring] 메인 메서드는 흐름만 제어 (복잡도 2)
    public void createActiveSkillItemSlot(int skillId, PositionAngleSet pos){
        ActiveSkill activeSkill = generateActiveSkill(skillId);

        if (activeSkill != null) {
            spawnToGameLoop(activeSkill, pos);
        }
    }

    // [Extract] 공통된 스폰 로직 분리 (복잡도 1: 분기 없음)
    private void spawnToGameLoop(IStoreItem skillItem, PositionAngleSet pos) {
        StoreSlot storeSlot = new StoreSlot(gameLoop, skillItem, pos.positionX, pos.positionY);
        SpawnSignal spawnSignal = new SpawnSignal(storeSlot, gameLoop, pos, SpawnSignal.STORE_ITEM_SIGNAL);
        gameLoop.addGameObject(spawnSignal);
    }

    // [Extract] 스위치 문 분리 (복잡도 4~5: 스위치 문 집중)
    // Java 14+를 쓴다면 Switch Expression을 써서 더 줄일 수 있습니다.
    private PassiveSkill generatePassiveSkill(int skillId) {
        PassiveSkill passiveSkill = null;
        switch (skillId) {
            case PS_FIRE_SPEED:      passiveSkill = new PassiveSkill(PlayerPassiveSkillType.FIRE_SPEED, gameLoop); break;
            case PS_DAMAGE_UP:       passiveSkill = new PassiveSkill(PlayerPassiveSkillType.DAMAGE_UP, gameLoop); break;
            case PS_ADDITIONAL_ENGINE: passiveSkill = new PassiveSkill(PlayerPassiveSkillType.ADDITIONAL_ENGINE, gameLoop); break;
            case PS_REPAIR_KIT:      passiveSkill = new PassiveSkill(PlayerPassiveSkillType.REPAIR_KIT, gameLoop); break;
            default:
                System.err.println(skillId + "은 존재하지 않은 passiveSkill ID 입니다.");
                passiveSkill = null;
                break;
        }
        return passiveSkill;
    }

    // [Extract] 스위치 문 분리 (복잡도 4~5)
    private ActiveSkill generateActiveSkill(int skillId) {
        ActiveSkill activeSkill = null;
        switch (skillId) {
            case AS_BARRIER: activeSkill = new BarrierSkill(gameLoop); break;
            case AS_BOMB:    activeSkill = new BombSkill(gameLoop); break;
            case AS_LASER:   activeSkill = new LaserSkill(gameLoop); break;
            case AS_REFLECT: activeSkill = new ReflectSkill(gameLoop); break;
            default:
                System.err.println(skillId + "은 존재하지 않은 activeSkill ID 입니다.");
                activeSkill = null;
                break;
        }
        return activeSkill;
    }
}
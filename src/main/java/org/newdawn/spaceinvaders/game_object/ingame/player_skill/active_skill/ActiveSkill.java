package org.newdawn.spaceinvaders.game_object.ingame.player_skill.active_skill;

import org.newdawn.spaceinvaders.game_object.ingame.player.PlayerShip;
import org.newdawn.spaceinvaders.game_object.ingame.store.IStoreItem;
import org.newdawn.spaceinvaders.loop.GameLoop;

public abstract class ActiveSkill implements IStoreItem{
    private long coolTime;
    private String skillName;
    private String skillSpriteRef;
    protected GameLoop gameLoop;
    public long getCoolTime() { return coolTime; }

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public ActiveSkill(){
        super();
    }
    public ActiveSkill(String skillName, String skillSpriteRef, long coolTime, GameLoop gameLoop) {
        this.skillName = skillName;
        this.skillSpriteRef = skillSpriteRef;
        this.coolTime = coolTime;
        this.gameLoop = gameLoop;

    }

    @Override
    public boolean onAcquire(GameLoop gameLoop, PlayerShip playerShip) {
        playerShip.setActiveSkill(this);
        gameLoop.notifySkillStoreItemAcquired();
        return true;
    }
    
    public abstract void activate(PlayerShip playerShip);

    public int getPrice(PlayerShip playerShip){
        return 3;
    }
    public String getPriceString(PlayerShip playerShip){
        return Integer.toString(getPrice(playerShip));
    }

    @Override
    public String getName() {
        return skillName;
    }

    @Override
    public String getSpriteRef() {
        return skillSpriteRef;
    }
}

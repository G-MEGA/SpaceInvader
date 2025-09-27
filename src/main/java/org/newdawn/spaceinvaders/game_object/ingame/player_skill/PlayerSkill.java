package org.newdawn.spaceinvaders.game_object.ingame.player_skill;

import org.newdawn.spaceinvaders.game_object.ingame.PlayerShip;
import org.newdawn.spaceinvaders.game_object.ingame.store.IStoreItem;
import org.newdawn.spaceinvaders.game_object.visual.SpriteRenderer;
import org.newdawn.spaceinvaders.loop.GameLoop;
import org.newdawn.spaceinvaders.sprite.Sprite;



public abstract class PlayerSkill implements IStoreItem {
    protected String skillName;
    protected String skillSpriteRef;
    protected PlayerShip playerShip;
    protected GameLoop gameLoop;

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public  PlayerSkill(){
        super();
    }
    public PlayerSkill(String skillName, String skillSpriteRef, PlayerShip playerShip, GameLoop gameLoop) {
        this.skillName = skillName;
        this.skillSpriteRef = skillSpriteRef;
        this.playerShip = playerShip;
        this.gameLoop = gameLoop;
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

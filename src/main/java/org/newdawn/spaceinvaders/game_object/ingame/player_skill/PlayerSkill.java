package org.newdawn.spaceinvaders.game_object.ingame.player_skill;

import org.newdawn.spaceinvaders.game_object.ingame.store.IStoreItem;
import org.newdawn.spaceinvaders.game_object.visual.SpriteRenderer;
import org.newdawn.spaceinvaders.loop.GameLoop;
import org.newdawn.spaceinvaders.sprite.Sprite;

public abstract class PlayerSkill implements IStoreItem {
    private String skillName;
    private String skillSpriteRef;
    private GameLoop gameLoop;

    public PlayerSkill(String skillName, String skillSpriteRef, GameLoop gameLoop) {
        this.skillName = skillName;
        this.skillSpriteRef = skillSpriteRef;
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

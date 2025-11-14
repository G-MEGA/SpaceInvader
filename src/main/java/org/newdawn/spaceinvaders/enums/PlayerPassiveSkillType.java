package org.newdawn.spaceinvaders.enums;

import org.newdawn.spaceinvaders.sprite.SpriteRef;

public enum PlayerPassiveSkillType {
    FireSpeed("FireSpeed", SpriteRef.TEST_PASSIVE_SKILL_REF, 3),
    DamageUp("DamageUp", SpriteRef.TEST_PASSIVE_SKILL_REF, 5),
    AdditionalEngine("AdditionalEngine", SpriteRef.TEST_PASSIVE_SKILL_REF, 3),
    RepairKit("RepairKit", SpriteRef.TEST_PASSIVE_SKILL_REF, 3);

    private final String name;
    private final String spriteRef;
    private final int maxLevel;
    
    PlayerPassiveSkillType(String name, String spriteRef, int maxLevel) {
        this.name = name;
        this.spriteRef = spriteRef;
        this.maxLevel = maxLevel;
    }
    
    public String getName() { return name; }
    public String getSpriteRef() { return spriteRef; }
    public int getMaxLevel() { return maxLevel; }
}
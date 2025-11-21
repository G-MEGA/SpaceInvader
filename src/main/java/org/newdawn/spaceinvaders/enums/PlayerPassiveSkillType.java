package org.newdawn.spaceinvaders.enums;

import org.newdawn.spaceinvaders.sprite.SpriteRef;

public enum PlayerPassiveSkillType {
    FIRE_SPEED("FireSpeed", SpriteRef.TEST_PASSIVE_SKILL_REF, 3),
    DAMAGE_UP("DamageUp", SpriteRef.TEST_PASSIVE_SKILL_REF, 3),
    ADDITIONAL_ENGINE("AdditionalEngine", SpriteRef.TEST_PASSIVE_SKILL_REF, 3),
    REPAIR_KIT("RepairKit", SpriteRef.TEST_PASSIVE_SKILL_REF, 3);

    private final String name;
    private final String spriteRef;
    private final int price;
    
    PlayerPassiveSkillType(String name, String spriteRef, int price) {
        this.name = name;
        this.spriteRef = spriteRef;
        this.price = price;
    }
    
    public String getName() { return name; }
    public String getSpriteRef() { return spriteRef; }
    public int getPrice() { return price; }
}
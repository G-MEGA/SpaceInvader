package org.newdawn.spaceinvaders.enums;

public enum PlayerPassiveSkillType {
    FireSpeed("FireSpeed", "sprites/testPassiveSkill.png", 3),
    DamageUp("DamageUp", "sprites/testPassiveSkill.png", 5),
    AdditionalEngine("AdditionalEngine", "sprites/testPassiveSkill.png", 3),
    RepairKit("RepairKit", "sprites/testPassiveSkill.png", 3);

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

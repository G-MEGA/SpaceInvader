package org.newdawn.spaceinvaders.enums;

public enum PlayerPassiveSkillType {
    AttackSpeed("AttackSpeed", "sprites/testPassiveSkill.png", 3L),
    Penetrating("Penetrating", "sprites/testPassiveSkill.png", 3L),
    AdditionalEngine("AdditionalEngine", "sprites/testPassiveSkill.png", 3L),
    RepiarKit("RepairKit", "sprites/testPassiveSkill.png", 3L);

    private final String name;
    private final String spriteRef;
    private final Long maxLevel;
    
    PlayerPassiveSkillType(String name, String spriteRef, long maxLevel) {
        this.name = name;
        this.spriteRef = spriteRef;
        this.maxLevel = maxLevel;
    }
    
    public String getName() { return name; }
    public String getSpriteRef() { return spriteRef; }
    public Long getMaxLevel() { return maxLevel; }
}

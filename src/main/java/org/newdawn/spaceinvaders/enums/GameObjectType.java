package org.newdawn.spaceinvaders.enums;

public enum GameObjectType {
    // Enum constants with their associated string values
    Enemy("enemy"),
    Coin("coin"),
    Item("item"),
    Player("player"),
    Effect("effect"),
    Text("text");

    // Field to store the string value
    private final String value;

    // Constructor to initialize the string value
    GameObjectType(String value) {
        this.value = value;
    }

    // Public method to get the string value
    public String getValue() {
        return value;
    }
    
    // Convert string to enum (factory method)
    public static GameObjectType fromValue(String value) {
        for (GameObjectType type : values()) {
            if (type.value.equalsIgnoreCase(value)) { // 대소문자 구분 없음
                return type;
            }
        }
        throw new IllegalArgumentException(String.format("GameObjectType에는 '%s'라는 Type이 없습니다.", value));
    }
}
package org.newdawn.spaceinvaders.enums;

public enum GameObjectType {
    // Enum constants with their associated string values
    Enemy("enemy"),
    Coin("coin"),
    Item("item");

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
}
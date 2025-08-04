package com.terrescalmes.items;

public class StatModifier {

    private String type; // "damage", "range", "cooldown", "effect"
    private float value;
    private boolean isMultiplier;

    public StatModifier(String type, float value, boolean isMultiplier) {
        this.type = type;
        this.value = value;
        this.isMultiplier = isMultiplier;
    }

    public String getType() {
        return type;
    }

    public float getValue() {
        return value;
    }

    public boolean isMultiplier() {
        return isMultiplier;
    }
}

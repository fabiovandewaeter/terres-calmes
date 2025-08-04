package com.terrescalmes.items;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ItemId {

    IRON_ORE("iron_ore"),
    // weapons
    IRON_SWORD("iron_sword");

    private final String id;

    ItemId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static List<String> getAllIds() {
        return Arrays.stream(values())
                .map(ItemId::getId)
                .collect(Collectors.toList());
    }
}

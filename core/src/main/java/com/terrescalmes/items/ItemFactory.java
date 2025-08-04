package com.terrescalmes.items;

import java.util.List;

import com.terrescalmes.entities.attacks.MeleeAttack;
import com.terrescalmes.entities.attacks.effects.SinglePointEffect;

public class ItemFactory {

    private ItemFactory() {
    }

    public static Item createItem(ItemId itemId) {
        switch (itemId) {
            case IRON_ORE:
                return new Item(itemId, null);
            default:
                throw new IllegalArgumentException("Unknown item ID: " + itemId);
        }
    }

    public static Weapon createWeapon(ItemId itemId) {
        switch (itemId) {
            case IRON_SWORD:
                return new Weapon(
                        itemId,
                        null,
                        new MeleeAttack(1f, 0.2f, List.of(new SinglePointEffect(1))),
                        List.of(new StatModifier("damage", 1.0f, true)));
            default:
                throw new IllegalArgumentException("Unknown item ID: " + itemId);
        }
    }
}

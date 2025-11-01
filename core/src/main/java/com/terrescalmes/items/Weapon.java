package com.terrescalmes.items;

import java.util.List;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.entities.Entity;
import com.terrescalmes.entities.attacks.Attack;
import com.terrescalmes.util.Vector2I;

public class Weapon extends Item {

    private Attack attack;
    private List<StatModifier> statModifiers;

    public Weapon(ItemId itemId, TextureRegion icon, Attack attack, List<StatModifier> statModifiers) {
        super(itemId, icon);
        this.attack = attack;
        this.statModifiers = statModifiers;
    }

    public void attack(Entity source, Vector2I target) {
        attack.execute(source, target, statModifiers);
    }

    // update attack cooldown
    public void update(float delta) {
        attack.update(delta);
    }
}

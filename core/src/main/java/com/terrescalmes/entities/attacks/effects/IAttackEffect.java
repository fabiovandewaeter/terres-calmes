package com.terrescalmes.entities.attacks.effects;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.entities.Entity;
import com.terrescalmes.items.StatModifier;

public interface IAttackEffect {

    void trigger(Entity source, Vector2 position, List<StatModifier> statModifiers);

    int applyStatModifiers(List<StatModifier> statModifiers);
}

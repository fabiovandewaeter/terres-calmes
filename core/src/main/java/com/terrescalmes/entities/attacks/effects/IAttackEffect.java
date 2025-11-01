package com.terrescalmes.entities.attacks.effects;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.entities.Entity;
import com.terrescalmes.items.StatModifier;
import com.terrescalmes.util.Vector2I;

public interface IAttackEffect {

    void trigger(Entity source, Vector2I position, List<StatModifier> statModifiers);

    int applyStatModifiers(List<StatModifier> statModifiers);
}

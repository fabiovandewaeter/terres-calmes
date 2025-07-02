package com.terrescalmes.entities.attacks.effects;

import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.entities.Entity;

public interface AttackEffect {
    void trigger(Entity source, Vector2 position);
}

package com.terrescalmes.entities.attacks.effects;

import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.entities.attacks.Projectile;

public interface AttackEffect {
    void trigger(Projectile projectile, Vector2 position);
}

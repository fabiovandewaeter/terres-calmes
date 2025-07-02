package com.terrescalmes.entities.attacks;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.entities.Entity;
import com.terrescalmes.entities.EntityManager;
import com.terrescalmes.entities.attacks.effects.AttackEffect;

public class RangedAttack extends Attack {
    private float acceleration;

    public RangedAttack(float range, float cooldown, float acceleration, List<AttackEffect> hitEffects) {
        super(range, cooldown, hitEffects);
        this.acceleration = acceleration;
        this.hitEffects = hitEffects;
    }

    @Override
    public void execute(Entity source, Vector2 targetPos) {
        if (!canExecute()) {
            return;
        }

        Projectile projectile = new Projectile(
                null,
                source.getPosition().cpy(),
                targetPos,
                acceleration,
                range,
                source,
                hitEffects);
        EntityManager.getInstance().add(projectile);

        resetCooldown();
    }
}

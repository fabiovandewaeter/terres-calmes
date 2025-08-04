package com.terrescalmes.entities.attacks;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.entities.Entity;
import com.terrescalmes.entities.EntityManager;
import com.terrescalmes.entities.attacks.effects.IAttackEffect;
import com.terrescalmes.items.StatModifier;

public class RangedAttack extends Attack {

    private float acceleration;

    public RangedAttack(float range, float cooldown, float acceleration, List<IAttackEffect> hitEffects) {
        super(range, cooldown, hitEffects);
        this.acceleration = acceleration;
        this.hitEffects = hitEffects;
    }

    @Override
    public void execute(Entity source, Vector2 targetPos, List<StatModifier> statModifiers) {
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
                hitEffects, statModifiers);
        EntityManager.getInstance().add(projectile);

        resetCooldown();
    }
}

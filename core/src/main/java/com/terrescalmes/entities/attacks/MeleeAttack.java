package com.terrescalmes.entities.attacks;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.entities.Entity;
import com.terrescalmes.entities.attacks.effects.AttackEffect;

public class MeleeAttack extends Attack {

    public MeleeAttack(float range, float cooldown, List<AttackEffect> hitEffects) {
        super(range, cooldown, hitEffects);
    }

    @Override
    public void execute(Entity source, Vector2 targetPos) {
        System.out.println(cooldown + " " + cooldownCounter);
        if (!canExecute()) {
            return;
        }

        // checks if target is in range
        float distanceToTarget = source.getPosition().dst(targetPos);
        if (distanceToTarget > range) {
            return;
        }

        for (AttackEffect effect : hitEffects) {
            effect.trigger(source, targetPos);
        }

        resetCooldown();
    }
}

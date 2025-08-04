package com.terrescalmes.entities.attacks;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.items.StatModifier;
import com.terrescalmes.entities.Entity;
import com.terrescalmes.entities.attacks.effects.IAttackEffect;

public class MeleeAttack extends Attack {

    public MeleeAttack(float range, float cooldown, List<IAttackEffect> hitEffects) {
        super(range, cooldown, hitEffects);
    }

    @Override
    public void execute(Entity source, Vector2 targetPos, List<StatModifier> statModifiers) {
        if (!canExecute()) {
            return;
        }

        Vector2 sourcePos = source.getPosition();
        float distanceToTarget = sourcePos.dst(targetPos);

        Vector2 attackPosition;

        if (distanceToTarget > range) {
            // Calculer la direction vers la cible
            Vector2 direction = targetPos.cpy().sub(sourcePos).nor();

            // Créer une nouvelle position d'attaque à la portée maximale dans cette
            // direction
            attackPosition = sourcePos.cpy().add(direction.scl(range));
        } else {
            // La cible est à portée, attaquer à la position demandée
            attackPosition = targetPos.cpy();
        }

        for (IAttackEffect effect : hitEffects) {
            effect.trigger(source, attackPosition, statModifiers);
        }

        resetCooldown();
    }
}

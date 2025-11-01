package com.terrescalmes.entities.attacks;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.items.StatModifier;
import com.terrescalmes.util.Vector2I;
import com.terrescalmes.entities.Entity;
import com.terrescalmes.entities.EntityManager;
import com.terrescalmes.entities.attacks.effects.IAttackEffect;

public class MeleeAttack extends Attack {

    public MeleeAttack(int range, float cooldown, List<IAttackEffect> hitEffects) {
        super(range, cooldown, hitEffects);
    }

    @Override
    public void execute(Entity source, Vector2I targetPos, List<StatModifier> statModifiers) {
        if (!canExecute()) {
            return;
        }

        Vector2 sourcePos = source.getPosition().toVector2();
        float distanceToTarget = sourcePos.dst(targetPos.toVector2());

        Vector2 attackPosition;

        if (distanceToTarget > range) {
            // Calculer la direction vers la cible
            Vector2 direction = targetPos.toVector2().cpy().sub(sourcePos).nor();

            // Créer une nouvelle position d'attaque à la portée maximale dans cette
            // direction
            attackPosition = sourcePos.cpy().add(direction.scl(range));
        } else {
            // La cible est à portée, attaquer à la position demandée
            attackPosition = targetPos.cpy().toVector2();
        }
        // spawn visuel (juste le visuel)
        EntityManager.getInstance().spawnHitMarker(Vector2I.from(attackPosition));

        for (IAttackEffect effect : hitEffects) {
            effect.trigger(source, Vector2I.from(attackPosition), statModifiers);
        }

        resetCooldown();
    }
}

package com.terrescalmes.entities.attacks;

import java.util.List;
import java.util.Vector;

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

        Vector2I sourcePos = source.getPosition();
        // int manhattanDist = sourcePos.manhattanDistance(targetPos);
        int chebyshevDistance = sourcePos.chebyshevDistance(targetPos);

        Vector2I attackPosition;

        if (chebyshevDistance > range) {
            // Calculer la direction
            int dx = targetPos.x - sourcePos.x;
            int dy = targetPos.y - sourcePos.y;

            // Normaliser la direction pour qu'elle reste dans la range
            // En gardant le ratio entre dx et dy
            if (Math.abs(dx) > Math.abs(dy)) {
                attackPosition = new Vector2I(
                        sourcePos.x + (dx > 0 ? range : -range),
                        sourcePos.y + (int) Math.signum(dy) * Math.min(range, Math.abs(dy)));
            } else {
                attackPosition = new Vector2I(
                        sourcePos.x + (int) Math.signum(dx) * Math.min(range, Math.abs(dx)),
                        sourcePos.y + (dy > 0 ? range : -range));
            }
        } else {
            attackPosition = targetPos;
        }

        // spawn visuel (juste le visuel)
        EntityManager.getInstance().spawnHitMarker(attackPosition);

        for (IAttackEffect effect : hitEffects) {
            effect.trigger(source, attackPosition, statModifiers);
        }

        resetCooldown();
    }
}

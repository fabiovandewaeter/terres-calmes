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
        int manhattanDist = sourcePos.chebyshevDistance(targetPos);

        Vector2I attackPosition;

        if (manhattanDist > range) {
            // Calculer la direction avec la distance de Manhattan
            int dx = targetPos.x - sourcePos.x;
            int dy = targetPos.y - sourcePos.y;

            // Normaliser pour la distance de Manhattan
            int absDx = Math.abs(dx);
            int absDy = Math.abs(dy);

            if (absDx > absDy) {
                // Priorité horizontale
                attackPosition = new Vector2I(
                        sourcePos.x + (dx > 0 ? range : -range),
                        sourcePos.y);
            } else {
                // Priorité verticale
                attackPosition = new Vector2I(
                        sourcePos.x,
                        sourcePos.y + (dy > 0 ? range : -range));
            }
        } else {
            attackPosition = targetPos;
        }

        // spawn visuel (juste le visuel)
        System.out.println("execute() " + attackPosition + " " + manhattanDist + " " + range);
        EntityManager.getInstance().spawnHitMarker(attackPosition);

        for (IAttackEffect effect : hitEffects) {
            effect.trigger(source, attackPosition, statModifiers);
        }

        resetCooldown();
    }
}

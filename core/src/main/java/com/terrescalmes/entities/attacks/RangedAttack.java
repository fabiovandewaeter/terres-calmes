package com.terrescalmes.entities.attacks;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.CollisionManager;
import com.terrescalmes.entities.Entity;
import com.terrescalmes.entities.EntityManager;
import com.terrescalmes.entities.attacks.effects.IAttackEffect;
import com.terrescalmes.items.StatModifier;
import com.terrescalmes.util.Vector2I;

public class RangedAttack extends Attack {

    private float acceleration;

    public RangedAttack(int range, float cooldown, float acceleration, List<IAttackEffect> hitEffects) {
        super(range, cooldown, hitEffects);
        this.acceleration = acceleration;
        this.hitEffects = hitEffects;
    }

    @Override
    public void execute(Entity source, Vector2I targetPos, List<StatModifier> statModifiers) {
        if (!canExecute()) {
            return;
        }

        // Tir instantané avec raycast case par case
        Vector2I hitPosition = raycastToTarget(source, targetPos);
        // spawn visuel (juste le visuel)
        EntityManager.getInstance().spawnHitMarker(hitPosition);

        // Vérifier si on a touché une entité
        Entity hitEntity = findEntityAtPosition(hitPosition, source);

        if (hitEntity != null) {
            // Appliquer les effets sur l'entité touchée
            for (IAttackEffect effect : hitEffects) {
                effect.trigger(source, hitPosition, statModifiers);
            }

            // Gérer le kill si l'entité meurt
            if (hitEntity.isDead()) {
                source.onKill(hitEntity);
            }

            System.out.println("YES " + hitPosition);
        } else {
            // Tir dans le vide ou obstacle - déclencher les effets quand même
            for (IAttackEffect effect : hitEffects) {
                effect.trigger(source, hitPosition, statModifiers);
            }
            System.out.println("NO" + hitPosition);
        }

        resetCooldown();
    }

    /**
     * Effectue un raycast case par case de la source vers la cible
     * S'arrête au premier obstacle (mur ou entité)
     * Utilise l'algorithme de Bresenham pour parcourir les cases
     * 
     * @return La position où le tir s'arrête
     */
    private Vector2I raycastToTarget(Entity source, Vector2I targetPos) {
        Vector2I start = source.getPosition().cpy();
        Vector2 direction = targetPos.cpy().sub(start).toVector2();
        float distance = direction.len();

        // Limiter à la portée maximale
        if (distance > range) {
            direction.nor().scl(range);
            distance = range;
        }

        Vector2 end = start.toVector2().cpy().add(direction);

        // Convertir en coordonnées de cases (tiles)
        int x0 = Math.round(start.x);
        int y0 = Math.round(start.y);
        int x1 = Math.round(end.x);
        int y1 = Math.round(end.y);

        // Algorithme de Bresenham pour parcourir les cases
        return bresenhamRaycast(x0, y0, x1, y1, source);
    }

    /**
     * Parcourt les cases selon l'algorithme de Bresenham
     * S'arrête au premier obstacle rencontré
     */
    private Vector2I bresenhamRaycast(int x0, int y0, int x1, int y1, Entity source) {
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;

        int x = x0;
        int y = y0;

        CollisionManager collisionManager = CollisionManager.getInstance();

        while (true) {
            Vector2I currentPos = new Vector2I(x, y);

            // Ignorer la case de départ (position de l'attaquant)
            if (x != x0 || y != y0) {
                // Vérifier s'il y a un obstacle (mur)
                if (!collisionManager.allowMove(source, currentPos)) {
                    // Obstacle rencontré - retourner la position juste avant
                    return currentPos;
                }

                // Vérifier s'il y a une entité ennemie
                Entity entityAtPos = findEntityAtPosition(currentPos, source);
                if (entityAtPos != null) {
                    // Entité touchée
                    return currentPos;
                }
            }

            // Fin du raycast
            if (x == x1 && y == y1) {
                return new Vector2I(x1, y1);
            }

            // Étape suivante de Bresenham
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x += sx;
            }
            if (e2 < dx) {
                err += dx;
                y += sy;
            }
        }
    }

    /**
     * Trouve une entité à une position donnée (autre que la source)
     */
    private Entity findEntityAtPosition(Vector2I position, Entity source) {
        EntityManager entityManager = EntityManager.getInstance();

        for (Entity entity : entityManager.getEntities()) {
            if (entity == source) {
                continue; // Ignorer la source
            }

            if (entity.isDead()) {
                continue; // Ignorer les entités mortes
            }

            // Vérifier si l'entité est sur cette case (distance < 1 case)
            if (entity.getPosition().dst(position.toVector2()) < 1.0f) {
                // Vérifier si c'est un ennemi
                if (!entity.getFaction().equals(source.getFaction())) {
                    return entity;
                }
            }
        }

        return null;
    }
}

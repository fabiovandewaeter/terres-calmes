package com.terrescalmes.entities.attacks;

import java.util.List;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
// import com.terrescalmes.IsometricCameraManager;
import com.terrescalmes.CollisionManager;
import com.terrescalmes.TopDownCameraManager;
import com.terrescalmes.entities.Entity;
import com.terrescalmes.entities.attacks.effects.IAttackEffect;
import com.terrescalmes.items.StatModifier;

public class Projectile extends Entity {

    private static final int MAX_HP = 10;

    private Vector2 target;
    private float range;
    private Vector2 startPosition;
    public Entity source;
    private List<IAttackEffect> hitEffects;
    private List<StatModifier> statModifiers;

    public Projectile(TextureRegion textureRegion, Vector2 position, Vector2 target, float acceleration, float range,
            Entity source, List<IAttackEffect> hitEffects, List<StatModifier> statModifiers) {
        super(textureRegion, position, MAX_HP, acceleration, Entity.DEFAULT_SIZE, Entity.DEFAULT_SIZE,
                source.getFaction());
        this.target = target;
        this.range = range;
        this.source = source;
        this.hitEffects = hitEffects;
        startPosition = position.cpy(); // Position initiale pour le calcul de la distance
        this.statModifiers = statModifiers;
    }

    @Override
    public void update(float delta) {
        if (HP <= 0)
            return; // Ne pas mettre à jour si le projectile est détruit

        if (moveTo(target, delta)) {
            triggerHitEffects();
            HP = 0;
            return;
        }

        // Vérifier la portée maximale
        if (position.dst(startPosition) > range) {
            triggerHitEffects();
            HP = 0;
            return;
        }

        super.update(delta); // Met à jour la hitbox
    }

    // does the same than Entity.moveTo() but returns true when there is a collision
    // so Projectile can explode
    @Override
    public boolean moveTo(Vector2 target, float delta) {
        Vector2 direction = target.cpy().sub(position);
        float distanceToTarget = direction.len();

        if (distanceToTarget == 0) {
            return true;
        }

        // Utilisation de la normalisation isométrique
        Vector2 moveVector = TopDownCameraManager.normalize(direction, acceleration * delta);
        float moveLength = moveVector.len();

        if (moveLength == 0) {
            return false; // Aucun déplacement nécessaire
        }

        Vector2 targetPosition;
        boolean reachedTarget = false;

        if (moveLength >= distanceToTarget) {
            targetPosition = target.cpy();
            reachedTarget = true;
        } else {
            targetPosition = position.cpy().add(moveVector);
        }

        // Vérifier si le mouvement est possible (pas d'obstacle)
        if (!CollisionManager.getInstance().allowMove(this, targetPosition)) {
            // Le projectile est bloqué par un obstacle
            triggerHitEffects();
            HP = 0;
            return false; // Mouvement bloqué
        }

        // Mettre à jour la position
        position.set(targetPosition);

        // Retourner true seulement si on a atteint exactement la cible
        return reachedTarget && position.equals(target);
    }

    @Override
    public void handleCollision(Entity other) {
        if (other != source && !other.getFaction().equals(faction)) { // Évite de toucher la source
            triggerHitEffects();
            HP = 0; // Détruit le projectile après collision
        }
        if (other.isDead()) {
            source.onKill(other);
        }
    }

    private void triggerHitEffects() {
        for (IAttackEffect effect : hitEffects) {
            effect.trigger(source, position, statModifiers);
        }
    }
}

package com.terrescalmes.entities.attacks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.entities.Entity;

public class Projectile extends Entity {
    private final Vector2 target;
    private final float speed;
    private final float range;
    private final Vector2 startPosition;
    private final int damage;
    private final Entity source;

    public Projectile(TextureRegion textureRegion, Vector2 position, Vector2 target, float speed, float range,
            int damage, Entity source) {
        super(textureRegion, position);
        this.target = target;
        this.speed = speed;
        this.range = range;
        this.damage = damage;
        this.source = source;
        this.startPosition = position.cpy(); // Position initiale pour le calcul de la distance
    }

    @Override
    public void update(float delta) {
        if (HP <= 0)
            return; // Ne pas mettre à jour si le projectile est détruit

        // Calculer la direction vers la cible
        Vector2 direction = target.cpy().sub(position);
        float distanceToTarget = direction.len();

        // Vérifier si la cible est atteinte
        if (distanceToTarget < speed * delta) {
            position.set(target);
            HP = 0; // Détruire le projectile
        } else {
            // Normaliser et déplacer
            direction.nor().scl(speed * delta);
            position.add(direction);
        }

        // Vérifier la portée maximale
        if (position.dst(startPosition) > range) {
            HP = 0; // Détruire si la portée est dépassée
        }

        super.update(delta); // Met à jour la hitbox
    }

    @Override
    public void handleCollision(Entity other) {
        if (other != source) { // Évite de toucher la source
            other.takeDamage(damage);
            HP = 0; // Détruit le projectile après collision
        }
    }
}

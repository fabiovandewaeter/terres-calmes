package com.terrescalmes.entities.attacks;

import java.util.List;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.entities.Entity;
import com.terrescalmes.entities.attacks.effects.AttackEffect;

public class Projectile extends Entity {
    private final Vector2 target;
    private final float range;
    private final Vector2 startPosition;
    public final Entity source;
    private List<AttackEffect> hitEffects;

    public Projectile(TextureRegion textureRegion, Vector2 position, Vector2 target, float acceleration, float range,
            Entity source, List<AttackEffect> hitEffects) {
        super(textureRegion, position, acceleration, source.getFaction());
        this.target = target;
        this.range = range;
        this.source = source;
        this.hitEffects = hitEffects;
        startPosition = position.cpy(); // Position initiale pour le calcul de la distance
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
        for (AttackEffect effect : hitEffects) {
            effect.trigger(source, position);
        }
    }
}

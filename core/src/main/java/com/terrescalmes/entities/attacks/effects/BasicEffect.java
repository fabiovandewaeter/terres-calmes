package com.terrescalmes.entities.attacks.effects;

import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.entities.Entity;
import com.terrescalmes.entities.EntityManager;

public class BasicEffect implements AttackEffect {
    private int damage;

    public BasicEffect(int damage) {
        this.damage = damage;
    }

    @Override
    public void trigger(Entity source, Vector2 position) {
        Entity target = EntityManager.getInstance().getEntityAt(position.x, position.y);

        if (target != null && !target.equals(source) && !target.getFaction().equals(source.getFaction())) {
            target.takeDamage(damage);
            if (target.isDead()) {
                source.onKill(target);
            }
        }
    }
}

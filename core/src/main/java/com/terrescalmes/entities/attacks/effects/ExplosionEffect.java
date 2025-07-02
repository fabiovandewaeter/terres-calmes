package com.terrescalmes.entities.attacks.effects;

import java.util.List;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.entities.Entity;
import com.terrescalmes.entities.EntityManager;

public class ExplosionEffect implements AttackEffect {
    private float radius;
    private int damage;

    public ExplosionEffect(float radius, int damage) {
        this.radius = radius;
        this.damage = damage;
    }

    @Override
    public void trigger(Entity source, Vector2 position) {
        Circle explosionCircle = new Circle(position.x, position.y, radius);
        List<Entity> entities = EntityManager.getInstance().getEntitiesInCircle(explosionCircle);
        for (Entity entity : entities) {
            if (!entity.equals(source) && !entity.getFaction().equals(source.getFaction())) {
                entity.takeDamage(damage);
                if (entity.isDead()) {
                    source.onKill(entity);
                }
            }
        }
    }
}

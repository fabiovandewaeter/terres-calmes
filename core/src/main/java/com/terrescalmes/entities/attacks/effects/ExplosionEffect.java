package com.terrescalmes.entities.attacks.effects;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.EntityManager;
import com.terrescalmes.entities.Entity;
import com.terrescalmes.entities.attacks.Projectile;

public class ExplosionEffect implements AttackEffect {
    private float radius;
    private int damage;

    public ExplosionEffect(float radius, int damage) {
        this.radius = radius;
        this.damage = damage;
    }

    @Override
    public void trigger(Projectile projectile, Vector2 position) {
        List<Entity> entities = EntityManager.getInstance().getEntitiesInRadius(position, radius);
        for (Entity entity : entities) {
            if (!entity.equals(projectile.source) && !entity.equals(projectile)) {
                entity.takeDamage(damage);
            }
        }
    }
}

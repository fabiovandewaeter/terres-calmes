package com.terrescalmes.entities.attacks;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.EntityManager;
import com.terrescalmes.entities.Entity;

public class RangedAttack implements Attack {
    private int damage;
    private float range;
    // in ms
    private int cooldown;
    private float speed;

    public RangedAttack(int damage, float range, int cooldown, float speed) {
        this.damage = damage;
        this.range = range;
        this.cooldown = cooldown;
        this.speed = speed;
    }

    @Override
    public void execute(Entity source, Vector2 targetPos) {
        Projectile projectile = new Projectile(
                null,
                source.position.cpy(),
                targetPos,
                speed,
                range,
                damage,
                source);
        EntityManager.getInstance().add(projectile);
    }

    @Override
    public boolean update(float delta) {
        // projectiles updated by EntityManager like every Entity
        return true;
    }

    @Override
    public void render(SpriteBatch batch) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'render'");
    }
}

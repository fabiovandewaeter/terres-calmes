package com.terrescalmes.entities.attacks;

import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.entities.Entity;
import com.terrescalmes.entities.EntityManager;
import com.terrescalmes.entities.attacks.effects.AttackEffect;

public class RangedAttack implements Attack {
    private float range;
    // in seconds
    private float cooldown;
    private float acceleration;
    private List<AttackEffect> hitEffects;

    public RangedAttack(float range, float cooldown, float acceleration, List<AttackEffect> hitEffects) {
        this.range = range;
        this.cooldown = cooldown;
        this.acceleration = acceleration;
        this.hitEffects = hitEffects;
    }

    @Override
    public void execute(Entity source, Vector2 targetPos) {
        Projectile projectile = new Projectile(
                null,
                source.getPosition().cpy(),
                targetPos,
                acceleration,
                range,
                source,
                hitEffects);
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

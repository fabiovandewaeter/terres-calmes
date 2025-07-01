package com.terrescalmes.entities.attacks.effects;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.EntityManager;
import com.terrescalmes.entities.Entity;
import com.terrescalmes.entities.attacks.RangedAttack;

class ExplosionEffectTest {
    private static final float FIXED_DELTA = 0.016f; // 60 FPS

    @BeforeEach
    void setUp() {
        EntityManager.reset();
    }

    @Test
    void explosionHitEntityInRadius() {
        // create attack
        int damage = 50000;
        int cooldown = 1000;
        float range = 20f;
        float acceleration = 100f;
        List<AttackEffect> attackEffects = new ArrayList<>();
        attackEffects.add(new ExplosionEffect(2f, damage));
        RangedAttack attack = new RangedAttack(range, cooldown, acceleration, attackEffects);
        Vector2 sourceEntityPos = new Vector2(0, 0);
        Vector2 targetEntityPos = new Vector2(3f, 5);
        Vector2 target = new Vector2(5, 5);

        // create entity
        Entity sourceEntity = new Entity(null, sourceEntityPos, 2f);
        Entity targetEntity = new Entity(null, targetEntityPos, 2f);
        EntityManager entityManager = EntityManager.getInstance();
        entityManager.add(sourceEntity);
        entityManager.add(targetEntity);

        assertEquals(2, entityManager.entities.size());

        // should have reached the target in less than 100 updates
        attack.execute(sourceEntity, target);
        for (int i = 0; i < 100; i++) {
            entityManager.update(FIXED_DELTA);
        }

        assertEquals(1, entityManager.entities.size());
    }

    @Test
    void explosionHitAllEntitiesInRadius() {
        // create attack
        int damage = 50000;
        int cooldown = 1000;
        float range = 20f;
        float acceleration = 100f;
        List<AttackEffect> attackEffects = new ArrayList<>();
        attackEffects.add(new ExplosionEffect(2f, damage));
        RangedAttack attack = new RangedAttack(range, cooldown, acceleration, attackEffects);
        Vector2 sourceEntityPos = new Vector2(0, 0);
        Vector2 targetEntityPos1 = new Vector2(3f, 5);
        Vector2 targetEntityPos2 = new Vector2(5f, 3);
        Vector2 target = new Vector2(5, 5);

        // create entity
        Entity sourceEntity = new Entity(null, sourceEntityPos, 2f);
        Entity targetEntity1 = new Entity(null, targetEntityPos1, 2f);
        Entity targetEntity2 = new Entity(null, targetEntityPos2, 2f);
        EntityManager entityManager = EntityManager.getInstance();
        entityManager.add(sourceEntity);
        entityManager.add(targetEntity1);
        entityManager.add(targetEntity2);

        assertEquals(3, entityManager.entities.size());

        // should have reached the target in less than 100 updates
        attack.execute(sourceEntity, target);
        for (int i = 0; i < 100; i++) {
            entityManager.update(FIXED_DELTA);
        }

        assertEquals(1, entityManager.entities.size());
    }

    @Test
    void explosionOnlyHitEntitiesInRadius() {
        // create attack
        int damage = 50000;
        int cooldown = 1000;
        float range = 20f;
        float acceleration = 100f;
        List<AttackEffect> attackEffects = new ArrayList<>();
        attackEffects.add(new ExplosionEffect(2f, damage));
        RangedAttack attack = new RangedAttack(range, cooldown, acceleration, attackEffects);
        Vector2 sourceEntityPos = new Vector2(0, 0);
        Vector2 targetEntityPos1 = new Vector2(3f, 5);
        Vector2 targetEntityPos2 = new Vector2(10f, 3);
        Vector2 target = new Vector2(5, 5);

        // create entity
        Entity sourceEntity = new Entity(null, sourceEntityPos, 2f);
        Entity targetEntity1 = new Entity(null, targetEntityPos1, 2f);
        Entity targetEntity2 = new Entity(null, targetEntityPos2, 2f);
        EntityManager entityManager = EntityManager.getInstance();
        entityManager.add(sourceEntity);
        entityManager.add(targetEntity1);
        entityManager.add(targetEntity2);

        assertEquals(3, entityManager.entities.size());

        // should have reached the target in less than 100 updates
        attack.execute(sourceEntity, target);
        for (int i = 0; i < 100; i++) {
            entityManager.update(FIXED_DELTA);
        }

        assertEquals(2, entityManager.entities.size());
    }
}

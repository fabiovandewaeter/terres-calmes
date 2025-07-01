package com.terrescalmes.entities.attacks;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.EntityManager;
import com.terrescalmes.entities.Entity;

class RangedAttackTest {
    private static final float FIXED_DELTA = 0.016f; // 60 FPS

    @BeforeEach
    void setUp() {
        EntityManager.reset();
    }

    @Test
    void attackReachTargetAndDealDamage() {
        // create attack
        int damage = 10;
        int cooldown = 1000;
        float range = 20f;
        float speed = 100f;
        RangedAttack attack = new RangedAttack(damage, range, cooldown, speed);
        Vector2 source = new Vector2(0, 0);
        Vector2 target = new Vector2(5, 5);

        // create entity
        Entity sourceEntity = new Entity(null, source);
        Entity targetEntity = new Entity(null, target);
        EntityManager entityManager = EntityManager.getInstance();
        // entityManager.add(sourceEntity);
        entityManager.add(targetEntity);

        int baseHP = targetEntity.HP;

        // should have reached the target in less than 100 updates
        attack.execute(sourceEntity, target);
        for (int i = 0; i < 100; i++) {
            entityManager.update(FIXED_DELTA);
        }

        int newHP = targetEntity.HP;

        assertEquals(baseHP - damage, newHP);
    }

    @Test
    void attackDeletedWhenOutOfRange() {
    }

    @Test
    void testCooldown() {
    }
}

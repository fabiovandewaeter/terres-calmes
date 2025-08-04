package com.terrescalmes.entities.attacks;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.entities.Entity;
import com.terrescalmes.entities.EntityManager;
import com.terrescalmes.entities.attacks.effects.SinglePointEffect;

class MeleeAttackTest {
    private static final float FIXED_DELTA = 0.016f; // 60 FPS

    @BeforeEach
    void setUp() {
        EntityManager.reset();
    }

    @Test
    void attackReachTargetIfInRangeAndGoodOrientationEvenIfClickIsOutOfRange() {
        // create attack
        int damage = 10;
        Attack attack = new MeleeAttack(1f, 0.2f, List.of(new SinglePointEffect(damage)));
        Vector2 sourceEntityPos = new Vector2(0, 0);
        // targetEntity is in range and in front of the sourceEntity
        Vector2 targetEntityPos = new Vector2(1, 0);
        // attack really far but in the direction of the targetEntity
        Vector2 target = new Vector2(5, 0);

        // create entity
        Entity sourceEntity = new Entity(null, sourceEntityPos, 100, 2f);
        Entity targetEntity = new Entity(null, targetEntityPos, 100, 2f, Entity.DEFAULT_SIZE, Entity.DEFAULT_SIZE,
                "Enemies");
        EntityManager entityManager = EntityManager.getInstance();
        entityManager.add(sourceEntity);
        entityManager.add(targetEntity);

        int baseHP = targetEntity.getHP();

        // should have reached the target in less than 100 updates
        attack.execute(sourceEntity, target, null);
        for (int i = 0; i < 100; i++) {
            entityManager.update(FIXED_DELTA);
        }

        int newHP = targetEntity.getHP();

        assertEquals(baseHP - damage, newHP);
    }

    @Test
    void attackDoesNotReachTargetIfInRangeButNotGoodOrientationEvenIfClickIsOutOfRange() {
        // create attack
        int damage = 10;
        Attack attack = new MeleeAttack(1f, 0.2f, List.of(new SinglePointEffect(damage)));
        Vector2 sourceEntityPos = new Vector2(0, 0);
        // targetEntity is in range and in front of the sourceEntity
        Vector2 targetEntityPos = new Vector2(0, 1);
        // attack really far but in the direction of the targetEntity
        Vector2 target = new Vector2(5, 0);

        // create entity
        Entity sourceEntity = new Entity(null, sourceEntityPos, 100, 2f);
        Entity targetEntity = new Entity(null, targetEntityPos, 100, 2f, Entity.DEFAULT_SIZE, Entity.DEFAULT_SIZE,
                "Enemies");
        EntityManager entityManager = EntityManager.getInstance();
        entityManager.add(sourceEntity);
        entityManager.add(targetEntity);

        int baseHP = targetEntity.getHP();

        // should have reached the target in less than 100 updates
        attack.execute(sourceEntity, target, null);
        for (int i = 0; i < 100; i++) {
            entityManager.update(FIXED_DELTA);
        }

        int newHP = targetEntity.getHP();

        assertEquals(baseHP, newHP);
    }
}

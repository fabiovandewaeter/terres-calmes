package com.terrescalmes.entities.attacks;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.EntityManager;
import com.terrescalmes.entities.Entity;
import com.terrescalmes.entities.attacks.effects.AttackEffect;
import com.terrescalmes.entities.attacks.effects.ExplosionEffect;

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
        float acceleration = 100f;
        List<AttackEffect> attackEffects = new ArrayList<>();
        attackEffects.add(new ExplosionEffect(2.5f, damage));
        RangedAttack attack = new RangedAttack(range, cooldown, acceleration, attackEffects);
        Vector2 sourceEntityPos = new Vector2(0, 0);
        Vector2 targetEntityPos = new Vector2(5, 5);

        // create entity
        Entity sourceEntity = new Entity(null, sourceEntityPos, 2f);
        Entity targetEntity = new Entity(null, targetEntityPos, 2f);
        EntityManager entityManager = EntityManager.getInstance();
        // entityManager.add(sourceEntity);
        entityManager.add(targetEntity);

        int baseHP = targetEntity.HP;

        // should have reached the target in less than 100 updates
        attack.execute(sourceEntity, targetEntityPos);
        for (int i = 0; i < 100; i++) {
            entityManager.update(FIXED_DELTA);
        }

        int newHP = targetEntity.HP;

        assertEquals(baseHP - damage, newHP);
    }

    @Test
    void attackDeletedWhenOutOfRange() {
        // create attack
        int damage = 10;
        int cooldown = 1000;
        float range = 20f;
        float acceleration = 100f;
        List<AttackEffect> attackEffects = new ArrayList<>();
        attackEffects.add(new ExplosionEffect(2.5f, damage));
        RangedAttack attack = new RangedAttack(range, cooldown, acceleration, attackEffects);
        Vector2 sourceEntityPos = new Vector2(0, 0);
        Vector2 targetEntityPos = new Vector2(50, 5);

        // create entity
        Entity sourceEntity = new Entity(null, sourceEntityPos, 2f);
        Entity targetEntity = new Entity(null, targetEntityPos, 2f);
        EntityManager entityManager = EntityManager.getInstance();
        // entityManager.add(sourceEntity);
        entityManager.add(targetEntity);

        int baseHP = targetEntity.HP;

        // should have reached the target in less than 100 updates
        attack.execute(sourceEntity, targetEntityPos);
        for (int i = 0; i < 100; i++) {
            entityManager.update(FIXED_DELTA);
        }

        int newHP = targetEntity.HP;

        assertEquals(baseHP, newHP);
    }
}

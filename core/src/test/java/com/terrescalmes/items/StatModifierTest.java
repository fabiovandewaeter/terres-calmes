package com.terrescalmes.items;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.entities.Entity;
import com.terrescalmes.entities.EntityManager;
import com.terrescalmes.entities.attacks.Attack;
import com.terrescalmes.entities.attacks.MeleeAttack;
import com.terrescalmes.entities.attacks.effects.SinglePointEffect;

public class StatModifierTest {
    private static final float FIXED_DELTA = 0.016f; // 60 FPS

    @BeforeEach
    void setUp() {
        EntityManager.reset();
    }

    @Test
    void statModifierChangeDamagesAppliedToTheTarget() {
        // create attack
        int damage = 10;
        Attack attack = new MeleeAttack(10f, 0.2f, List.of(new SinglePointEffect(damage)));
        List<StatModifier> statModifiers = List.of(new StatModifier("damage", 2, true));
        Vector2 sourceEntityPos = new Vector2(0, 0);
        Vector2 targetEntityPos = new Vector2(1, 1);

        // create entity
        Entity sourceEntity = new Entity(null, sourceEntityPos, 100, 2f);
        Entity targetEntity = new Entity(null, targetEntityPos, 100, 2f, Entity.DEFAULT_SIZE, Entity.DEFAULT_SIZE,
                "Enemies");
        EntityManager entityManager = EntityManager.getInstance();
        entityManager.add(sourceEntity);
        entityManager.add(targetEntity);

        int baseHP = targetEntity.getHP();

        // should have reached the target in less than 100 updates
        attack.execute(sourceEntity, targetEntityPos, statModifiers);
        for (int i = 0; i < 100; i++) {
            entityManager.update(FIXED_DELTA);
        }

        int newHP = targetEntity.getHP();

        assertEquals(baseHP - damage * 2, newHP);
    }
}

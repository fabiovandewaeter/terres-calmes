package com.terrescalmes.entities.attacks;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.EntityManager;
import com.terrescalmes.entities.Entity;
import com.terrescalmes.entities.attacks.RangedAttack;;

class RangedAttackTest {

    void update() {

    }

    @BeforeEach
    void setUp() {
        EntityManager.getInstance().entities.clear();
    }

    @Test
    void attackReachTargetAndDealDamage() {
        // create attack
        int damage = 10;
        int cooldown = 1000;
        float range = 20f;
        float speed = 2f;
        RangedAttack attack = new RangedAttack(damage, range, cooldown, speed);
        Vector2 source = new Vector2(0, 0);
        Vector2 target = new Vector2(5, 5);

        // create entity
        Entity sourceEntity = new Entity(null, source);
        Entity targetEntity = new Entity(null, target);

        int baseHP = targetEntity.HP;

        // should have reach the target in less than 100 updates
        attack.execute(sourceEntity, target);
        int loops = 100;
        while (loops > 0 && !attack.update(Gdx.graphics.getDeltaTime())) {
            loops++;
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

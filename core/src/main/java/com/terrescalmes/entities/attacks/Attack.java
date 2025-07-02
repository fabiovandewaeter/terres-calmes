package com.terrescalmes.entities.attacks;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.entities.Entity;
import com.terrescalmes.entities.attacks.effects.AttackEffect;

public abstract class Attack {
    protected float range;
    protected float cooldown;
    protected float cooldownCounter = 0f;
    protected List<AttackEffect> hitEffects;

    public Attack(float range, float cooldown, List<AttackEffect> hitEffects) {
        this.range = range;
        this.cooldown = cooldown;
        this.hitEffects = hitEffects;
    }

    // start the attack
    public abstract void execute(Entity source, Vector2 targetPos);

    // returns true if the attack finished
    public boolean update(float delta) {
        cooldownCounter = Math.max(0f, cooldownCounter - delta);
        return updateAttack(delta);
    }

    // updates the logic of the attack ; returns true when attack finished
    protected boolean updateAttack(float delta) {
        return true;
    }

    public boolean canExecute() {
        return cooldownCounter <= 0f;
    }

    public void resetCooldown() {
        cooldownCounter = cooldown;
    }

    public float getCooldown() {
        return cooldown;
    }

    public float getRemainingCooldown() {
        return cooldownCounter;
    }

}

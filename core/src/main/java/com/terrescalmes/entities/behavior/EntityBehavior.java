package com.terrescalmes.entities.behavior;

import com.terrescalmes.entities.Entity;

public abstract class EntityBehavior {

    protected Entity entity;
    protected EntityState currentState;
    protected float stateTimer;

    public EntityBehavior(Entity entity) {
        this.entity = entity;
        this.currentState = EntityState.IDLE;
        this.stateTimer = 0f;
    }

    public abstract void update(float delta);

    public abstract void changeState(EntityState newState);

    public EntityState getCurrentState() {
        return currentState;
    }

    protected void setState(EntityState newState) {
        if (currentState != newState) {
            onStateExit(currentState);
            currentState = newState;
            stateTimer = 0f;
            onStateEnter(newState);
        }
    }

    protected void onStateEnter(EntityState state) {
        // Override dans les sous-classes si nécessaire
    }

    protected void onStateExit(EntityState state) {
        // Override dans les sous-classes si nécessaire
    }
}

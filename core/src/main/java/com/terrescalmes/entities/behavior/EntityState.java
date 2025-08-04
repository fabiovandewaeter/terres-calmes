package com.terrescalmes.entities.behavior;

public enum EntityState {
    IDLE, // L'entité ne fait rien
    PURSUING, // L'entité poursuit le joueur
    ATTACKING, // L'entité attaque
    FLEEING, // L'entité fuit (si blessée)
    PATROLLING, // L'entité patrouille
    STUNNED // L'entité est étourdie
}

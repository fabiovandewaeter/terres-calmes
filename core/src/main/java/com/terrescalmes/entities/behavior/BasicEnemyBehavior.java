package com.terrescalmes.entities.behavior;

import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.entities.Entity;
import com.terrescalmes.entities.EntityManager;
import com.terrescalmes.entities.Player;
import com.terrescalmes.entities.attacks.Attack;

public class BasicEnemyBehavior extends EntityBehavior {

    private static final float ATTACK_RANGE = 1.0f;
    private static final float PURSUIT_RANGE = 8.0f;
    private static final float FLEE_HEALTH_THRESHOLD = 0.2f;
    private static final float ATTACK_COOLDOWN = 1.5f;
    private static final float STUN_DURATION = 1.0f;

    private float attackTimer;
    private Vector2 lastKnownPlayerPosition;
    private Vector2 patrolTarget;
    private Vector2 originalPosition;

    public BasicEnemyBehavior(Entity entity) {
        super(entity);
        this.attackTimer = 0f;
        this.lastKnownPlayerPosition = null;
        this.originalPosition = entity.getPosition().cpy();
        this.patrolTarget = null;
    }

    @Override
    public void update(float delta) {
        stateTimer += delta;
        attackTimer += delta;

        Player player = EntityManager.getInstance().getPlayer();
        if (player == null) {
            setState(EntityState.IDLE);
            return;
        }

        float distanceToPlayer = entity.getPosition().dst(player.getPosition());
        float healthPercentage = (float) entity.getHP() / entity.getMaxHP();

        // Transition d'états basée sur les conditions
        switch (currentState) {
            case IDLE:
                if (distanceToPlayer <= PURSUIT_RANGE) {
                    setState(EntityState.PURSUING);
                } else if (stateTimer > 3f) { // Après 3 secondes d'inactivité, commencer à patrouiller
                    setState(EntityState.PATROLLING);
                }
                break;

            case PURSUING:
                if (healthPercentage < FLEE_HEALTH_THRESHOLD) {
                    setState(EntityState.FLEEING);
                } else if (distanceToPlayer <= ATTACK_RANGE) {
                    setState(EntityState.ATTACKING);
                } else if (distanceToPlayer > PURSUIT_RANGE * 1.5f) {
                    setState(EntityState.IDLE);
                }
                break;

            case ATTACKING:
                if (healthPercentage < FLEE_HEALTH_THRESHOLD) {
                    setState(EntityState.FLEEING);
                } else if (distanceToPlayer > ATTACK_RANGE * 1.2f) {
                    setState(EntityState.PURSUING);
                }
                break;

            case FLEEING:
                if (healthPercentage > FLEE_HEALTH_THRESHOLD || distanceToPlayer > PURSUIT_RANGE) {
                    setState(EntityState.IDLE);
                }
                break;

            case PATROLLING:
                if (distanceToPlayer <= PURSUIT_RANGE) {
                    setState(EntityState.PURSUING);
                }
                break;

            case STUNNED:
                if (stateTimer > STUN_DURATION) {
                    setState(EntityState.IDLE);
                }
                break;
        }

        // Exécuter le comportement de l'état actuel
        executeCurrentState(delta, player);
    }

    private void executeCurrentState(float delta, Player player) {
        switch (currentState) {
            case IDLE:
                // Ne rien faire, juste rester en place
                break;

            case PURSUING:
                lastKnownPlayerPosition = player.getPosition().cpy();
                entity.moveTo(player.getPosition(), delta);
                break;

            case ATTACKING:
                // Attaquer le joueur s'il est à portée et que le cooldown est écoulé
                if (attackTimer >= ATTACK_COOLDOWN) {
                    performAttack(player);
                    attackTimer = 0f;
                }
                break;

            case FLEEING:
                // Fuir dans la direction opposée au joueur
                Vector2 fleeDirection = entity.getPosition().cpy().sub(player.getPosition()).nor();
                Vector2 fleeTarget = entity.getPosition().cpy().add(fleeDirection.scl(2f));
                entity.moveTo(fleeTarget, delta);
                break;

            case PATROLLING:
                patrol(delta);
                break;

            case STUNNED:
                // Ne rien faire pendant qu'on est étourdi
                break;
        }
    }

    private void performAttack(Player player) {
        // Infliger des dégâts au joueur
        if (!entity.getAttacks().isEmpty()) {
            Attack meleeAttack = entity.getAttacks().get(0);
            meleeAttack.execute(entity, player.getPosition(), null);
        }

        // Tu peux aussi ajouter des effets visuels ou sonores ici
        System.out.println("Ennemi attaque le joueur !");
    }

    private void patrol(float delta) {
        // Si on n'a pas de cible de patrouille, en choisir une
        if (patrolTarget == null || entity.getPosition().dst(patrolTarget) < 0.5f) {
            // Choisir une nouvelle position aléatoire autour de la position d'origine
            float angle = (float) (Math.random() * Math.PI * 2);
            float radius = 2f + (float) (Math.random() * 3f);
            patrolTarget = new Vector2(
                    originalPosition.x + (float) Math.cos(angle) * radius,
                    originalPosition.y + (float) Math.sin(angle) * radius);
        }

        entity.moveTo(patrolTarget, delta);
    }

    @Override
    public void changeState(EntityState newState) {
        setState(newState);
    }

    @Override
    protected void onStateEnter(EntityState state) {
        switch (state) {
            case ATTACKING:
                // Réinitialiser le timer d'attaque pour permettre une attaque immédiate
                attackTimer = ATTACK_COOLDOWN;
                break;
            case FLEEING:
                System.out.println("Ennemi fuit !");
                break;
            case PURSUING:
                System.out.println("Ennemi poursuit le joueur !");
                break;
        }
    }

    // Méthode pour étourdir l'entité (peut être appelée depuis l'extérieur)
    public void stun() {
        setState(EntityState.STUNNED);
    }
}

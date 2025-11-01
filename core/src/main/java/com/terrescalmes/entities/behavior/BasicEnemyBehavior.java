package com.terrescalmes.entities.behavior;

import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.entities.Entity;
import com.terrescalmes.entities.EntityManager;
import com.terrescalmes.entities.Player;
import com.terrescalmes.entities.attacks.Attack;
import com.terrescalmes.util.Vector2I;

public class BasicEnemyBehavior extends EntityBehavior {

    private static final int ATTACK_RANGE = 1; // Distance en cases (1 = adjacent)
    private static final int PURSUIT_RANGE = 8; // Distance en cases
    private static final float FLEE_HEALTH_THRESHOLD = 0.2f;
    private static final float ATTACK_COOLDOWN = 1.5f;
    private static final float STUN_DURATION = 1.0f;
    private static final float PATHFINDING_UPDATE_INTERVAL = 0.5f; // Recalculer le chemin toutes les 0.5s
    private static final int MAX_PATHFINDING_ITERATIONS = 500;

    private float attackTimer;
    private float pathfindingTimer;
    private Vector2I lastKnownPlayerPosition;
    private Vector2I patrolTarget;
    private Vector2I originalPosition;
    private Vector2I lastTargetPosition; // Pour détecter si le joueur a bougé

    private static final float DECISION_COOLDOWN = 0.5f;
    private float decisionTimer = 0f;

    public BasicEnemyBehavior(Entity entity) {
        super(entity);
        this.attackTimer = 0f;
        this.pathfindingTimer = 0f;
        this.lastKnownPlayerPosition = null;
        this.originalPosition = entity.getPosition().cpy();
        this.patrolTarget = null;
        this.lastTargetPosition = null;
    }

    @Override
    public void update(float delta) {
        // Timers
        attackTimer += delta;
        pathfindingTimer += delta;
        decisionTimer -= delta;

        Player player = EntityManager.getInstance().getPlayer();

        // Déterminer l'état en fonction de la distance joueur/ennemi
        if (player != null) {
            // int dist = entity.getPosition().manhattanDistance(player.getPosition());
            int dist = entity.getPosition().chebyshevDistance(player.getPosition());

            if (dist <= ATTACK_RANGE) {
                changeState(EntityState.ATTACKING);
            } else if (dist <= PURSUIT_RANGE) {
                changeState(EntityState.PURSUING);
            } else {
                // Si on n'a rien d'autre à faire, patrouille
                if (currentState != EntityState.PATROLLING && currentState != EntityState.FLEEING) {
                    changeState(EntityState.PATROLLING);
                }
            }
        } else {
            changeState(EntityState.PATROLLING);
        }

        // Ne pas prendre des décisions trop souvent
        if (decisionTimer <= 0f && entity.canMove()) {
            executeCurrentState(delta, player);
            decisionTimer = DECISION_COOLDOWN;
        } else {
            // Même si on n'exécute pas une nouvelle décision, gérer timers pour
            // attaques/stun
            if (currentState == EntityState.ATTACKING) {
                attackBehavior(delta, player);
            }
        }
    }

    private void executeCurrentState(float delta, Player player) {
        switch (currentState) {
            case IDLE:
                // Ne rien faire, juste rester en place
                entity.stopMovement();
                break;

            case PURSUING:
                pursueBehavior(delta, player);
                break;

            case ATTACKING:
                attackBehavior(delta, player);
                break;

            case FLEEING:
                fleeBehavior(delta, player);
                break;

            case PATROLLING:
                patrolBehavior(delta);
                break;

            case STUNNED:
                // Ne rien faire pendant qu'on est étourdi
                entity.stopMovement();
                break;
        }
    }

    private void pursueBehavior(float delta, Player player) {
        if (player == null)
            return;

        lastKnownPlayerPosition = player.getPosition().cpy();

        boolean playerMoved = lastTargetPosition == null || !lastTargetPosition.equals(player.getPosition());

        // Recalculer le chemin seulement si nécessaire
        if (playerMoved || pathfindingTimer >= PATHFINDING_UPDATE_INTERVAL || !entity.isMoving()) {

            // On cherche d'abord une case adjacente au joueur où on peut aller
            boolean movedToAdjacent = tryMoveToReachableAdjacentToPlayer(player);

            if (!movedToAdjacent) {
                // Si aucune case adjacente n'est accessible, on essaie de se rapprocher du
                // joueur
                Vector2 direction = player.getPosition().toVector2().sub(entity.getPosition().toVector2()).nor();
                entity.moveInDirection(Vector2I.from(direction));
            }

            lastTargetPosition = player.getPosition().cpy();
            pathfindingTimer = 0f;
        }
    }

    /**
     * Tente, dans l'ordre des cases les plus proches de l'ennemi, de pathfinder
     * vers une case
     * adjacente au joueur. Retourne true si un déplacement via pathfinding a été
     * lancé.
     */
    private boolean tryMoveToReachableAdjacentToPlayer(Player player) {
        Vector2I playerPos = player.getPosition();
        Vector2I enemyPos = entity.getPosition();

        // Directions 4-way (on peut ajouter les diagonales si le jeu les autorise)
        Vector2I[] adj = {
                new Vector2I(1, 0), new Vector2I(-1, 0), new Vector2I(0, 1), new Vector2I(0, -1)
        };

        // Construire la liste de candidats et trier par distance manhattan depuis
        // l'ennemi
        java.util.List<Vector2I> candidates = new java.util.ArrayList<>();
        for (Vector2I d : adj) {
            candidates.add(new Vector2I(playerPos.x + d.x, playerPos.y + d.y));
        }

        // candidates.sort((a, b) -> Integer.compare(a.manhattanDistance(enemyPos),
        // b.manhattanDistance(enemyPos)));
        candidates.sort((a, b) -> Integer.compare(a.chebyshevDistance(enemyPos), b.chebyshevDistance(enemyPos)));

        // Essayer chaque case : si moveToPosition renvoie true, le pathfinding a été
        // lancé avec succès
        for (Vector2I target : candidates) {
            // IMPORTANT : si vous avez des méthodes pour vérifier "walkable" ou "occupied"
            // -> utilisez-les ici
            boolean started = entity.moveToPosition(target, MAX_PATHFINDING_ITERATIONS);
            if (started)
                return true;
        }

        return false;
    }

    private void attackBehavior(float delta, Player player) {
        // Si on a perdu le joueur, repasser en poursuite
        if (player == null) {
            changeState(EntityState.PURSUING);
            return;
        }

        // int dist = entity.getPosition().manhattanDistance(player.getPosition());
        int dist = entity.getPosition().chebyshevDistance(player.getPosition());
        if (dist > ATTACK_RANGE) {
            // Le joueur s'est éloigné : poursuivre
            changeState(EntityState.PURSUING);
            return;
        }

        // Arrêter le mouvement pour attaquer
        entity.stopMovement();

        // Attaquer le joueur s'il est à portée et que le cooldown est écoulé
        if (attackTimer >= ATTACK_COOLDOWN) {
            performAttack(player);
            attackTimer = 0f;
        }
    }

    private void fleeBehavior(float delta, Player player) {
        if (player == null)
            return;

        // Recalculer la direction de fuite périodiquement
        if (!entity.isMoving() || pathfindingTimer >= PATHFINDING_UPDATE_INTERVAL) {
            // Direction opposée au joueur
            Vector2 fleeDirection = entity.getPosition().toVector2().sub(player.getPosition().toVector2()).nor();

            // Position de fuite à 5 cases de distance
            Vector2I fleeTarget = entity.getPosition().cpy().add(Vector2I.from(fleeDirection.scl(5f)));

            // Essayer d'aller à cette position
            boolean success = entity.moveToPosition(fleeTarget, MAX_PATHFINDING_ITERATIONS);

            if (!success) {
                // Si le pathfinding échoue, essayer de bouger dans la direction opposée
                entity.moveInDirection(Vector2I.from(fleeDirection));
            }

            pathfindingTimer = 0f;
        }
    }

    private void patrolBehavior(float delta) {
        // Si on n'a pas de cible de patrouille ou qu'on l'a atteinte
        if (patrolTarget == null || !entity.isMoving()) {
            // Choisir une nouvelle position aléatoire autour de la position d'origine
            float angle = (float) (Math.random() * Math.PI * 2);
            float radius = 2f + (float) (Math.random() * 3f);

            patrolTarget = new Vector2I(
                    Math.round(originalPosition.x + (float) Math.cos(angle) * radius),
                    Math.round(originalPosition.y + (float) Math.sin(angle) * radius));

            // Aller à cette position avec pathfinding
            entity.moveToPosition(patrolTarget, MAX_PATHFINDING_ITERATIONS);
        }
    }

    private void performAttack(Player player) {
        // Infliger des dégâts au joueur
        if (!entity.getAttacks().isEmpty()) {
            Attack meleeAttack = entity.getAttacks().get(0);
            meleeAttack.execute(entity, player.getPosition(), null);
        }

        System.out.println("Ennemi attaque le joueur !");
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
                // Arrêter le mouvement pour attaquer
                entity.stopMovement();
                break;
            case FLEEING:
                System.out.println("Ennemi fuit !");
                pathfindingTimer = PATHFINDING_UPDATE_INTERVAL; // Forcer le recalcul immédiat
                break;
            case PURSUING:
                System.out.println("Ennemi poursuit le joueur !");
                lastTargetPosition = null; // Réinitialiser pour forcer le calcul de chemin
                pathfindingTimer = PATHFINDING_UPDATE_INTERVAL;
                break;
            case PATROLLING:
                patrolTarget = null; // Réinitialiser pour choisir une nouvelle cible
                break;
            case IDLE:
                entity.stopMovement();
                break;
            case STUNNED:
                entity.stopMovement();
                break;
        }
    }

    // Méthode pour étourdir l'entité (peut être appelée depuis l'extérieur)
    public void stun() {
        setState(EntityState.STUNNED);
    }
}

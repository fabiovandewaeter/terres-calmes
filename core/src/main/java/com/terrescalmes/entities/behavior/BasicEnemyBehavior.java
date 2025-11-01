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
        decisionTimer -= delta;

        // Ne prendre des décisions que périodiquement et seulement si l'ennemi peut
        // bouger
        if (decisionTimer <= 0 && entity.canMove()) {
            Player player = EntityManager.getInstance().getPlayer();
            if (player != null) {
                // Calculer la direction vers le joueur
                Vector2I direction = calculateDirectionToPlayer(player);

                // Essayer de se déplacer vers le joueur
                if (!entity.moveInDirection(direction)) {
                    // Si le mouvement est bloqué, essayer une direction alternative
                    tryAlternativeDirections();
                }
            }

            decisionTimer = DECISION_COOLDOWN;
        }
    }

    private Vector2I calculateDirectionToPlayer(Player player) {
        Vector2I playerPos = player.getPosition();
        Vector2I enemyPos = entity.getPosition();

        int dx = Integer.compare(playerPos.x, enemyPos.x);
        int dy = Integer.compare(playerPos.y, enemyPos.y);

        // Prioriser le déplacement selon l'axe le plus éloigné
        if (Math.abs(playerPos.x - enemyPos.x) > Math.abs(playerPos.y - enemyPos.y)) {
            return new Vector2I(dx, 0);
        } else {
            return new Vector2I(0, dy);
        }
    }

    private void tryAlternativeDirections() {
        Vector2I[] directions = {
                new Vector2I(1, 0), new Vector2I(-1, 0),
                new Vector2I(0, 1), new Vector2I(0, -1)
        };

        for (Vector2I dir : directions) {
            if (entity.moveInDirection(dir)) {
                break;
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
        lastKnownPlayerPosition = player.getPosition().cpy();

        // Vérifier si le joueur a bougé ou si c'est le moment de recalculer le chemin
        boolean playerMoved = lastTargetPosition == null ||
                !lastTargetPosition.toVector2().epsilonEquals(player.getPosition().toVector2(), 0.5f);

        if (playerMoved || pathfindingTimer >= PATHFINDING_UPDATE_INTERVAL) {
            // Si l'entité n'est pas déjà en mouvement ou si le joueur a bougé
            if (!entity.isMoving() || playerMoved) {
                // Arrondir la position du joueur à la case la plus proche
                Vector2I targetCell = new Vector2I(
                        Math.round(player.getPosition().x),
                        Math.round(player.getPosition().y));

                // Lancer le pathfinding
                boolean pathFound = entity.moveToPosition(targetCell, MAX_PATHFINDING_ITERATIONS);

                if (!pathFound) {
                    // Si pas de chemin trouvé, essayer de se rapprocher en ligne droite
                    Vector2 direction = player.getPosition().toVector2().cpy().sub(entity.getPosition().toVector2())
                            .nor();
                    entity.moveInDirection(Vector2I.from(direction));
                }

                lastTargetPosition = player.getPosition().cpy();
                pathfindingTimer = 0f;
            }
        }
    }

    private void attackBehavior(float delta, Player player) {
        // Arrêter le mouvement pour attaquer
        entity.stopMovement();

        // Attaquer le joueur s'il est à portée et que le cooldown est écoulé
        if (attackTimer >= ATTACK_COOLDOWN) {
            performAttack(player);
            attackTimer = 0f;
        }
    }

    private void fleeBehavior(float delta, Player player) {
        // Recalculer la direction de fuite périodiquement
        if (!entity.isMoving() || pathfindingTimer >= PATHFINDING_UPDATE_INTERVAL) {
            // Direction opposée au joueur
            Vector2 fleeDirection = entity.getPosition().toVector2().cpy().sub(player.getPosition().toVector2()).nor();

            // Position de fuite à 5 cases de distance
            Vector2I fleeTarget = entity.getPosition().cpy().add(Vector2I.from(fleeDirection.scl(5f)));

            // Arrondir à la case la plus proche
            fleeTarget.x = Math.round(fleeTarget.x);
            fleeTarget.y = Math.round(fleeTarget.y);

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

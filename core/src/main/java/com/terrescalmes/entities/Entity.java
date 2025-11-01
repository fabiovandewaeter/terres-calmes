package com.terrescalmes.entities;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.CollisionManager;
import com.terrescalmes.TopDownCameraManager;
import com.terrescalmes.entities.attacks.Attack;
import com.terrescalmes.entities.attacks.MeleeAttack;
import com.terrescalmes.entities.behavior.BasicEnemyBehavior;
import com.terrescalmes.entities.behavior.EntityBehavior;
import com.terrescalmes.items.Weapon;
import com.terrescalmes.util.Vector2I;
import com.terrescalmes.entities.attacks.effects.IAttackEffect;
import com.terrescalmes.entities.attacks.effects.SinglePointEffect;

public class Entity {

    public static final float DEFAULT_SIZE = 1.0f;
    public static final String DEFAULT_FACTION = "";
    // private static final float CELL_MOVE_SPEED = 5f; // Vitesse de déplacement
    // entre cases
    private static final float MOVE_COOLDOWN = 0.2f; // Temps minimum entre deux mouvements

    private TextureRegion textureRegion;
    protected Vector2I position;
    protected float acceleration;
    protected boolean isSprinting;
    protected int maxHP;
    protected int HP;
    protected float hitboxSize;
    protected float renderingSize;
    protected Rectangle hitbox;
    protected Rectangle screenBounds;
    protected List<Attack> attacks;
    protected String faction;
    protected int xp;
    protected EntityBehavior behavior;
    protected Weapon weapon;

    // Système de mouvement case par case
    protected Vector2I targetCell;
    protected boolean isMoving;
    protected List<Vector2I> path; // Chemin à suivre
    protected int currentPathIndex; // Index de la prochaine case dans le chemin
    protected float moveTimer; // Timer pour limiter la vitesse

    public Entity(TextureRegion textureRegion, Vector2I position, int maxHP, float acceleration) {
        this(textureRegion, position, maxHP, acceleration, DEFAULT_SIZE, DEFAULT_SIZE, DEFAULT_FACTION);
    }

    public Entity(TextureRegion textureRegion, Vector2I position, int maxHP, float acceleration, float hitboxSize,
            float renderingSize, String faction) {
        this.textureRegion = textureRegion;
        this.position = position;
        this.acceleration = acceleration;
        isSprinting = false;
        this.maxHP = maxHP;
        HP = maxHP;
        this.hitboxSize = hitboxSize;
        this.renderingSize = renderingSize;
        hitbox = new Rectangle(position.x, position.y, hitboxSize, hitboxSize);
        screenBounds = new Rectangle();
        this.faction = faction;
        attacks = new ArrayList<>();
        xp = 0;

        // Initialisation du système de mouvement
        targetCell = null;
        isMoving = false;
        path = null;
        currentPathIndex = 0;
        moveTimer = 0f;

        updateWorldBounds();
        if ("Enemies".equals(faction)) {
            this.behavior = new BasicEnemyBehavior(this);
            fillEnemyAttacks();
        }
    }

    private void fillEnemyAttacks() {
        List<IAttackEffect> attackEffects = new ArrayList<>();
        attackEffects.add(new SinglePointEffect(15));
        Attack meleeAttack = new MeleeAttack(1, 1.5f, attackEffects);
        attacks.add(meleeAttack);
    }

    public void update(float delta) {
        hitbox.setPosition(position.x, position.y);

        // Mise à jour du timer de mouvement
        if (moveTimer > 0) {
            moveTimer -= delta;
            if (moveTimer < 0) {
                moveTimer = 0f;
            }
        }

        // Mise à jour du mouvement case par case
        updateCellMovement(delta);

        // update cooldowns and attack logic
        for (Attack attack : attacks) {
            attack.update(delta);
        }

        if (behavior != null) {
            behavior.update(delta);
        }

        if (weapon != null) {
            weapon.update(delta);
        }
    }

    /**
     * Gère le mouvement case par case (DISCRET - pas d'interpolation flottante
     * ici).
     * Lorsqu'une targetCell est définie, l'entité saute sur la case cible dans
     * la prochaine mise à jour, puis passe à la suivante si un chemin est défini.
     */
    protected void updateCellMovement(float delta) {
        if (!isMoving || targetCell == null || moveTimer > 0) {
            return;
        }

        // Déplacer instantanément vers la case cible
        position.x = targetCell.x;
        position.y = targetCell.y;

        // Mettre à jour la hitbox immédiatement
        hitbox.setPosition(position.x, position.y);

        float cooldown = MOVE_COOLDOWN;
        if (acceleration > 0f) {
            cooldown = MOVE_COOLDOWN / acceleration;
        }
        moveTimer = cooldown;

        // Si on suit un chemin, passer à la case suivante (mais ne pas l'appliquer
        // tout de suite dans le même update pour éviter de traverser plusieurs cases en
        // un frame)
        if (path != null && currentPathIndex < path.size() - 1) {
            currentPathIndex++;
            Vector2I next = path.get(currentPathIndex);
            targetCell = new Vector2I(next.x, next.y);
        } else {
            stopMovement();
        }
    }

    /**
     * Déplace l'entité d'une case dans une direction cardinale
     * 
     * @param direction Direction normalisée (1,0), (-1,0), (0,1) ou (0,-1)
     * @return true si le mouvement a été initié, false si bloqué
     */
    public boolean moveInDirection(Vector2I direction) {
        if (isMoving || moveTimer > 0) {
            return false; // Déjà en mouvement
        }

        // Calculer la case cible
        int targetCellX = position.x + direction.x;
        int targetCellY = position.y + direction.y;

        Vector2I potentialTarget = new Vector2I(targetCellX, targetCellY);

        // Vérifier si le mouvement est possible
        if (CollisionManager.getInstance().allowMove(this, potentialTarget)) {
            targetCell = potentialTarget;
            isMoving = true;
            return true;
        } else {
            System.out.println("Mouvement bloqué vers (" + targetCellX + ", " + targetCellY + ")");
            return false;
        }
    }

    /**
     * Déplace l'entité vers une position avec pathfinding
     * 
     * @param destination   Position de destination
     * @param maxIterations Nombre max d'itérations pour A* (1000 recommandé)
     * @return true si un chemin a été trouvé, false sinon
     */
    public boolean moveToPosition(Vector2I destination, int maxIterations) {
        if (isMoving || moveTimer > 0) {
            System.out.println("Entité déjà en mouvement ou en cooldown");
            return false;
        }

        // Vérifier si on est déjà à destination
        if (position.equals(destination)) {
            return true;
        }

        List<Vector2I> foundPath = PathfindingManager.getInstance().findPath(
                this,
                position,
                destination,
                maxIterations);

        if (foundPath == null || foundPath.size() <= 1) {
            System.out.println("Impossible d'atteindre (" + destination.x + ", " + destination.y + ")");
            return false;
        }

        // Démarrer le suivi du chemin
        path = foundPath;
        currentPathIndex = 1;
        targetCell = path.get(currentPathIndex).cpy();
        isMoving = true;

        System.out.println("Chemin trouvé: " + path.size() + " cases");
        return true;
    }

    /**
     * Arrête le mouvement en cours
     */
    public void stopMovement() {
        isMoving = false;
        targetCell = null;
        path = null;
        currentPathIndex = 0;
    }

    private void updateWorldBounds() {
        Vector2 displayPos = TopDownCameraManager.gameToDisplayCoordinates(position.toVector2());
        float w = renderingSize * TopDownCameraManager.CUBE_WIDTH;
        float h = renderingSize * TopDownCameraManager.CUBE_HEIGHT;
        float x = displayPos.x;
        float y = displayPos.y;
        screenBounds.set(x, y, w, h);
    }

    public void render(SpriteBatch batch) {
        updateWorldBounds();
        if (textureRegion != null) {
            batch.draw(
                    textureRegion,
                    screenBounds.x, screenBounds.y,
                    screenBounds.width, screenBounds.height);
        }
    }

    public void renderHitbox(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        batch.end();
        Vector2 hitboxDisplayPos = TopDownCameraManager.gameToDisplayCoordinates(new Vector2(hitbox.x, hitbox.y));
        float displayWidth = hitbox.width * TopDownCameraManager.CUBE_WIDTH;
        float displayHeight = hitbox.height * TopDownCameraManager.CUBE_HEIGHT;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 0, 0, 1);
        shapeRenderer.rect(hitboxDisplayPos.x, hitboxDisplayPos.y, displayWidth, displayHeight);
        shapeRenderer.end();
        batch.begin();
    }

    public void takeDamage(int amount) {
        System.out.println("takeDamage(): " + amount);
        HP = Math.max(0, HP - amount);
    }

    public boolean collide(Vector2I position) {
        return hitbox.contains(position.x, position.y);
    }

    public boolean collide(Entity other) {
        return hitbox.overlaps(other.hitbox);
    }

    public void handleCollision(Entity other) {
        // TODO
    }

    public boolean isDead() {
        return HP <= 0;
    }

    public void onKill(Entity victim) {
        if (!victim.getFaction().equals(faction)) {
            xp += victim.xpDrop();
        }
        System.out.println("Xp: " + xp);
    }

    public int xpDrop() {
        return 100;
    }

    // getters
    public Vector2I getPosition() {
        return position;
    }

    public boolean isSprinting() {
        return isSprinting;
    }

    public int getHP() {
        return HP;
    }

    public int getMaxHP() {
        return maxHP;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public float getHitboxSize() {
        return hitboxSize;
    }

    public Rectangle getScreenBounds() {
        updateWorldBounds();
        return screenBounds;
    }

    public List<Attack> getAttacks() {
        return attacks;
    }

    public float getAcceleration() {
        return acceleration;
    }

    public String getFaction() {
        return faction;
    }

    public int getXp() {
        return xp;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public List<Vector2I> getCurrentPath() {
        return path;
    }

    public boolean canMove() {
        return !isMoving && moveTimer <= 0;
    }

    public float getMoveCooldown() {
        return MOVE_COOLDOWN;
    }

    public float getMoveTimer() {
        return moveTimer;
    }
}
